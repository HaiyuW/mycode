package com.example.cse438.cse438_assignment2.Adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cse438.cse438_assignment2.Activities.ViewActivity
import com.example.cse438.cse438_assignment2.Database.ResultList
import com.example.cse438.cse438_assignment2.PlayListViewModel
import com.example.cse438.cse438_assignment2.R


// show the tracks in the playlists
class ShowViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.song_item, parent, false)) {
    public val deleteButton: Button
    public var trackListId: Int
    public val songName: TextView
    public var trackId: Int
    public val artistName: TextView
    private val genre: TextView
    public val time: TextView
    public var duration : Int
    private val rating: TextView
    public var title: String
    public var trackPreview: String

    init {
        songName = itemView.findViewById(R.id.song_name)
        artistName = itemView.findViewById(R.id.artist_name)
        genre = itemView.findViewById(R.id.genre)
        time = itemView.findViewById(R.id.time)
        rating = itemView.findViewById(R.id.rating)
        deleteButton = itemView.findViewById(R.id.delete_song)
        this.trackListId = 0
        this.trackId = 0
        this.title = ""
        this.trackPreview = ""
        this.duration = 0

    }

    fun bind(resultList: ResultList) {
        trackListId = resultList.tracklistId
        songName.text = resultList.trackTitle
        artistName.text = resultList.artistName
        genre.text = resultList.genre
        time.text = "altTime: "+resultList.time.toString()+" "
        rating.text ="Rating: "+ resultList.rating.toString()
        trackId = resultList.trackId
        title = resultList.trackTitle
        trackPreview = resultList.trackPreview
        duration = resultList.time
    }
}

class ShowAdapter(private val list: ArrayList<ResultList>?, viewModel: PlayListViewModel, context: Context)
    : RecyclerView.Adapter<ShowViewHolder> () {
    private var listResults: ArrayList<ResultList>? = list
    private var viewModel = viewModel
    private var myContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ShowViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        val resultList = listResults!!.get(position)
        holder.bind(resultList)
        // delete the track in the playlists
        holder.deleteButton.setOnClickListener {
            viewModel!!.deleteTrackList(holder.trackListId)
        }
        // click the track title and show the information of the track
        holder.songName.setOnClickListener {
            var bundle = Bundle()
            bundle.putInt("trackId", holder.trackId)
            bundle.putInt("duration", holder.duration)
            bundle.putString("artist", holder.artistName.text.toString())
            bundle.putString("title",holder.title)
            bundle.putString("trackPreview",holder.trackPreview)

            val intent = Intent(myContext, ViewActivity::class.java)
            intent.putExtras(bundle)
            myContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }
}