package com.example.ronflometre.ui.screens.sommeil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ronflometre.ui.components.ChoixMode
import com.example.ronflometre.ui.components.SelecteurHeure
import com.example.ronflometre.ui.theme.RonflometreTheme

@Composable
fun SommeilScreen(modifier: Modifier = Modifier, onDemarrer: (heure: Int, minute: Int, mode: ModeAlarme) -> Unit, viewModel: SommeilViewModel = viewModel()) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sommeil")
        Spacer(Modifier.height(24.dp))

        SelecteurHeure(
            heure = viewModel.heure,
            minute = viewModel.minute,
            onHeurePlus = { viewModel.augmenterHeure() },
            onHeureMoins = { viewModel.diminuerHeure() },
            onMinutePlus = { viewModel.augmenterMinute() },
            onMinuteMoins = { viewModel.diminuerMinute() }
        )

        ChoixMode(
            heure = viewModel.heure,
            minute = viewModel.minute,
            modeInitial = viewModel.mode,
            onModeChoisi = { viewModel.choisirMode(it) }
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = { onDemarrer(viewModel.heure, viewModel.minute, viewModel.mode) }) { Text("Démarrer") }
    }
}

@Preview(showBackground = true)
@Composable
fun SommeilScreenPreview() {
    RonflometreTheme { SommeilScreen(onDemarrer = { _, _, _ -> }) }
}