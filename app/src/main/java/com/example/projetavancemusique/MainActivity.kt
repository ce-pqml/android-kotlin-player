package com.example.projetavancemusique

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetavancemusique.adapters.MusicAdapter
import com.example.projetavancemusique.adapters.MusicFavorisAdapter
import com.example.projetavancemusique.database.AppDatabaseHelper
import com.example.projetavancemusique.models.MusicFavoris
import com.example.projetavancemusique.service.MusicPlayerService
import com.example.projetavancemusique.service.getAllMusicPhone


class MainActivity : AppCompatActivity() {
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicFavorisAdapter: MusicFavorisAdapter
    var isAdpaterFav : Boolean = false
    private lateinit var monBroadcastReceiver: BtnBroadcastReceiver

    inner class BtnBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            changeBtn(intent.getStringExtra("btn"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listMusicFavoris: MutableList<MusicFavoris> = AppDatabaseHelper.getDatabase(this)
            .musicFavorisDAO()
            .getMusicFavoris()

        val listMusic = getAllMusicPhone(listMusicFavoris, this)

        musicFavorisAdapter = MusicFavorisAdapter(listMusicFavoris, this, listMusic)

        findViewById<RecyclerView>(R.id.list_box).setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.list_box).layoutManager = layoutManager

        if (listMusic != null) {
            musicAdapter = MusicAdapter(listMusic, this, musicFavorisAdapter)
            findViewById<RecyclerView>(R.id.list_box).adapter = musicAdapter
            findViewById<ProgressBar>(R.id.loader).setVisibility(View.GONE)
        } else {
            findViewById<ProgressBar>(R.id.loader).setVisibility(View.GONE)
        }

        val intent = Intent(this, MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.EXTRA_GET_PLAYLIST, MusicPlayerService.PLAYLIST_BOTH)
        this.startService(intent)

//        findViewById<ImageButton>(R.id.play).setOnClickListener {
//            val i = Intent(this, MusicPlayerService::class.java)
//            i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
//            this.startService(i)
//        }
//
//        findViewById<ImageButton>(R.id.pause).setOnClickListener {
//            val i = Intent(this, MusicPlayerService::class.java)
//            i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PAUSE)
//            this.startService(i)
//        }

        val playPause: ImageButton = findViewById(R.id.play_pause)
        playPause.setOnClickListener {
            val imgTag: String? = playPause.tag as? String
            val i = Intent(this, MusicPlayerService::class.java)
            if (imgTag == "pause") {
                playPause.setImageResource(R.drawable.ic_baseline_pause_48)
                playPause.tag = "play"
                i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
                this.startService(i)
            } else {
                playPause.setImageResource(R.drawable.ic_baseline_play_arrow_48)
                playPause.tag = "pause"
                i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PAUSE)
                this.startService(i)
            }
        }

        val stopBtn: ImageButton = findViewById(R.id.stop_btn)
        stopBtn.setOnClickListener {
            val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(7634)
            val actionBtn: LinearLayout = findViewById(R.id.btn_player_liste)
            if (actionBtn.isVisible) {
                actionBtn.visibility = View.GONE
            }
            val i = Intent(this, MusicPlayerService::class.java)
            i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_STOP)
            this.startService(i)
        }

        monBroadcastReceiver = BtnBroadcastReceiver()
        registerReceiver(monBroadcastReceiver, IntentFilter("com.android.activity.BTN_PLAYER"))
    }

    fun changeBtn(action: String?)
    {
        val playPause: ImageButton = findViewById(R.id.play_pause)
        if (action == "play") {
            playPause.setImageResource(R.drawable.ic_baseline_pause_48)
        } else {
            playPause.setImageResource(R.drawable.ic_baseline_play_arrow_48)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.action_favorite ->
            {
                if (isAdpaterFav) {
                    item.setIcon(R.drawable.ic_favorite_border_black_48dp);
                    findViewById<RecyclerView>(R.id.list_box).adapter = musicAdapter
                    isAdpaterFav = false
                } else {
                    item.setIcon(R.drawable.ic_favorite_black_48dp);
                    findViewById<RecyclerView>(R.id.list_box).adapter = musicFavorisAdapter
                    isAdpaterFav = true
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}