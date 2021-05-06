package com.example.projetavancemusique.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.projetavancemusique.service.MusicPlayerService


class NotificationBroadcast : BroadcastReceiver() {

    // gestion des boutons de la notification play/pause
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == MusicPlayerService.NOTIFY_PLAY) {
            val i = Intent(context, MusicPlayerService::class.java)
            i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
            context?.startService(i)
        } else if (intent.action == MusicPlayerService.NOTIFY_PAUSE) {
            val i = Intent(context, MusicPlayerService::class.java)
            i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PAUSE)
            context?.startService(i)
        }
    }
}