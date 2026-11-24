package com.example.ronflometre.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ronflometre.ui.screens.journal.JournalScreen
import com.example.ronflometre.ui.screens.nuit.NuitScreen
import com.example.ronflometre.ui.screens.ProfilScreen
import com.example.ronflometre.ui.screens.StatistiquesScreen
import com.example.ronflometre.ui.screens.sommeil.SommeilScreen

object Routes {
    const val SOMMEIL = "sommeil"
    const val JOURNAL = "journal"
    const val STATISTIQUES = "statistiques"
    const val PROFIL = "profil"
    const val NUIT = "nuit"
}

// Associe chaque route à son écran
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.SOMMEIL
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.SOMMEIL) {
            SommeilScreen(
                // Démarrer ouvre l'écran de la nuit
                onDemarrer = { navController.navigate(Routes.NUIT) }
            )
        }
        composable(Routes.JOURNAL) { JournalScreen() }
        composable(Routes.STATISTIQUES) { StatistiquesScreen() }
        composable(Routes.PROFIL) { ProfilScreen() }

        composable(Routes.NUIT) {
            NuitScreen(
                // Arrêter : la nuit est enregistrée, on va sur Journal
                onArreter = {
                    navController.navigate(Routes.JOURNAL) {
                        popUpTo(Routes.SOMMEIL) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}