package com.example.cse438.cse438_assignment2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse438.cse438_assignment2.Adapter.PlayListAdapter
import com.example.cse438.cse438_assignment2.Database.PlayList
import com.example.cse438.cse438_assignment2.PlayListViewModel
import com.example.cse438.cse438_assignment2.Activities.PlaylistActivity
import com.example.cse438.cse438_assignment2.R
import kotlinx.android.synthetic.main.fragment_playlist.*

// show all playlists and create button
class PlaylistFragment : Fragment() {

    private var listplaylist : ArrayList<PlayList> = ArrayList()
    private var playListViewModel: PlayListViewModel? = null
    public lateinit var createButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playListViewModel = ViewModelProvider(this).get(PlayListViewModel::class.java)

        val recyclerView = playlist_recycler_view
        val adapter = this.context?.let { PlayListAdapter(listplaylist, it, playListViewModel!!) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        createButton = createPlaylist

        // show all playlists
        playListViewModel!!.allPlayLists.observe(this, Observer { playlists ->
            listplaylist.clear()
            listplaylist.addAll(playlists)
            adapter!!.notifyDataSetChanged()
        })

        // create a new playlist
        createButton.setOnClickListener {
            val playlistIntent = Intent(this.context, PlaylistActivity::class.java)
            startActivity(playlistIntent)
        }

    }
}