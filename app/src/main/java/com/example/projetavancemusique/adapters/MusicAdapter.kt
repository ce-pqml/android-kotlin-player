package com.example.projetavancemusique.adapters

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.projetavancemusique.MainActivity
import com.example.projetavancemusique.NotificationMusic
import com.example.projetavancemusique.R
import com.example.projetavancemusique.models.MusicFavoris
import com.example.projetavancemusique.service.MusicPhone
import com.example.projetavancemusique.service.MusicPlayerService
import com.example.projetavancemusique.service.TestService


class MusicAdapter(private var listMusic: MutableList<MusicPhone>, private var mainActivity: MainActivity, private var musicFavorisAdapter: MusicFavorisAdapter) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>()
{
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

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
        Log.d("tag-dev", listMusic[position].idPhone.toString())
        Log.d("tag-dev", listMusic[position].favorite.toString())
        if (listMusic[position].favorite) {
//            val csl = AppCompatResources.getColorStateList(mainActivity, R.color.teal_200)
//            ImageViewCompat.setImageTintList(holder.btnFavoris, csl);
            holder.btnFavoris.setImageResource(R.drawable.ic_favorite_black_48dp);
        } else {
//            val csl = AppCompatResources.getColorStateList(mainActivity, R.color.black)
//            ImageViewCompat.setImageTintList(holder.btnFavoris, csl);
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

                NotificationMusic().startMusic(mainActivity, musicToListen.location)

//                val intent = Intent(mainActivity, MusicPlayerService::class.java)

//                val playlistString : ArrayList<String> = arrayListOf()
//                listMusic.forEach { entry -> playlistString.add(entry.location) }
//                Log.d("tag-dev", playlistString[1])
//                intent.putStringArrayListExtra("playlist", playlistString)
//                intent.putExtra("position", adapterPosition)

//                intent.putExtra("music", musicToListen.location)
//                intent.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
//                mainActivity.startService(intent)

//                NotificationMusic().startMusic(mainActivity, listMusic, adapterPosition)
//                val playlistString : ArrayList<String> = arrayListOf()
//                listMusic.forEach { entry -> playlistString.add(entry.location) }
//                val intent = Intent(mainActivity, MusicPlayerService::class.java)
//                intent.putExtra("playlist", playlistString)
//                intent.putExtra("position", adapterPosition)
//                intent.putExtra(MusicPlayerService.EXTRA_COMMANDE, MusicPlayerService.COMMANDE_PLAY)
//                Log.d("tag-dev", "start music service")
//                mainActivity.startService(intent)


//                val remoteViews:RemoteViews = RemoteViews(mainActivity.getPackageName(), R.layout.notification_music)
////                remoteViews.setOnClickPendingIntent(R.id.play, pendingIntent)
////                remoteViews.setOnClickPendingIntent(R.id.pause, pendingIntent)
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
//                    notificationChannel.enableLights(true)
//                    notificationChannel.lightColor = Color.GREEN
//                    notificationChannel.enableVibration(false)
//                    notificationManager.createNotificationChannel(notificationChannel)
//
//                    builder = Notification.Builder(mainActivity, channelId)
//                            .setContentText("test")
//                            .setSmallIcon(R.drawable.ic_launcher_background)
//                            .setLargeIcon(BitmapFactory.decodeResource(mainActivity.resources, R.drawable.ic_launcher_background))
////                            .setContentIntent(pendingIntent)
//                            .setCustomContentView(remoteViews)
//                            .setOngoing(true)
//                            .setAutoCancel(true)
//                } else {
//
//                    builder = Notification.Builder(mainActivity)
//                            .setContentText("test")
//                            .setSmallIcon(R.drawable.ic_launcher_background)
//                            .setLargeIcon(BitmapFactory.decodeResource(mainActivity.resources, R.drawable.ic_launcher_background))
////                            .setContentIntent(pendingIntent)
//                            .setCustomContentView(remoteViews)
//                            .setOngoing(true)
//                            .setAutoCancel(true)
//                }
//                notificationManager.notify(1234, builder.build())

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