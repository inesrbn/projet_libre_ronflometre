package com.example.ronflometre.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ronflometre.ui.theme.RonflometreTheme

// Grand cercle avec la qualité de la nuit en % au centre
@Composable
fun CercleQualite(qualite: Int, modifier: Modifier = Modifier) {
    // couleur selon la note : vert si bonne nuit, orange moyen, rouge mauvaise
    val couleur = when {
        qualite >= 80 -> MaterialTheme.colorScheme.primary
        qualite >= 50 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
    Box(modifier = modifier.size(160.dp), contentAlignment = Alignment.Center) {
        // l'anneau : progress va de 0 à 1, donc 85 % -> 0.85
        CircularProgressIndicator(
            progress = { qualite / 100f },
            modifier = Modifier.size(160.dp),
            color = couleur,
            strokeWidth = 14.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
        // le texte au centre
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$qualite %", fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Text("qualité", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CercleQualitePreview() {
    RonflometreTheme { CercleQualite(qualite = 85) }
}
