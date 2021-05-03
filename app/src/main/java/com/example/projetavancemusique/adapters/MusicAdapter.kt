package com.example.projetavancemusique.adapters

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.projetavancemusique.MainActivity
import com.example.projetavancemusique.NotificationMusic
import com.example.projetavancemusique.R
import com.example.projetavancemusique.models.MusicFavoris
import com.example.projetavancemusique.service.MusicPhone
import com.example.projetavancemusique.service.MusicPlayerService


class MusicAdapter(private var listMusic: MutableList<MusicPhone>, private var mainActivity: MainActivity, private var musicFavorisAdapter: MusicFavorisAdapter) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>()
{
    // Crée chaque vue item à afficher :
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder
    {
        val viewMusic = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(viewMusic, mainActivity)
    }

    // Renseigne le contenu de chaque vue item :
    override fun onBindViewHolder(holder: MusicViewHolder, position: Int)
    {
        holder.textViewTitle.text = listMusic[position].title
        holder.textViewSize.text = "Taille : " + listMusic[position].size + " (en Mo)"
        holder.textViewDuration.text = "Durée : " + listMusic[position].duration + " (mm:ss)"
        if (listMusic[position].favorite) {
            holder.btnFavoris.setImageResource(R.drawable.ic_favorite_black_48dp);
        } else {
            holder.btnFavoris.setImageResource(R.drawable.ic_favorite_border_black_48dp);
        }
    }

    override fun getItemCount(): Int = listMusic.size

    fun removeItemFavorite(idPhone: Int) {
        val musicToDelete = listMusic.find { it.idPhone == idPhone }
        if (musicToDelete != null) {
            listMusic.remove(musicToDelete)
            notifyDataSetChanged()
        }
    }

    fun updateList(listMusic: MutableList<MusicPhone>)
    {
        this.listMusic = listMusic
        notifyDataSetChanged()
    }

    // ViewHolder :
    inner class MusicViewHolder(itemView: View, mainActivity: MainActivity) : RecyclerView.ViewHolder(itemView)
    {
        val textViewTitle: TextView = itemView.findViewById(R.id.title_music)
        val textViewSize: TextView = itemView.findViewById(R.id.size_music)
        val textViewDuration: TextView = itemView.findViewById(R.id.duration_music)

        val chooseMusic: ConstraintLayout = itemView.findViewById(R.id.music_item_box)
        val btnFavoris: ImageButton = itemView.findViewById(R.id.music_favoris_btn)

        var notificationManager = mainActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        init {
            chooseMusic.setOnClickListener {
                Log.d("tag-dev", listMusic[adapterPosition].location)
                val musicToListen = listMusic[adapterPosition]

                NotificationMusic().startMusic(mainActivity, MusicPlayerService.PLAYLIST_PHONE , adapterPosition)

                val action_btn: LinearLayout = mainActivity.findViewById(R.id.btn_player_liste)
                if (!action_btn.isVisible) {
                    action_btn.visibility = View.VISIBLE
                }
            }

            btnFavoris.setOnClickListener {
                val musicToFavoris = listMusic[adapterPosition]
                Log.d("tag-dev", musicToFavoris.toString())
                if (musicToFavoris.favorite) {
                    Log.d("tag-dev", "remove-fav")

                    //TODO Changer la manière de réatribution de la valeur (la virer (music) de la liste, et la remettre dans la liste)
                    listMusic[adapterPosition].favorite = false
                    musicFavorisAdapter.removeItemWithIdPhone(musicToFavoris.idPhone)
                } else {
                    Log.d("tag-dev", "add-fav")

                    //TODO Changer la manière de réatribution de la valeur
                    listMusic[adapterPosition].favorite = true
                    musicFavorisAdapter.addItem(MusicFavoris(0, musicToFavoris.title, musicToFavoris.size, musicToFavoris.duration, musicToFavoris.location, musicToFavoris.idPhone))
                }

                updateList(listMusic)
            }
        }
    }
}