package com.example.cse438.cse438_assignment2.Activities

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cse438.cse438_assignment2.Adapter.DataListAdapter
import com.example.cse438.cse438_assignment2.DataViewModel
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.data.Track
import kotlinx.android.synthetic.main.playlist_show.*

// Show tracks in a radio
class RadioActivity : AppCompatActivity() {
    lateinit var backButton: Button
    lateinit var viewModel: DataViewModel
    var trackList: ArrayList<Track> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playlist_show)

        val intent = intent
        val bundle = intent.extras

        // get radioId and radioName from bundle
        val radioId = bundle.get("radioId").toString().toInt()
        val radioName = bundle.get("radioName").toString()

        tracklist_name.text = radioName

        // get DataViewModel
        viewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        // initiate recyclerView, adapter, layoutManager
        var recyclerView = show_recycler_view
        var adapter = DataListAdapter(trackList, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // show track by radioId
        viewModel.radioTrack.observe(this, Observer { radioTracks ->
            trackList.clear()
            trackList.addAll(radioTracks.data)
            adapter.notifyDataSetChanged()

        })

        viewModel.getTrackByRadio(radioId)

        // back to the former page
        backButton = backFromShow

        backButton.setOnClickListener {
            finish()
        }

    }

}