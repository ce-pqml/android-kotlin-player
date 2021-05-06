package com.example.projetavancemusique

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private lateinit var monBroadcastReceiver: BtnBroadcastReceiver
    var isAdpaterFav : Boolean = false

    companion object
    {
        const val BROADCAST_BTN_MAIN = "com.android.activity.BTN_PLAYER"
        const val PERMISSIONS_STORAGE_ID = 473
    }

    // broadcast unique du MainActivity pour changer les boutons
    inner class BtnBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            changeBtn(intent.getStringExtra("btn"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // on vérifie si on a l'a permission de voir le STORAGE
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            loadPlaylist()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_STORAGE_ID)
        }

        // mise en place de l'action du bouton play/pause
        // change son image on fonction de l'état du MusicPlayerService
        val playPause: ImageButton = findViewById(R.id.play_pause)
        if (MusicPlayerService.isMusicPlay) {
            playPause.setImageResource(R.drawable.ic_baseline_pause_48)
        } else {
            playPause.setImageResource(R.drawable.ic_baseline_play_arrow_48)
        }

        playPause.setOnClickListener {
            val i = Intent(this, MusicPlayerService::class.java)
            if (!MusicPlayerService.isMusicPlay) {
                playPause.setImageResource(R.drawable.ic_baseline_pause_48)
                i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
                this.startService(i)
            } else {
                playPause.setImageResource(R.drawable.ic_baseline_play_arrow_48)
                i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PAUSE)
                this.startService(i)
            }
        }

        // mise en place de l'action sur le bouton stop
        val stopBtn: ImageButton = findViewById(R.id.stop_btn)
        stopBtn.setOnClickListener {
            val actionBtn: LinearLayout = findViewById(R.id.btn_player_liste)
            if (actionBtn.isVisible) {
                actionBtn.visibility = View.GONE
            }

            val i = Intent(this, MusicPlayerService::class.java)
            i.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_STOP)
            this.startService(i)
        }

        // si le service tourne déjà, on affiche les boutons
        if (MusicPlayerService.isServiceRunning) {
            val actionBtn: LinearLayout = findViewById(R.id.btn_player_liste)
            if (!actionBtn.isVisible) {
                actionBtn.visibility = View.VISIBLE
            }
        }

        // register du broadcast pour changer les boutons du MainActivity
        monBroadcastReceiver = BtnBroadcastReceiver()
        registerReceiver(monBroadcastReceiver, IntentFilter(BROADCAST_BTN_MAIN))
    }

    // function du chargement des playlist dans le service MusicPlayerService
    fun loadPlaylist() {
        findViewById<TextView>(R.id.denied_msg).visibility = View.GONE
        val listMusicFavoris: MutableList<MusicFavoris> = AppDatabaseHelper.getDatabase(this)
                .musicFavorisDAO()
                .getMusicFavoris()
        val listMusic = getAllMusicPhone(listMusicFavoris, this)
        musicFavorisAdapter = MusicFavorisAdapter(listMusicFavoris, this, listMusic)

        findViewById<RecyclerView>(R.id.list_box).setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = false
        findViewById<RecyclerView>(R.id.list_box).layoutManager = layoutManager

        if (listMusic != null) {
            musicAdapter = MusicAdapter(listMusic, this, musicFavorisAdapter)
            findViewById<RecyclerView>(R.id.list_box).adapter = musicAdapter
            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        } else {
            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        }

        val intent = Intent(this, MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.EXTRA_GET_PLAYLIST, MusicPlayerService.PLAYLIST_BOTH)
        this.startService(intent)
    }

    // on change l'image du bouton en fonction de l'état de la musique
    fun changeBtn(action: String?)
    {
        val playPause: ImageButton = findViewById(R.id.play_pause)
        if (action == "play") {
            playPause.setImageResource(R.drawable.ic_baseline_pause_48)
        } else {
            playPause.setImageResource(R.drawable.ic_baseline_play_arrow_48)
        }
    }

    // affiche du menu favoris
    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // action au clic sur le menu favoris
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

    // si retour de permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_STORAGE_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPlaylist()
            } else {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            }
        }
    }
}