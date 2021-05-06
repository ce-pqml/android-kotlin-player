package com.example.projetavancemusique.adapters

import android.content.Intent
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
import com.example.projetavancemusique.R
import com.example.projetavancemusique.database.AppDatabaseHelper
import com.example.projetavancemusique.models.MusicFavoris
import com.example.projetavancemusique.models.MusicPhone
import com.example.projetavancemusique.service.MusicPlayerService


class MusicFavorisAdapter(private var listMusic: MutableList<MusicFavoris>, private var mainActivity: MainActivity, private var listMusicPhone : MutableList<MusicPhone>?) :
    RecyclerView.Adapter<MusicFavorisAdapter.MusicViewHolder>()
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
        holder.btnFavoris.setImageResource(R.drawable.ic_favorite_black_48dp);
    }

    override fun getItemCount(): Int = listMusic.size

    // ajout d'une musique en favoris
    fun addItem(musicFavoris: MusicFavoris)
    {
        listMusic.add(listMusic.size, musicFavoris)
        val id = AppDatabaseHelper.getDatabase(mainActivity)
            .musicFavorisDAO()
            .insert(musicFavoris)
        musicFavoris.id = id
        notifyDataSetChanged()
    }

    // on retire une musique avec son id de liste non-favoris (telephone)
    fun removeItemWithIdPhone(idPhone: Int) {
        val musicToDelete = listMusic.find { it.idPhone == idPhone }
        if (musicToDelete != null) {
            listMusic.remove(musicToDelete)

            AppDatabaseHelper.getDatabase(mainActivity)
                .musicFavorisDAO()
                .delete(musicToDelete)

            notifyDataSetChanged()
        }
    }

    // on retire une musique favoris avec sa position
    fun removeItem(position: Int) {
        val musicToDelete = listMusic[position]
        listMusic.removeAt(position)

        AppDatabaseHelper.getDatabase(mainActivity)
            .musicFavorisDAO()
            .delete(musicToDelete)

        notifyDataSetChanged()

        val intent = Intent(mainActivity, MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.EXTRA_GET_PLAYLIST, MusicPlayerService.PLAYLIST_FAVORIS)
        mainActivity.startService(intent)
    }

    // ViewHolder :
    inner class MusicViewHolder(itemView: View, mainActivity: MainActivity) : RecyclerView.ViewHolder(itemView)
    {
        val textViewTitle: TextView = itemView.findViewById(R.id.title_music)
        val textViewSize: TextView = itemView.findViewById(R.id.size_music)
        val textViewDuration: TextView = itemView.findViewById(R.id.duration_music)

        val chooseMusic: ConstraintLayout = itemView.findViewById(R.id.music_item_box)
        val btnFavoris: ImageButton = itemView.findViewById(R.id.music_favoris_btn)

        init {
            // au clic on indique au service de lancer la musique et la notification
            // en ajoutant un intent avec les infos nécessaire
            chooseMusic.setOnClickListener {
                val actionBtn: LinearLayout = mainActivity.findViewById(R.id.btn_player_liste)
                if (!actionBtn.isVisible) {
                    actionBtn.visibility = View.VISIBLE
                }

                val intent = Intent(mainActivity, MusicPlayerService::class.java)
                intent.putExtra(MusicPlayerService.EXTRA_PLAYLIST, MusicPlayerService.PLAYLIST_FAVORIS)
                intent.putExtra(MusicPlayerService.EXTRA_POSITION, adapterPosition)
                intent.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
                mainActivity.startService(intent)
            }

            // au clic on modifie l'icone favoris dans la liste des non-favoris (telephone)
            // et on retire de la liste des favoris
            btnFavoris.setOnClickListener {
                val musicFavorisToRemove = listMusic[adapterPosition]
                if (listMusicPhone != null) {
                    val indexToChange = listMusicPhone!!.indexOfFirst{ it.idPhone == musicFavorisToRemove.idPhone }
                    if (indexToChange != null) {
                        listMusicPhone!![indexToChange].favorite = false
                    }
                }
                removeItem(adapterPosition)
            }
        }
    }
}