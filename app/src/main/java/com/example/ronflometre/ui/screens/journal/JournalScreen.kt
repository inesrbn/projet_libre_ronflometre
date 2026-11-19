package com.example.ronflometre.ui.screens.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ronflometre.data.Evenement
import com.example.ronflometre.data.Nuit
import com.example.ronflometre.ui.theme.RonflometreTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

//petites fonctions pour afficher les heures et les durées

private val formatHeure = DateTimeFormatter.ofPattern("HH:mm")
private val formatDate = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH)

private fun heure(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(formatHeure)

private fun date(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(formatDate)


private fun duree(millis: Long): String {
    val secondes = millis / 1000
    val h = secondes / 3600
    val min = (secondes % 3600) / 60
    return when {
        h > 0 -> "$h h ${"%02d".format(min)} min"
        min > 0 -> "$min min"
        else -> "$secondes s"
    }
}

// stateful
@Composable
fun JournalScreen(viewModel: JournalViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    JournalContenu(uiState = uiState, onCreerDemo = { viewModel.creerNuitDeDemo() })
}

// stateless
@Composable
fun JournalContenu(uiState: JournalUiState, onCreerDemo: () -> Unit) {
    val nuit = uiState.nuit
    when {
        uiState.chargement -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { CircularProgressIndicator() }

        nuit == null -> AucuneNuit(onCreerDemo)

        else -> LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // en-tête : date de la nuit
            item {
                Text("Journal", style = MaterialTheme.typography.headlineMedium)
                Text(date(nuit.debut), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // les cartes du bilan, 2 par ligne
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CarteBilan("Coucher", heure(nuit.debut), Modifier.weight(1f))
                    CarteBilan("Réveil", nuit.fin?.let { heure(it) } ?: "—", Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CarteBilan("Temps au lit", duree(uiState.dureeNuitMs), Modifier.weight(1f))
                    CarteBilan("Mon ronflement", duree(uiState.dureeRonflementMs), Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CarteBilan("Taux", "%.1f / h".format(uiState.tauxParHeure), Modifier.weight(1f))
                    CarteBilan("Sons détectés", "${uiState.evenements.size}", Modifier.weight(1f))
                }
            }
            // la liste horodatée
            item {
                Spacer(Modifier.height(8.dp))
                Text("Sons de la nuit", style = MaterialTheme.typography.titleMedium)
            }
            if (uiState.evenements.isEmpty()) {
                item { Text("Aucun son détecté cette nuit.") }
            } else {
                items(uiState.evenements, key = { it.id }) { evt ->
                    LigneEvenement(evt)
                    HorizontalDivider()
                }
            }
        }
    }
}

// message quand aucune nuit n'est encore enregistrée
@Composable
private fun AucuneNuit(onCreerDemo: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Aucune nuit enregistrée", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("Lance une nuit depuis l'onglet Sommeil.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        // pour tester sans micro
        Button(onClick = onCreerDemo) { Text("Créer une nuit de démo") }
    }
}

// une carte du bilan : un titre et une valeur
@Composable
private fun CarteBilan(titre: String, valeur: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(titre, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valeur, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// une ligne de la liste
@Composable
private fun LigneEvenement(evt: Evenement) {
    val estRonflement = evt.type == Evenement.RONFLEMENT
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(heure(evt.heure), fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.25f))
        Text(
            if (estRonflement) "Ronflement" else "Bruit",
            color = if (estRonflement) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.35f)
        )
        Text(duree(evt.dureeMs), modifier = Modifier.weight(0.2f))
        Text("${evt.volumeDb.toInt()} dB", modifier = Modifier.weight(0.2f))
    }
}

@Preview(showBackground = true)
@Composable
fun JournalPreview() {
    val debut = 1_700_000_000_000L
    RonflometreTheme {
        JournalContenu(
            uiState = JournalUiState(
                chargement = false,
                nuit = Nuit(id = 1, debut = debut, fin = debut + 27_000_000L),
                evenements = listOf(
                    Evenement(1, 1, debut + 3_600_000L, 8_000, 52f, Evenement.RONFLEMENT),
                    Evenement(2, 1, debut + 7_200_000L, 1_500, 61f, Evenement.BRUIT)
                )
            ),
            onCreerDemo = {}
        )
    }
}
