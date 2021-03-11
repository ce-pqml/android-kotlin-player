package com.example.projetavancemusique.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "musicFavoris")
class MusicFavoris (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val size: Double = 0.0,
    val duration: String = "",
    val location: String = "",
    val idPhone: Int = 0
) : Parcelable