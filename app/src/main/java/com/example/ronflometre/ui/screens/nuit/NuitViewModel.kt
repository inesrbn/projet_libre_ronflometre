package com.example.ronflometre.ui.screens.nuit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ronflometre.audio.AudioRecorder
import com.example.ronflometre.data.NuitRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NuitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NuitRepository(application)
    private val audioRecorder = AudioRecorder(application)

    val volumeDb: StateFlow<Float> = audioRecorder.volumeDb
    val estEnEcoute: StateFlow<Boolean> = audioRecorder.estEnEcoute

    // id de la nuit en cours dans Room (null tant qu'elle n'est pas créée)
    var nuitId: Long? = null
        private set

    // la création de la nuit
    private var creation: Job? = null

    fun commencer() {
        if (creation != null) return     // déjà commencée
        creation = viewModelScope.launch {
            nuitId = repository.commencerNuit()
        }
    }

    fun demarrerEcoute(): Boolean {
        return audioRecorder.demarrer(viewModelScope)
    }

    // termine la nuit, arrête la capture micro puis prévient l'écran (pour aller au Journal)
    fun arreter(onTermine: () -> Unit) {
        audioRecorder.arreter()
        viewModelScope.launch {
            creation?.join() // attend que la nuit soit bien créée
            nuitId?.let { repository.terminerNuit(it) }
            onTermine()
        }
    }

    override fun onCleared() {
        audioRecorder.arreter()
    }
}
