package com.example.cse438.cse438_assignment2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse438.cse438_assignment2.Adapter.AddListAdapter
import com.example.cse438.cse438_assignment2.Adapter.PlayListAdapter
import com.example.cse438.cse438_assignment2.Database.PlayList
import com.example.cse438.cse438_assignment2.Database.TrackList
import com.example.cse438.cse438_assignment2.PlayListViewModel
import com.example.cse438.cse438_assignment2.R
import kotlinx.android.synthetic.main.fragment_add.*

// add a track into the selected playlist
class addTrackFragment : Fragment() {

    private var listplaylist : ArrayList<PlayList> = ArrayList()
    private var playListViewModel: PlayListViewModel? = null
    lateinit var trackList: TrackList
    lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get information from bundle
        val bundle = getArguments()
        val id = bundle!!.get("trackId").toString().toInt()
        val title = bundle!!.get("title").toString()
        val time = bundle!!.get("duration").toString().toInt()
        val artist = bundle!!.get("artist").toString()
        val preview = bundle!!.get("trackPreview").toString()
        trackList = TrackList(0,title, 0, artist, time,id, preview)
        playListViewModel = ViewModelProvider(this).get(PlayListViewModel::class.java)

        val recyclerView = playlists
        val adapter =
            this.context?.let { AddListAdapter(listplaylist, trackList, playListViewModel!!, it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        // show all playlists
        playListViewModel!!.allPlayLists.observe(this, Observer { playlists ->
            listplaylist.clear()
            listplaylist.addAll(playlists)
            adapter!!.notifyDataSetChanged()
        })

        // back to the former page
        backButton = backFromAdd

        backButton.setOnClickListener {
            val viewFragment = viewFragment()
            viewFragment.setArguments(bundle)
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.view_container, viewFragment)
            transaction.commit()
        }
    }
}