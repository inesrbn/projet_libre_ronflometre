package com.example.ronflometre.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ronflometre.ui.screens.JournalScreen
import com.example.ronflometre.ui.screens.NuitScreen
import com.example.ronflometre.ui.screens.ProfilScreen
import com.example.ronflometre.ui.screens.SommeilScreen
import com.example.ronflometre.ui.screens.StatistiquesScreen

object Routes {

    const val NUIT = "nuit"

    const val JOURNAL = "journal"

    const val STATISTIQUES = "statistiques"

    const val SOMMEIL = "sommeil"

    const val PROFIL = "profil"

}

// Associe chaque route à son écran
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SOMMEIL) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    )  {
        composable(Routes.SOMMEIL) {
            SommeilScreen(
                // le bouton Démarrer ouvre l'écran de la nuit
                onDemarrer = { navController.navigate(Routes.NUIT) }
            )
        }
        composable(Routes.JOURNAL) { JournalScreen() }
        composable(Routes.STATISTIQUES) { StatistiquesScreen() }
        composable(Routes.PROFIL) { ProfilScreen() }
        composable(Routes.NUIT) {
            NuitScreen(
                // le bouton Arrêter revient à l'écran précédent (Sommeil)
                onArreter = { navController.popBackStack() }
            )
        }
    }
}
