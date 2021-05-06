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
import com.example.projetavancemusique.models.MusicFavoris
import com.example.projetavancemusique.models.MusicPhone
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

    fun updateList(listMusic: MutableList<MusicPhone>)
    {
        this.listMusic = listMusic
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
                val intent = Intent(mainActivity, MusicPlayerService::class.java)
                intent.putExtra(MusicPlayerService.EXTRA_PLAYLIST, MusicPlayerService.PLAYLIST_PHONE)
                intent.putExtra(MusicPlayerService.EXTRA_POSITION, adapterPosition)
                intent.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
                mainActivity.startService(intent)

                val actionBtn: LinearLayout = mainActivity.findViewById(R.id.btn_player_liste)
                if (!actionBtn.isVisible) {
                    actionBtn.visibility = View.VISIBLE
                }
            }

            // au clic sur l'icone favoris
            // on modifie l'icone favoris
            // on ajoute ou retire de la liste des favoris
            btnFavoris.setOnClickListener {
                val musicToFavoris = listMusic[adapterPosition]

                if (musicToFavoris.favorite) {
                    //TODO Changer la manière de réatribution de la valeur (la virer (music) de la liste, et la remettre dans la liste)
                    listMusic[adapterPosition].favorite = false
                    musicFavorisAdapter.removeItemWithIdPhone(musicToFavoris.idPhone)
                } else {
                    //TODO Changer la manière de réatribution de la valeur
                    listMusic[adapterPosition].favorite = true
                    musicFavorisAdapter.addItem(MusicFavoris(0, musicToFavoris.title, musicToFavoris.size, musicToFavoris.duration, musicToFavoris.location, musicToFavoris.idPhone))
                }

                updateList(listMusic)
            }
        }
    }
}