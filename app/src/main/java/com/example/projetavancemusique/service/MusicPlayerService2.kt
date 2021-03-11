package com.example.projetavancemusique.service

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.example.projetavancemusique.MainActivity
import com.example.projetavancemusique.NotificationMusic

class MusicPlayerService2 : Service() {
    // Bloc static :
    companion object {
        // Constantes :
        const val EXTRA_COMMANDE = "EXTRA_COMMANDE"
        const val EXTRA_PLAYLIST = "EXTRA_PLAYLIST"
        const val EXTRA_POSITION = "EXTRA_POSITION"
        const val COMMANDE_PLAY = "COMMANDE_PLAY"
        const val COMMANDE_PAUSE = "COMMANDE_PAUSE"
    }

    // MediaPlayer :
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var playlist: ArrayList<String>
    private var position: Int = 0

    override fun onCreate() {
        // init :
        super.onCreate()
        Log.d("tag-dev", "player service start")
//        "/storage/emulated/0/sdcard/Musiques/10 Cloverfield Lane/10 Cloverfield Lane - 01. Michelle (Soundtrack).mp3"
        mediaPlayer = MediaPlayer()
        playlist = arrayListOf()
        // media player :
        mediaPlayer.setOnCompletionListener {
            // on signale à l'activité qu'on a atteint la fin du titre :
            val intent = Intent()
            intent.action = NotificationMusic().NOTIFY_PLAY
            sendBroadcast(intent)

            // on remet à zéro :
            mediaPlayer.seekTo(0)
        }
        Log.d("tag-dev", "player service end")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("tag-dev", "music init")
        if (intent?.hasExtra("playlist") == true) {
            playlist = intent.getStringArrayListExtra("playlist") as ArrayList<String>
        }
        if (intent?.hasExtra("position") == true) {
            position = intent.getIntExtra("position", 0)
        }
        mediaPlayer = MediaPlayer.create(this, Uri.parse(playlist[position]))
        Log.d("tag-dev", "music init2")

        if (intent?.hasExtra(EXTRA_COMMANDE) == true) {
            when (intent.getStringExtra(EXTRA_COMMANDE)) {
                COMMANDE_PLAY -> {
                    //lecture :
                    mediaPlayer.start()
                    Log.d("tag-dev", "music start")

                }
                COMMANDE_PAUSE -> {
                    // pause :
                    mediaPlayer.pause()
                    Log.d("tag-dev", "music stop")

                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        // init :
        super.onDestroy()

        // on libère le media player :
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }
}