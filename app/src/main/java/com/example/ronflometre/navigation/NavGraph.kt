package com.example.ronflometre.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ronflometre.ui.screens.JournalScreen
import com.example.ronflometre.ui.screens.NuitScreen
import com.example.ronflometre.ui.screens.ProfilScreen
import com.example.ronflometre.ui.screens.StatistiquesScreen
import com.example.ronflometre.ui.screens.sommeil.ModeAlarme
import com.example.ronflometre.ui.screens.sommeil.SommeilScreen

object Routes {
    const val SOMMEIL = "sommeil"
    const val JOURNAL = "journal"
    const val STATISTIQUES = "statistiques"
    const val PROFIL = "profil"

    // l'écran Nuit reçoit l'heure, la minute et le mode dans sa route
    const val NUIT = "nuit/{heure}/{minute}/{mode}"
    fun nuit(heure: Int, minute: Int, mode: ModeAlarme) = "nuit/$heure/$minute/${mode.name}"
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
                // Démarrer ouvre l'écran de la nuit avec l'heure et le mode choisis
                onDemarrer = { heure, minute, mode ->
                    navController.navigate(Routes.nuit(heure, minute, mode))
                }
            )
        }
        composable(Routes.JOURNAL) { JournalScreen() }
        composable(Routes.STATISTIQUES) { StatistiquesScreen() }
        composable(Routes.PROFIL) { ProfilScreen() }

        composable(
            route = Routes.NUIT,
            // le type de chaque argument de la route
            arguments = listOf(
                navArgument("heure") { type = NavType.IntType },
                navArgument("minute") { type = NavType.IntType },
                navArgument("mode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // on lit les arguments
            val heure = backStackEntry.arguments?.getInt("heure") ?: 7
            val minute = backStackEntry.arguments?.getInt("minute") ?: 30
            val mode = ModeAlarme.valueOf(
                backStackEntry.arguments?.getString("mode") ?: ModeAlarme.FIXE.name
            )

            NuitScreen(
                heure = heure,
                minute = minute,
                mode = mode,
                // Arrêter revient à l'écran précédent (Sommeil)
                onArreter = { navController.popBackStack() }
            )
        }
    }
}