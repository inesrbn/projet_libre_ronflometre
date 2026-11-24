package com.example.ronflometre.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class NuitRepository(context: Context) {

    private val dao = RonflometreDatabase.getInstance(context).nuitDao()

    // Démarrer : crée la nuit et renvoie son id
    suspend fun commencerNuit(): Long =
        dao.insererNuit(Nuit(debut = System.currentTimeMillis()))

    // Arrêter : enregistre l'heure de fin
    suspend fun terminerNuit(nuitId: Long) =
        dao.terminerNuit(nuitId, System.currentTimeMillis())

    // un son détecté pendant la nuit
    suspend fun ajouterEvenement(nuitId: Long, dureeMs: Long, volumeDb: Float, type: String) =
        dao.insererEvenement(
            Evenement(nuitId = nuitId, heure = System.currentTimeMillis(), dureeMs = dureeMs, volumeDb = volumeDb, type = type)
        )

    fun derniereNuit(): Flow<Nuit?> = dao.derniereNuit()
    fun toutesLesNuits(): Flow<List<Nuit>> = dao.toutesLesNuits()
    fun evenements(nuitId: Long): Flow<List<Evenement>> = dao.evenementsDeLaNuit(nuitId)

    suspend fun supprimerToutesLesNuits() = dao.supprimerToutesLesNuits()

    // Pour tester le Journal sans micro : une fausse nuit de 23 h 30 à 7 h 00 avec des sons inventés
    suspend fun creerNuitDeDemo() {
        val maintenant = System.currentTimeMillis()
        val fin = maintenant - 60_000L
        val debut = fin - (7.5 * 60 * 60 * 1000).toLong()
        val nuitId = dao.insererNuit(Nuit(debut = debut))
        val evenements = List(25) {
            val estRonflement = Random.nextInt(100) < 70
            Evenement(
                nuitId = nuitId,
                heure = debut + Random.nextLong(fin - debut),
                dureeMs = if (estRonflement) Random.nextLong(2_000, 15_000) else Random.nextLong(500, 3_000),
                volumeDb = Random.nextInt(40, 70).toFloat(),
                type = if (estRonflement) Evenement.RONFLEMENT else Evenement.BRUIT
            )
        }
        dao.insererEvenements(evenements)
        dao.terminerNuit(nuitId, fin)
    }
}
