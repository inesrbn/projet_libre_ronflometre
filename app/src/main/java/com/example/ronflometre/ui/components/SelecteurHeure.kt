package com.example.ronflometre.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ronflometre.ui.theme.RonflometreTheme

// stateless
@Composable
fun SelecteurHeure(
    heure: Int,
    minute: Int,
    onHeurePlus: () -> Unit,
    onHeureMoins: () -> Unit,
    onMinutePlus: () -> Unit,
    onMinuteMoins: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ColonneChiffre(valeur = heure, onPlus = onHeurePlus, onMoins = onHeureMoins)

        Text(
            text = ":",
            fontSize = 64.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        ColonneChiffre(valeur = minute, onPlus = onMinutePlus, onMoins = onMinuteMoins)
    }
}

// Une colonne : flèche vers le haut, le nombre sur 2 chiffres, flèche vers le bas
@Composable
private fun ColonneChiffre(
    valeur: Int,
    onPlus: () -> Unit,
    onMoins: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onPlus) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Augmenter",
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            text = "%02d".format(valeur),   // 7 s'affiche "07"
            fontSize = 64.sp,
            fontWeight = FontWeight.Light
        )
        IconButton(onClick = onMoins) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Diminuer",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelecteurHeurePreview() {
    RonflometreTheme {
        SelecteurHeure(
            heure = 7, minute = 30,
            onHeurePlus = {}, onHeureMoins = {},
            onMinutePlus = {}, onMinuteMoins = {}
        )
    }
}