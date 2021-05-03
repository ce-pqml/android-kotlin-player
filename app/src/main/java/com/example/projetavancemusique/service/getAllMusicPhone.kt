package com.example.projetavancemusique.service

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.projetavancemusique.models.MusicFavoris
import kotlin.math.round

fun getAllMusicPhone(listMusicFavoris: MutableList<MusicFavoris>, context: Context): MutableList<MusicPhone>? {
    val tempAudioList: MutableList<MusicPhone> = ArrayList()

    val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    //Valeur à récupérer
    val projection = arrayOf(
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.SIZE,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.Media._ID
    )

    //Type de fichier à sélectionner
    val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

    val c: Cursor? = context.contentResolver.query(
        uri,
        projection,
        selection,
        null,
        null
    )

    if (c != null) {
        while (c.moveToNext()) {
            var title: String = c.getString(0)
            if (!c.getString(4).equals("<unknown>")) {
                title = c.getString(0) + " - " + c.getString(4)
            }
            val size: Double = octetToMo(c.getDouble(1))
            val duration: String = msToMinuteAndSec(c.getInt(2))
            val path: String = c.getString(3)
            val idPhone: Int = c.getInt(5)
            var favorite: Boolean = false
            if (listMusicFavoris.find{it.idPhone == idPhone} !== null) {
                Log.d("tag-dev", idPhone.toString())
                Log.d("tag-dev", listMusicFavoris.find{it.idPhone == idPhone}.toString())
                favorite = true
            }
            val musicPhone = MusicPhone(title, size, duration, path, idPhone, favorite)
            tempAudioList.add(musicPhone)
        }
        c.close()
    }

    return tempAudioList
}

fun octetToMo(octet: Double): Double {
    //Passage d'un bytes en Mo (en divise par 8 pour passer en Octet puis par 1000000 pour passer en Mo)
    return round((octet/1000000) *100)/100
}

fun msToMinuteAndSec(ms: Int): String {
    //Passage de ms à minutes et secondes en utilisant une formule mathématique au lieu d'appeler un autre objet
    //Pour faire plus propre on ajoute un 0 devant les secondes si < 10 (pour avoir deux chiffres)
    val minutes = ms / 1000 / 60
    val seconds = ms / 1000 % 60
    if (seconds < 10) {
        return "$minutes:0$seconds"
    } else {
        return "$minutes:$seconds"
    }
}