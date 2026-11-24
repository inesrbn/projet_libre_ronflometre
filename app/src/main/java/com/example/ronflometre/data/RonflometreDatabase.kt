package com.example.ronflometre.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// La base de données de l'appli
@Database(entities = [Nuit::class, Evenement::class], version = 2, exportSchema = false)
abstract class RonflometreDatabase : RoomDatabase() {

    abstract fun nuitDao(): NuitDao

    companion object {
        // une seule instance pour toute l'appli
        @Volatile
        private var instance: RonflometreDatabase? = null

        fun getInstance(context: Context): RonflometreDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    RonflometreDatabase::class.java,
                    "ronflometre.db"
                )
                    // si la structure de la base change, on repart d'une base vide
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build().also { instance = it }
            }
    }
}
