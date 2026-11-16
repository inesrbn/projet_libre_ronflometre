package com.example.ronflometre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ronflometre.ui.screens.sommeil.ModeAlarme

// Les 3 modes qu'on fait glisser + les petits points en dessous
// Prévient le parent (onModeChoisi) à chaque changement de page
@Composable
fun ChoixMode(
    heure: Int,
    minute: Int,
    modeInitial: ModeAlarme,
    onModeChoisi: (ModeAlarme) -> Unit,
    modifier: Modifier = Modifier
) {
    // état du pager : 3 pages
    val pagerState = rememberPagerState(initialPage = modeInitial.ordinal, pageCount = { 3 })

    // à chaque changement de page, on prévient le ViewModel
    LaunchedEffect(pagerState.currentPage) {
        // page 0 → FENETRE, page 1 → FIXE, page 2 → AUCUNE (même ordre que l'enum)
        onModeChoisi(ModeAlarme.entries[pagerState.currentPage])
    }

    // l'heure de l'alarme en texte
    val heureTexte = "%02d:%02d".format(heure, minute)

    // l'heure 30 minutes avant
    val minutesDebut = (heure * 60 + minute - 30 + 24 * 60) % (24 * 60)   // en minutes depuis minuit
    val debutFenetre = "%02d:%02d".format(minutesDebut / 60, minutesDebut % 60)

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            // le texte de chaque page
            val texte = when (page) {
                0 -> "Réveil entre $debutFenetre et $heureTexte"
                1 -> "Alarme à $heureTexte"
                else -> "Pas d'alarme"
            }
            Text(
                text = texte,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // les petits points : celui de la page actuelle est plus foncé
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { i ->
                val couleur = if (i == pagerState.currentPage) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(couleur)
                )
            }
        }
    }
}