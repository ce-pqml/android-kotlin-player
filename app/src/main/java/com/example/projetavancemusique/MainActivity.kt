package com.example.projetavancemusique

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetavancemusique.adapters.MusicAdapter
import com.example.projetavancemusique.adapters.MusicFavorisAdapter
import com.example.projetavancemusique.database.AppDatabaseHelper
import com.example.projetavancemusique.models.MusicFavoris
import com.example.projetavancemusique.service.getAllMusicPhone
import com.example.projetavancemusique.service.MusicPhone


class MainActivity : AppCompatActivity() {
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicFavorisAdapter: MusicFavorisAdapter
    var isAdpaterFav : Boolean = false

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