package com.example.ronflometre.audio

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.log10
import kotlin.math.sqrt

// Capture audio avec AudioRecord
class AudioRecorder(private val context: Context) {

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    private val _volumeDb = MutableStateFlow(0f)
    val volumeDb: StateFlow<Float> = _volumeDb.asStateFlow()

    private val _estEnEcoute = MutableStateFlow(false)
    val estEnEcoute: StateFlow<Boolean> = _estEnEcoute.asStateFlow()

    companion object {
        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    @SuppressLint("MissingPermission")
    fun demarrer(scope: CoroutineScope): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        if (_estEnEcoute.value) return true

        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
        val bufferSize = maxOf(minBufferSize, SAMPLE_RATE * 2)

        val record = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            record.release()
            return false
        }

        audioRecord = record
        record.startRecording()
        _estEnEcoute.value = true

        recordingJob = scope.launch(Dispatchers.IO) {
            val buffer = ShortArray(1024)
            while (isActive && _estEnEcoute.value) {
                val readCount = record.read(buffer, 0, buffer.size)
                if (readCount > 0) {
                    val db = calculerVolumeDb(buffer, readCount)
                    _volumeDb.value = db
                }
            }
        }
        return true
    }

    fun arreter() {
        _estEnEcoute.value = false
        recordingJob?.cancel()
        recordingJob = null
        try {
            audioRecord?.stop()
        } catch (_: Exception) {
        }
        audioRecord?.release()
        audioRecord = null
        _volumeDb.value = 0f
    }

    private fun calculerVolumeDb(buffer: ShortArray, readCount: Int): Float {
        var sumSquare = 0.0
        for (i in 0 until readCount) {
            val sample = buffer[i].toDouble()
            sumSquare += sample * sample
        }
        val meanSquare = sumSquare / readCount
        val rms = sqrt(meanSquare)

        if (rms <= 0.0) return 0f
        val db = 20 * log10(rms)
        // Échelle relative de 0 à 90 dB
        return (db - 10).toFloat().coerceIn(0f, 90f)
    }
}
