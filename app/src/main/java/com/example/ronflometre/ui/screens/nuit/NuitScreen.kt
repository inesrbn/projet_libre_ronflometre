package com.example.ronflometre.ui.screens.nuit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ronflometre.ui.screens.sommeil.ModeAlarme
import com.example.ronflometre.ui.theme.RonflometreTheme

// Écran de la nuit en cours avec écoute du micro et volume en direct
@Composable
fun NuitScreen(
    heure: Int,
    minute: Int,
    mode: ModeAlarme,
    onArreter: () -> Unit,
    viewModel: NuitViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var aLaPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var aDejaDemande by remember { mutableStateOf(false) }

    val launcherPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { estAccordee ->
        aDejaDemande = true
        aLaPermission = estAccordee
        if (estAccordee) {
            viewModel.demarrerEcoute()
        }
    }

    // Détecte automatiquement quand l'utilisateur revient des Paramètres Android
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val estAccordee = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

                aLaPermission = estAccordee
                if (estAccordee) {
                    viewModel.demarrerEcoute()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // À l'ouverture : création de la nuit dans Room + demande initiale de permission
    LaunchedEffect(Unit) {
        viewModel.commencer(heure, minute, mode)
        if (aLaPermission) {
            viewModel.demarrerEcoute()
        } else {
            launcherPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val volumeDb by viewModel.volumeDb.collectAsState()
    val estEnEcoute by viewModel.estEnEcoute.collectAsState()

    fun demanderOuOuvrirParametres() {
        if (aDejaDemande && !aLaPermission) {
            // Si la permission a déjà été refusée une fois, on redirige vers les paramètres de l'application
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        } else {
            launcherPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    NuitContenu(
        heure = heure,
        minute = minute,
        mode = mode,
        aLaPermission = aLaPermission,
        estEnEcoute = estEnEcoute,
        volumeDb = volumeDb,
        onDemanderPermission = { demanderOuOuvrirParametres() },
        onArreter = { viewModel.arreter(onTermine = onArreter) }
    )
}

@Composable
fun NuitContenu(
    heure: Int,
    minute: Int,
    mode: ModeAlarme,
    aLaPermission: Boolean,
    estEnEcoute: Boolean,
    volumeDb: Float,
    onDemanderPermission: () -> Unit,
    onArreter: () -> Unit
) {
    val heureTexte = "%02d:%02d".format(heure, minute)
    val texteAlarme = when (mode) {
        ModeAlarme.FENETRE -> "Réveil autour de $heureTexte"
        ModeAlarme.FIXE -> "Alarme à $heureTexte"
        ModeAlarme.AUCUNE -> "Pas d'alarme"
    }

    val progressionCible = (volumeDb / 90f).coerceIn(0f, 1f)
    val progressionAnimee by animateFloatAsState(
        targetValue = progressionCible,
        label = "volumeProgression"
    )

    val couleurVolume by animateColorAsState(
        targetValue = when {
            volumeDb > 65f -> MaterialTheme.colorScheme.error
            volumeDb > 40f -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        },
        label = "couleurVolume"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // Entête nuit
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Bonne nuit",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = texteAlarme,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section centrale : permission ou indicateur du micro
        if (!aLaPermission) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MicOff,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Permission du micro requise",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Le Ronflomètre a besoin du micro pour écouter le volume sonore de la nuit et détecter vos ronflements.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onDemanderPermission) {
                        Text("Autoriser le micro")
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icône micro animée / statut
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(couleurVolume.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Micro",
                            tint = couleurVolume,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = if (estEnEcoute) "Écoute en cours..." else "Initialisation...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(16.dp))

                    // Jauge de volume (dB)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Volume", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "${volumeDb.toInt()} dB",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = couleurVolume
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progressionAnimee },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = couleurVolume,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }
        }

        // Bouton d'arrêt
        Button(
            onClick = onArreter,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Arrêter la nuit",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NuitScreenPreview() {
    RonflometreTheme {
        NuitContenu(
            heure = 7,
            minute = 30,
            mode = ModeAlarme.FIXE,
            aLaPermission = true,
            estEnEcoute = true,
            volumeDb = 45f,
            onDemanderPermission = {},
            onArreter = {}
        )
    }
}
