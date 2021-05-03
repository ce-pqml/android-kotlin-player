package com.example.projetavancemusique

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import com.example.projetavancemusique.models.MusicFavoris
import com.example.projetavancemusique.service.MusicPhone
import com.example.projetavancemusique.service.MusicPlayerService


class NotificationMusic {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    val NOTIFY_PLAY = "com.avancemusic.notification.play"
    val NOTIFY_PAUSE = "com.avancemusic.notification.pause"

    fun startMusic(context: Context, playlist: String, position: Int) {
        displayNotification(context)
        val intent = Intent(context, MusicPlayerService::class.java)

        intent.putExtra(MusicPlayerService.EXTRA_PLAYLIST, playlist)
        intent.putExtra(MusicPlayerService.EXTRA_POSITION, position)
        intent.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
        context.startService(intent)
    }

    fun startMusic(context: Context, music: MusicFavoris) {
        displayNotification(context)
    }


    fun displayNotification(context: Context) {
        var notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val expandedView = RemoteViews(context.getPackageName(), R.layout.notification_music)
        val notifyIntent = Intent(context, MainActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

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
                    .setAutoCancel(true)
        } else {
            builder = Notification.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_background)
//                    .setCustomContentView(expandedView)
//                    .setCustomBigContentView(expandedView)
                    .setContentTitle("Music Player")
                    .setContentText("Control Audio")
                    .setOngoing(true)
                    .setAutoCancel(true)
        }
        setListeners(expandedView, context)
        notificationManager.notify(1234, builder.build())
    }

    private fun setListeners(view: RemoteViews, context: Context) {
        val pause = Intent(NOTIFY_PAUSE)
        val play = Intent(NOTIFY_PLAY)
        val pPause = PendingIntent.getBroadcast(context, 0, pause, PendingIntent.FLAG_UPDATE_CURRENT)
        view.setOnClickPendingIntent(R.id.pause, pPause)
        val pPlay = PendingIntent.getBroadcast(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT)
        view.setOnClickPendingIntent(R.id.play, pPlay)
    }
}