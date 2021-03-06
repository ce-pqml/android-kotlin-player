package com.example.projetavancemusique.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class MusicPhone (
    val title: String = "",
    val size: Double = 0.0,
    val duration: String = "",
    val location: String = "",
    val idPhone: Int = 0,
    var favorite: Boolean = false
) : Parcelable