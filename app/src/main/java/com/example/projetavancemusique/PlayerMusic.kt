package com.example.projetavancemusique

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.projetavancemusique.service.MusicPhone
import java.io.IOException

class PlayerMusic {
    lateinit var mediaPlayer: MediaPlayer

    fun getMusic(context: Context, music: MusicPhone) {
//        mediaPlayer = MediaPlayer.create(context, R.raw.ringtone)
//        mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener {
//            val intent = Intent()
//            intent.action = PlayerBroadcastReceiver.INTENT_FILTER
//            sendBroadcast(intent)
//            // next audio file
//        })
        try {
            mediaPlayer.setDataSource(context, Uri.parse(music.location))
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}