package com.example.ronflometre.ui.screens.sommeil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class ModeAlarme {
    // les 3 modes (réveil dans une fenêtre, alarme fixe, pas d'alarme)
    FENETRE,
    FIXE,
    AUCUNE
}

class SommeilViewModel : ViewModel() {

    var mode by mutableStateOf(ModeAlarme.FIXE)
        private set
    var heure by mutableIntStateOf(7)
        private set

    var minute by mutableIntStateOf(30)
        private set


    fun augmenterHeure() { heure = (heure + 1) % 24 }
    fun diminuerHeure() { heure = (heure - 1 + 24) % 24 }
    fun augmenterMinute() { minute = (minute + 5) % 60 }
    fun diminuerMinute() { minute = (minute - 5 + 60) % 60 }
    fun choisirMode(nouveau: ModeAlarme) { mode = nouveau }

}