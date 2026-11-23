package com.example.ronflometre.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Un son détecté pendant une nuit (ronflement ou autre bruit)
@Entity(
    tableName = "evenements",
    // chaque événement appartient à une nuit
    foreignKeys = [ForeignKey(
        entity = Nuit::class,
        parentColumns = ["id"],
        childColumns = ["nuitId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("nuitId")]
)
data class Evenement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nuitId: Long,
    val heure: Long,          // moment du son
    val dureeMs: Long,        // durée du son
    val volumeDb: Float,      // volume en décibels
    val type: String          // "RONFLEMENT" ou "BRUIT"
) {
    companion object {
        const val RONFLEMENT = "RONFLEMENT"
        const val BRUIT = "BRUIT"
    }
}
