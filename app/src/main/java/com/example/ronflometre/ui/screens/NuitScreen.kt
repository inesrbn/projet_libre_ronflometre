package com.example.ronflometre.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ronflometre.ui.screens.sommeil.ModeAlarme
import com.example.ronflometre.ui.theme.RonflometreTheme

// Écran de la nuit en cours
@Composable
fun NuitScreen(
    heure: Int,
    minute: Int,
    mode: ModeAlarme,
    onArreter: () -> Unit
) {
    val heureTexte = "%02d:%02d".format(heure, minute)
    val texteAlarme = when (mode) {
        ModeAlarme.FENETRE -> "Réveil autour de $heureTexte"
        ModeAlarme.FIXE -> "Alarme à $heureTexte"
        ModeAlarme.AUCUNE -> "Pas d'alarme"
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bonne nuit", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text(text = texteAlarme)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onArreter) { Text("Arrêter") }
    }
}

@Preview(showBackground = true)
@Composable
fun NuitScreenPreview() {
    RonflometreTheme {
        NuitScreen(heure = 7, minute = 30, mode = ModeAlarme.FIXE, onArreter = {})
    }
}