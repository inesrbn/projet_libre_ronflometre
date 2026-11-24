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
import com.example.ronflometre.ui.theme.RonflometreTheme

// Écran Sommeil : un seul bouton pour commencer la nuit
@Composable
fun SommeilScreen(modifier: Modifier = Modifier, onDemarrer: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sommeil")
        Spacer(Modifier.height(24.dp))
        Button(onClick = onDemarrer) { Text("Démarrer") }
    }
}

@Preview(showBackground = true)
@Composable
fun SommeilScreenPreview() {
    RonflometreTheme { SommeilScreen(onDemarrer = {}) }
}