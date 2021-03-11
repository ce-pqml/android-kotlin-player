package com.example.projetavancemusique.adapters

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projetavancemusique.MainActivity
import com.example.projetavancemusique.R
import com.example.projetavancemusique.database.AppDatabaseHelper
import com.example.projetavancemusique.models.MusicFavoris
import com.example.projetavancemusique.service.MusicPhone


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
//        Picasso.get().load("http://www.geognos.com/api/en/countries/flag/"+listCountry[position].alpha2Code+".png").into(holder.imgViewPreview);
        holder.textViewTitle.text = listMusic[position].title
        holder.textViewSize.text = "Taille : " + listMusic[position].size + " (en Mo)"
        holder.textViewDuration.text = "Durée : " + listMusic[position].duration + " (mm:ss)"
//        val csl = AppCompatResources.getColorStateList(mainActivity, R.color.teal_200)
//        ImageViewCompat.setImageTintList(holder.btnFavoris, csl);
        holder.btnFavoris.setImageResource(R.drawable.ic_favorite_black_48dp);
    }

    override fun getItemCount(): Int = listMusic.size

    fun addItem(musicFavoris: MusicFavoris)
    {
        listMusic.add(0, musicFavoris)
        AppDatabaseHelper.getDatabase(mainActivity)
            .musicFavorisDAO()
            .insert(musicFavoris)
        notifyItemChanged(0)
    }

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

    fun removeItem(position: Int) {
        val musicToDelete = listMusic[position]
        listMusic.removeAt(position)
        AppDatabaseHelper.getDatabase(mainActivity)
            .musicFavorisDAO()
            .delete(musicToDelete)
        notifyDataSetChanged()
    }

    fun updateList(listMusic: MutableList<MusicFavoris>)
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

        init {
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