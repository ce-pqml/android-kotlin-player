package com.example.projetavancemusique.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.example.projetavancemusique.R

class MusicPlayerService : Service() {
    // Bloc static :
    companion object
    {
        // Constantes :
        const val EXTRA_COMMANDE = "EXTRA_COMMANDE"
        const val COMMANDE_PLAY = "COMMANDE_PLAY"
        const val COMMANDE_PAUSE = "COMMANDE_PAUSE"
    }

    // Prérequis :
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playlist: ArrayList<String>
    private var position: Int = 0

    override fun onCreate()
    {
        // init :
        super.onCreate()

        // media player :
        playlist = arrayListOf()
        position = 0
        mediaPlayer = MediaPlayer.create(this, R.raw.titre)
        mediaPlayer.setOnCompletionListener {

            // on signale à l'activité qu'on a atteint la fin du titre :
//            val intent = Intent()
//            intent.action = MainActivity.LecteurBroadcastReceiver.INTENT_FILTER
//            sendBroadcast(intent)

            // on remet à zéro :
            mediaPlayer.seekTo(0)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        Log.d("tag-dev", "onStartCommand2")
        if (intent?.hasExtra("playlist") == true) {
            playlist = intent.getStringArrayListExtra("playlist") as ArrayList<String>
        }
        if (intent?.hasExtra("position") == true) {
            position = intent.getIntExtra("position", 0)
        }
        if (intent?.hasExtra("music") == true) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer = MediaPlayer.create(this, Uri.parse(intent.getStringExtra("music")))
        }
        if (intent?.hasExtra(EXTRA_COMMANDE) == true)
        {
            when (intent.getStringExtra(EXTRA_COMMANDE))
            {
                COMMANDE_PLAY -> {
                    //lecture :
                    mediaPlayer.start()
                }
                COMMANDE_PAUSE -> {
                    // pause :
                    mediaPlayer.pause()
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy()
    {
        // init :
        super.onDestroy()

        // on libère le media player :
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }
}