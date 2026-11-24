package com.example.ronflometre.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Une nuit d'écoute : de "Démarrer" à "Arrêter"
@Entity(tableName = "nuits")
data class Nuit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val debut: Long,                  // heure de début
    val fin: Long? = null             // null tant que la nuit n'est pas terminée
)
