package com.example.projetavancemusique.dao

import androidx.room.*
import com.example.projetavancemusique.models.MusicFavoris

@Dao
abstract class MusicFavorisDAO {
    @Query("SELECT * FROM musicFavoris")
    abstract fun getMusicFavoris(): MutableList<MusicFavoris>

    @Insert
    abstract fun insert(vararg musicFavoris: MusicFavoris)

    @Update
    abstract fun update(vararg musicFavoris: MusicFavoris)

    @Delete
    abstract fun delete(vararg musicFavoris: MusicFavoris)
}