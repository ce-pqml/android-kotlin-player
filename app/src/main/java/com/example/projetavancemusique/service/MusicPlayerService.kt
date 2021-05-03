package com.example.projetavancemusique.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.example.projetavancemusique.R
import com.example.projetavancemusique.database.AppDatabaseHelper
import com.example.projetavancemusique.models.MusicFavoris


class MusicPlayerService : Service() {
    // Bloc static :
    companion object
    {
        // Constantes :
        const val EXTRA_COMMANDE = "EXTRA_COMMANDE"
        const val COMMANDE_PLAY = "COMMANDE_PLAY"
        const val COMMANDE_PAUSE = "COMMANDE_PAUSE"
        const val COMMANDE_STOP = "COMMANDE_STOP"
        const val EXTRA_PLAYLIST = "EXTRA_PLAYLIST"
        const val EXTRA_POSITION = "EXTRA_POSITION"
        const val EXTRA_GET_PLAYLIST = "EXTRA_GET_PLAYLIST"
        const val PLAYLIST_PHONE = "PLAYLIST_PHONE"
        const val PLAYLIST_FAVORIS = "PLAYLIST_FAVORIS"
        const val PLAYLIST_BOTH = "PLAYLIST_BOTH"
    }

    // Prérequis :
    private lateinit var mediaPlayer: MediaPlayer
    private var playlist: String? = PLAYLIST_PHONE
    private var playlist_phone : MutableList<MusicPhone>? = arrayListOf()
    private lateinit var playlist_favoris : MutableList<MusicFavoris>
    private var position: Int = 0

    override fun onCreate()
    {
        super.onCreate()

        // media player
        position = 0
        mediaPlayer = MediaPlayer.create(this, R.raw.titre)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        Log.d("tag-dev", "onStartCommand")
        if (intent !== null && intent.hasExtra(EXTRA_POSITION) && intent.hasExtra(EXTRA_PLAYLIST)) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            playlist = intent.getStringExtra(EXTRA_PLAYLIST)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            var location : String = ""
            if (playlist == PLAYLIST_PHONE) {
                if (playlist_phone !== null) {
                    location = playlist_phone!![position].location
                }
            } else {
                location = playlist_favoris[position].location
            }
            mediaPlayer = MediaPlayer.create(this, Uri.parse(location))
        }
        if (intent !== null && intent.hasExtra("music")) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer = MediaPlayer.create(this, Uri.parse(intent.getStringExtra("music")))
        }
        if (intent !== null && intent.hasExtra(EXTRA_GET_PLAYLIST)) {
            when (intent.getStringExtra(EXTRA_GET_PLAYLIST)) {
                PLAYLIST_PHONE -> {
                    playlist_phone = getAllMusicPhone(playlist_favoris, this)
                }
                PLAYLIST_FAVORIS -> {
                    playlist_favoris = AppDatabaseHelper.getDatabase(this)
                                            .musicFavorisDAO()
                                            .getMusicFavoris()
                }
                PLAYLIST_BOTH -> {
                    playlist_favoris = AppDatabaseHelper.getDatabase(this)
                            .musicFavorisDAO()
                            .getMusicFavoris()
                    playlist_phone = getAllMusicPhone(playlist_favoris, this)
                }
            }
        }
        if (intent !== null && intent.hasExtra(EXTRA_COMMANDE))
        {
            when (intent.getStringExtra(EXTRA_COMMANDE))
            {
                COMMANDE_PLAY -> {
                    //lecture :
                    mediaPlayer.start()
                    //fin du titre en cours
                    mediaPlayer.setOnCompletionListener {
                        Log.d("tag-dev", "end music")
                        mediaPlayer.stop()
                        Log.d("tag-dev", "position $position, size phone ${playlist_phone!!.size}, size favoris ${playlist_favoris.size}")
                        position += 1
                        var location : String = ""
                        if (playlist == PLAYLIST_PHONE) {
                            if (playlist_phone !== null) {
                                if (position >= playlist_phone!!.size) {
                                    position = 0
                                }
                                location = playlist_phone!![position].location
                            }
                        } else {
                            if (position >= playlist_favoris.size) {
                                position = 0
                            }
                            location = playlist_favoris[position].location
                        }
                        Log.d("tag-dev", "playlist $playlist, position $position, location $location")
                        mediaPlayer = MediaPlayer.create(this, Uri.parse(location))
                        mediaPlayer.start()
                    }
                    val intentBroad = Intent("com.android.activity.BTN_PLAYER")
                    intentBroad.putExtra("btn", "play")
                    sendBroadcast(intentBroad)
                }
                COMMANDE_PAUSE -> {
                    // pause :
                    mediaPlayer.pause()
                    val intentBroad = Intent("com.android.activity.BTN_PLAYER")
                    intentBroad.putExtra("btn", "pause")
                    sendBroadcast(intentBroad)
                }
                COMMANDE_STOP -> {
                    // stop :
                    mediaPlayer.stop()
                    mediaPlayer.reset()
//                    mediaPlayer.release()
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