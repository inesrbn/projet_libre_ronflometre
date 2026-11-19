package com.example.ronflometre.ui.screens.journal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ronflometre.data.Evenement
import com.example.ronflometre.data.Nuit
import com.example.ronflometre.data.NuitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Tout ce que l'écran Journal affiche
data class JournalUiState(
    val chargement: Boolean = true,
    val nuit: Nuit? = null,
    val evenements: List<Evenement> = emptyList()
) {
    // durée de la nuit en millisecondes
    val dureeNuitMs: Long get() = nuit?.let { (it.fin ?: it.debut) - it.debut } ?: 0L

    // seulement les ronflements
    val ronflements: List<Evenement> get() = evenements.filter { it.type == Evenement.RONFLEMENT }

    // temps total de ronflement
    val dureeRonflementMs: Long get() = ronflements.sumOf { it.dureeMs }

    // nombre de ronflements par heure de nuit
    val tauxParHeure: Double
        get() {
            val heures = dureeNuitMs / 3_600_000.0
            return if (heures > 0) ronflements.size / heures else 0.0
        }
}

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NuitRepository(application)

    // la dernière nuit, puis ses événements
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<JournalUiState> = repository.derniereNuit()
        .flatMapLatest { nuit ->
            if (nuit == null) flowOf(JournalUiState(chargement = false))
            else repository.evenements(nuit.id).map { evts ->
                JournalUiState(chargement = false, nuit = nuit, evenements = evts)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), JournalUiState())

    // bouton de test : une fausse nuit pour voir le Journal rempli
    fun creerNuitDeDemo() {
        viewModelScope.launch { repository.creerNuitDeDemo() }
    }
}
