package com.example.projetavancemusique.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.projetavancemusique.NotificationMusic
import com.example.projetavancemusique.service.MusicPlayerService

class PlayerMusicBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == NotificationMusic().NOTIFY_PLAY) {
            Log.d("tag-dev", "music next")
            val i = Intent(context, MusicPlayerService::class.java)
            i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
            context?.startService(i)
        }
    }
}