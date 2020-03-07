package com.example.cse438.cse438_assignment2.Adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.Activities.ViewActivity
import com.example.cse438.cse438_assignment2.data.Track
import com.squareup.picasso.Picasso

// Show the track in the homeFragment
class DataViewHolder(inflater: LayoutInflater, parent: ViewGroup, context: Context):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.track_item, parent, false)) {
    private val trackNameView: TextView
    public val trackPicView: ImageView
    private val context: Context

    init {
        trackNameView = itemView.findViewById(R.id.track_name)
        trackPicView = itemView.findViewById(R.id.track_pic)
        this.context = context
    }

    fun bind(track: Track){
        trackNameView?.text = track.title
        Picasso.get().load(track.album.cover).into(trackPicView)
    }
}

class DataListAdapter(private val list: ArrayList<Track>, context: Context)
    : RecyclerView.Adapter<DataViewHolder>() {

    val myContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DataViewHolder(inflater, parent, parent.context)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val track: Track = list[position]
        holder.bind(track)
        // After clicking the picture of the track, start a new activity
        holder.trackPicView.setOnClickListener {
            var bundle = Bundle()
            bundle.putInt("trackId", track.id)
            bundle.putInt("duration", track.duration)
            bundle.putString("artist", track.artist.name)
            bundle.putString("title",track.title)
            bundle.putString("trackPreview",track.preview)

            val intent = Intent(myContext, ViewActivity::class.java)
            intent.putExtras(bundle)
            myContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
