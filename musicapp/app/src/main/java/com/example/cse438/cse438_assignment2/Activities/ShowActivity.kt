package com.example.cse438.cse438_assignment2.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse438.cse438_assignment2.Adapter.ShowAdapter
import com.example.cse438.cse438_assignment2.Database.ResultList
import com.example.cse438.cse438_assignment2.PlayListViewModel
import com.example.cse438.cse438_assignment2.R
import kotlinx.android.synthetic.main.playlist_show.*

// show tracks in a playlists
class ShowActivity : AppCompatActivity() {

    lateinit var backButton: Button
    lateinit var viewModel: PlayListViewModel
    var resultList: ArrayList<ResultList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playlist_show)

        val intent = intent
        val bundle = intent.extras

        // get playlistname, playlistId from bundle
        val name = bundle.get("playlistname").toString()
        val id = bundle.get("playlistId").toString().toInt()

        tracklist_name.text = name

        viewModel = ViewModelProvider(this).get(PlayListViewModel::class.java)

        var adapter = ShowAdapter(resultList, viewModel,this)

        val recyclerView = show_recycler_view
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // get tracks with the target playlistId
        viewModel.getTrackList(id)
        viewModel!!.allResultLists.observe(this, Observer { results ->
            resultList.clear()
            val size = results.size
            var index: Int = 0
            while (index<size){
                if (results[index].playlistId.equals(id)){
                    resultList.add(results[index])
                }
                index++
            }
            adapter.notifyDataSetChanged()
        })

        // back to the former page
        backButton = backFromShow

        backButton.setOnClickListener {
            finish()
        }


    }


}