package com.example.projetavancemusique.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.projetavancemusique.NotificationMusic
import com.example.projetavancemusique.service.MusicPlayerService


class NotificationBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == NotificationMusic().NOTIFY_PLAY) {
            Log.d("tag-dev", "play btn")
//            Toast.makeText(context, "NOTIFY_PLAY", Toast.LENGTH_LONG).show()
            val i = Intent(context, MusicPlayerService::class.java)
            i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
            context?.startService(i)
        } else if (intent.action == NotificationMusic().NOTIFY_PAUSE) {
            Log.d("tag-dev", "pause btn")
//            Toast.makeText(context, "NOTIFY_PAUSE", Toast.LENGTH_LONG).show()
            val i = Intent(context, MusicPlayerService::class.java)
            i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PAUSE)
            context?.startService(i)
        }
    }
}