package com.example.ronflometre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ronflometre.navigation.BarreDuBas
import com.example.ronflometre.navigation.NavGraph
import com.example.ronflometre.navigation.Routes
import com.example.ronflometre.ui.theme.RonflometreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RonflometreTheme {
                // un seul navController, partagé par la barre du bas et le NavGraph
                val navController = rememberNavController()
                val entreeActuelle by navController.currentBackStackEntryAsState()
                val routeActuelle = entreeActuelle?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // pas de barre pendant la nuit
                        if (routeActuelle != Routes.NUIT) {
                            BarreDuBas(navController)
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}