package com.example.ronflometre.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import com.example.ronflometre.data.Evenement
import com.example.ronflometre.ui.theme.RonflometreTheme

// Courbe de la nuit : l'axe horizontal = le temps (du coucher au réveil),
// chaque son est un trait vertical placé à son heure, sa hauteur = son volume
// Ronflements en couleur principale, autres bruits en gris
@Composable
fun CourbeNuit(
    debut: Long,
    fin: Long,
    evenements: List<Evenement>,
    heureDebut: String,
    heureFin: String,
    modifier: Modifier = Modifier
) {
    val couleurRonflement = MaterialTheme.colorScheme.primary
    val couleurBruit = MaterialTheme.colorScheme.outline
    val couleurAxe = MaterialTheme.colorScheme.outlineVariant
    val dureeNuit = (fin - debut).coerceAtLeast(1L)   // évite une division par 0

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            // size.width et size.height = taille de la zone de dessin en pixels
            val bas = size.height

            // la ligne de base (l'axe du temps)
            drawLine(
                color = couleurAxe,
                start = Offset(0f, bas),
                end = Offset(size.width, bas),
                strokeWidth = 2.dp.toPx()
            )

            // un trait par son
            evenements.forEach { evt ->
                // position horizontale : où se trouve ce son entre le début et la fin de la nuit (0 à 1)
                val position = ((evt.heure - debut).toFloat() / dureeNuit).coerceIn(0f, 1f)
                val x = position * size.width
                // hauteur : volume de 0 à 90 dB → 0 à toute la hauteur
                val hauteur = (evt.volumeDb / 90f).coerceIn(0.05f, 1f) * bas
                drawLine(
                    color = if (evt.type == Evenement.RONFLEMENT) couleurRonflement else couleurBruit,
                    start = Offset(x, bas),
                    end = Offset(x, bas - hauteur),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
        // heure de coucher à gauche, heure de réveil à droite
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(heureDebut, style = MaterialTheme.typography.labelSmall)
            Text(heureFin, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourbeNuitPreview() {
    val debut = 0L
    val fin = 8 * 3_600_000L
    RonflometreTheme {
        CourbeNuit(
            debut = debut,
            fin = fin,
            evenements = listOf(
                Evenement(1, 1, 3_600_000L, 8_000, 55f, Evenement.RONFLEMENT),
                Evenement(2, 1, 9_000_000L, 1_000, 70f, Evenement.BRUIT),
                Evenement(3, 1, 14_000_000L, 9_000, 50f, Evenement.RONFLEMENT),
                Evenement(4, 1, 20_000_000L, 6_000, 62f, Evenement.RONFLEMENT)
            ),
            heureDebut = "23:00",
            heureFin = "07:00"
        )
    }
}
