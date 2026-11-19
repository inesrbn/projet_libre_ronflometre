package com.example.ronflometre.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Les requêtes sur les nuits et les événements
@Dao
interface NuitDao {

    // renvoie l'id de la nuit créée
    @Insert
    suspend fun insererNuit(nuit: Nuit): Long

    @Query("UPDATE nuits SET fin = :fin WHERE id = :nuitId")
    suspend fun terminerNuit(nuitId: Long, fin: Long)

    @Insert
    suspend fun insererEvenement(evenement: Evenement)

    @Insert
    suspend fun insererEvenements(evenements: List<Evenement>)

    // la dernière nuit terminée (celle du Journal)
    @Query("SELECT * FROM nuits WHERE fin IS NOT NULL ORDER BY debut DESC LIMIT 1")
    fun derniereNuit(): Flow<Nuit?>

    // toutes les nuits terminées, la plus récente en premier (pour les Statistiques)
    @Query("SELECT * FROM nuits WHERE fin IS NOT NULL ORDER BY debut DESC")
    fun toutesLesNuits(): Flow<List<Nuit>>

    // les sons d'une nuit, dans l'ordre
    @Query("SELECT * FROM evenements WHERE nuitId = :nuitId ORDER BY heure")
    fun evenementsDeLaNuit(nuitId: Long): Flow<List<Evenement>>

    @Query("DELETE FROM nuits")
    suspend fun supprimerToutesLesNuits()
}
