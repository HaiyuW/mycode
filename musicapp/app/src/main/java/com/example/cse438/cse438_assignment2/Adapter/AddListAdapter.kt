package com.example.cse438.cse438_assignment2.Adapter

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cse438.cse438_assignment2.Database.PlayList
import com.example.cse438.cse438_assignment2.Database.TrackList
import com.example.cse438.cse438_assignment2.PlayListViewModel
import com.example.cse438.cse438_assignment2.R

// show the list of playlists after clicking "ADD TO PLAYLIST"
class AddViewHolder(inflater: LayoutInflater,parent: ViewGroup, trackList: TrackList, viewModel: PlayListViewModel) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.add_item, parent, false)) {
    public val addButton: Button
    public val playlistName: TextView
    public var playlistId: Int
    private val playlistDesc: TextView
    private val trackList: TrackList
    private val viewModel: PlayListViewModel

    init {
        playlistName = itemView.findViewById(R.id.play_name)
        playlistDesc = itemView.findViewById(R.id.play_desc)
        addButton = itemView.findViewById(R.id.addButton)
        this.trackList = trackList
        this.viewModel = viewModel
        this.playlistId = 0
    }

    fun bind(playList: PlayList) {
        playlistName.text = playList.playlistName
        playlistDesc.text = playList.description
        playlistId = playList.playlistId
    }
}

class AddListAdapter (private val list: ArrayList<PlayList>?, trackList: TrackList, viewModel: PlayListViewModel, context: Context) :
    RecyclerView.Adapter<AddViewHolder>() {
    private var listPlayList : ArrayList<PlayList>? = list
    val track_list = trackList
    val viewModel = viewModel
    val context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AddViewHolder(inflater, parent, track_list, viewModel)
    }

    override fun onBindViewHolder(holder: AddViewHolder, position: Int) {
        val playlist : PlayList = listPlayList!!.get(position)
        holder.bind(playlist)

        // After clicking the add button near the each playlist, insert it into tracklist_table
        holder.addButton.setOnClickListener{
            track_list.playlistId = holder.playlistId
            viewModel!!.insertTracklist(track_list)
            Toast.makeText(context, "Add to "+holder.playlistName.text.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        return list!!.size
    }


}