package com.example.ronflometre.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// Un onglet de la barre : sa route, son texte, son icône
data class Onglet(val route: String, val titre: String, val icone: ImageVector)

// Les onglets
val onglets = listOf(
    Onglet(Routes.SOMMEIL, "Sommeil", Icons.Filled.Bedtime),
    Onglet(Routes.JOURNAL, "Journal", Icons.Filled.Book),
    Onglet(Routes.STATISTIQUES, "Statistiques", Icons.Filled.BarChart),
    Onglet(Routes.PROFIL, "Profil", Icons.Filled.Person)
)

@Composable
fun BarreDuBas(navController: NavHostController) {
    // la route de l'écran affiché : se met à jour à chaque navigation
    val entreeActuelle by navController.currentBackStackEntryAsState()
    val routeActuelle = entreeActuelle?.destination?.route

    NavigationBar {
        onglets.forEach { onglet ->
            NavigationBarItem(
                selected = routeActuelle == onglet.route,
                onClick = {
                    navController.navigate(onglet.route) {
                        // on revient au premier écran au lieu d'empiler les onglets
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true   // pas de deuxième copie si on est déjà dessus
                        restoreState = true      // on retrouve l'onglet comme on l'avait laissé
                    }
                },
                icon = { Icon(onglet.icone, contentDescription = onglet.titre) },
                label = { Text(onglet.titre) }
            )
        }
    }
}
