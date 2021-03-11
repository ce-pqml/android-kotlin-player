package com.example.projetavancemusique.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.projetavancemusique.dao.MusicFavorisDAO
import com.example.projetavancemusique.models.MusicFavoris

@Database(entities = [MusicFavoris::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicFavorisDAO(): MusicFavorisDAO
}