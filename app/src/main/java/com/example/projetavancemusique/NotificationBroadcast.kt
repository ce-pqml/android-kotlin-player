package com.example.projetavancemusique

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast


class NotificationBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == NotificationMusic().NOTIFY_PLAY) {
            Log.d("tag-dev", "play btn")
            Toast.makeText(context, "NOTIFY_PLAY", Toast.LENGTH_LONG).show()
        } else if (intent.action == NotificationMusic().NOTIFY_PAUSE) {
            Log.d("tag-dev", "pause btn")
            Toast.makeText(context, "NOTIFY_PAUSE", Toast.LENGTH_LONG).show()
        }
    }
}