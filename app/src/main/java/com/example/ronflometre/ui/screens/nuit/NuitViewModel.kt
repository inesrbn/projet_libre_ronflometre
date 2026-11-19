package com.example.ronflometre.ui.screens.nuit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ronflometre.data.NuitRepository
import com.example.ronflometre.ui.screens.sommeil.ModeAlarme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Crée la nuit dans Room au démarrage et la termine à l'arrêt
class NuitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NuitRepository(application)

    // id de la nuit en cours dans Room (null tant qu'elle n'est pas créée)
    var nuitId: Long? = null
        private set

    // la création de la nuit
    private var creation: Job? = null

    fun commencer(heure: Int, minute: Int, mode: ModeAlarme) {
        if (creation != null) return     // déjà commencée
        val heureAlarme = if (mode == ModeAlarme.AUCUNE) null else heure * 60 + minute
        creation = viewModelScope.launch {
            nuitId = repository.commencerNuit(heureAlarme, mode.name)
        }
    }

    // termine la nuit puis prévient l'écran (pour aller au Journal)
    fun arreter(onTermine: () -> Unit) {
        viewModelScope.launch {
            creation?.join()// attend que la nuit soit bien créée
            nuitId?.let { repository.terminerNuit(it) }
            onTermine()
        }
    }
}
