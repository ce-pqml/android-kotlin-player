package com.example.projetavancemusique.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import com.example.projetavancemusique.MainActivity
import com.example.projetavancemusique.R
import com.example.projetavancemusique.database.AppDatabaseHelper
import com.example.projetavancemusique.models.MusicFavoris
import com.example.projetavancemusique.models.MusicPhone


class MusicPlayerService : Service() {
    // Bloc static :
    companion object
    {
        // Constantes :
        const val EXTRA_COMMANDE = "EXTRA_COMMANDE"
        const val COMMANDE_PLAY = "COMMANDE_PLAY"
        const val COMMANDE_PLAY_ID = 264
        const val COMMANDE_PAUSE = "COMMANDE_PAUSE"
        const val COMMANDE_PAUSE_ID = 265
        const val COMMANDE_STOP = "COMMANDE_STOP"
        const val EXTRA_PLAYLIST = "EXTRA_PLAYLIST"
        const val EXTRA_POSITION = "EXTRA_POSITION"
        const val EXTRA_GET_PLAYLIST = "EXTRA_GET_PLAYLIST"
        const val PLAYLIST_PHONE = "PLAYLIST_PHONE"
        const val PLAYLIST_FAVORIS = "PLAYLIST_FAVORIS"
        const val PLAYLIST_BOTH = "PLAYLIST_BOTH"
        const val NOTIFY_PLAY = "com.avancemusic.notification.play"
        const val NOTIFY_PAUSE = "com.avancemusic.notification.pause"
        const val NOTIFY_ID = 7634

        var isServiceRunning: Boolean = false
        var isMusicPlay: Boolean = false
    }

    // Prérequis :
    private lateinit var mediaPlayer: MediaPlayer
    private var playlist: String? = PLAYLIST_PHONE
    private var playlist_phone : MutableList<MusicPhone>? = arrayListOf()
    private lateinit var playlist_favoris : MutableList<MusicFavoris>
    private var position: Int = 0

    // Prérequis notification :
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.ca.musiquefav.gestion"
    private val description = "Notification gestion musique"

    override fun onCreate()
    {
        super.onCreate()

        // media player
        position = 0
        mediaPlayer = MediaPlayer.create(this, R.raw.titre)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        // si on reçoit le intent de choix de musique
        // la playlist choisis, la position,
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
//        if (intent !== null && intent.hasExtra("music")) {
//            if (mediaPlayer.isPlaying) {
//                mediaPlayer.stop()
//            }
//            mediaPlayer = MediaPlayer.create(this, Uri.parse(intent.getStringExtra("music")))
//        }

        // si on reçoit le intent de renouveller les playlist
        // possibiliter de choisir une playlist ou les deux
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

        // si on reçoit le intent de commande
        if (intent !== null && intent.hasExtra(EXTRA_COMMANDE)) {
            when (intent.getStringExtra(EXTRA_COMMANDE)) {
                COMMANDE_PLAY -> {
                    // on lance/relace la musique :
                    mediaPlayer.start()
                    isMusicPlay = true
                    // si le titre en cours ce finis on ajoute 1 a la postion dans la playlist
                    // et on lance si existe sinon on reviens à la position 0
                    mediaPlayer.setOnCompletionListener {
                        mediaPlayer.stop()
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
                        mediaPlayer = MediaPlayer.create(this, Uri.parse(location))
                        mediaPlayer.start()
                    }

                    // envoi du broadcast pour modifier le bouton sur MainActivity
                    val intentBroad = Intent(MainActivity.BROADCAST_BTN_MAIN)
                    intentBroad.putExtra("btn", "play")
                    sendBroadcast(intentBroad)

                    // on démarre la notification si déjà pas affiché
                    // démarrage en foreground pour avoir toujours la notif et la musique
                    if (!isServiceRunning) {
                        isServiceRunning = true
                        startForeground(NOTIFY_ID, displayNotification(this).build())
                    }
                }
                COMMANDE_PAUSE -> {
                    // on met en pause la musique :
                    mediaPlayer.pause()
                    isMusicPlay = false

                    // envoi du broadcast pour modifier le bouton sur MainActivity
                    val intentBroad = Intent(MainActivity.BROADCAST_BTN_MAIN)
                    intentBroad.putExtra("btn", "pause")
                    sendBroadcast(intentBroad)
                }
                COMMANDE_STOP -> {
                    // on stop tout, on retire les boutons et la notification :
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    isMusicPlay = false
                    isServiceRunning = false
                    stopForeground(true)
//                    stopSelf()
//                    mediaPlayer.release()
                }
            }
        }
        return START_NOT_STICKY
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

    // fonction pour afficher la notification
    fun displayNotification(context: Context): Notification.Builder {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val expandedView = RemoteViews(context.packageName, R.layout.notification_music)
        val notifyIntent = Intent(context, MainActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // en fonction de la version android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context, channelId)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setCustomContentView(expandedView)
//                    .setCustomBigContentView(expandedView)
                    .setContentTitle("Music Player")
                    .setContentText("Control Audio")
                    .setOngoing(true)
                    .setAutoCancel(false)
        } else {
            builder = Notification.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_background)
//                    .setCustomContentView(expandedView)
//                    .setCustomBigContentView(expandedView)
                    .setContentTitle("Music Player")
                    .setContentText("Control Audio")
                    .setOngoing(true)
                    .setAutoCancel(false)
        }

        // on ajoute a gestion des boutons
        setListeners(expandedView)
        return builder
    }

    // function de gestion des boutons
    // action à effectuer au clic
    private fun setListeners(view: RemoteViews) {
        val intentPlay = Intent(this, MusicPlayerService::class.java)
        intentPlay.putExtra(EXTRA_COMMANDE, COMMANDE_PLAY)
        val pendingIntentPlay = PendingIntent.getService(this, COMMANDE_PLAY_ID, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)
        view.setOnClickPendingIntent(R.id.play, pendingIntentPlay)

        val intentPause = Intent(this, MusicPlayerService::class.java)
        intentPause.putExtra(EXTRA_COMMANDE, COMMANDE_PAUSE)
        val pendingIntentPause = PendingIntent.getService(this, COMMANDE_PAUSE_ID, intentPause, PendingIntent.FLAG_UPDATE_CURRENT)
        view.setOnClickPendingIntent(R.id.pause, pendingIntentPause)
    }
}