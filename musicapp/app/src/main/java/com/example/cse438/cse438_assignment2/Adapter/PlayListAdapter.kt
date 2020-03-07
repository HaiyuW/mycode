package com.example.cse438.cse438_assignment2.Adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cse438.cse438_assignment2.Activities.ShowActivity
import com.example.cse438.cse438_assignment2.Database.PlayList
import com.example.cse438.cse438_assignment2.PlayListViewModel
import com.example.cse438.cse438_assignment2.R

// show playlists in PlaylistFragment
class PlayListViewHolder(inflater: LayoutInflater, parent: ViewGroup, context: Context) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.playlist_item, parent, false)) {
    public var playlistId: Int
    public val playlistName: TextView
    public val deleteButton : Button
    private val playlistDesc: TextView
    private val context: Context

    init {
        playlistName = itemView.findViewById(R.id.play_name)
        playlistDesc = itemView.findViewById(R.id.play_desc)
        deleteButton = itemView.findViewById(R.id.deletePlaylist)
        this.context = context
        this.playlistId=0
    }

    fun bind(playList: PlayList) {
        playlistId = playList.playlistId
        playlistName.text = playList.playlistName
        playlistDesc.text = playList.description
    }
}


class PlayListAdapter (private val list: ArrayList<PlayList>?, context: Context, viewModel: PlayListViewModel) :
        RecyclerView.Adapter<PlayListViewHolder>() {
    private var listPlayList : ArrayList<PlayList>? = list
    val context = context
    val viewModel = viewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlayListViewHolder(inflater, parent, parent.context)
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        val playlist : PlayList = listPlayList!!.get(position)
        holder.bind(playlist)
        // After clicking the playlistName, show the contents of the playlists
        holder.playlistName.setOnClickListener {
            val intent = Intent(context, ShowActivity::class.java)
            val bundle = Bundle()
            bundle.putString("playlistname", holder.playlistName.text.toString())
            bundle.putInt("playlistId", holder.playlistId)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
        // delete the playlist and all tracks in that list
        holder.deleteButton.setOnClickListener {
            viewModel.deleteTrack(holder.playlistId)
            viewModel.deletePlaylist(holder.playlistId)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }


}