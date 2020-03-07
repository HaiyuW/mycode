package com.example.cse438.cse438_assignment2.fragments

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Query
import com.example.cse438.cse438_assignment2.Activities.MainActivity
import com.example.cse438.cse438_assignment2.DataViewModel
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.data.Song
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_view.*
import java.io.IOException

// show details of a certain track
class viewFragment : Fragment() {
    private lateinit var back_button : Button
    private lateinit var add_button: Button
    lateinit var viewModel: DataViewModel
    private var track_name: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = getArguments()
        val track_id = bundle!!.get("trackId").toString().toInt()
        val url : String = bundle.get("trackPreview").toString()
        val mp = MediaPlayer()

        // show the information of a track
        viewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        viewModel.getTrackById(track_id)
        viewModel.songList.observe(this, Observer { songs ->
            trackName.text = songs.title
            Picasso.get().load(songs.album.cover).into(trackPic)
            view_artist.text = "Artist: " + songs.artist.name
            view_trackName.text = songs.title
            view_position.text = "Position: " + songs.track_position.toString()
            view_length.text = "Length: " + songs.duration
            view_release.text ="Released: " + songs.release_date
            view_rank.text = "Rank: " + songs.rank
            view_gain.text = "Gain: " + songs.gain.toString()

        })

        // add button, to add to a playlist
        add_button = view.findViewById(R.id.addButton)

        add_button.setOnClickListener {
            val addTrackFragment = addTrackFragment()
            addTrackFragment.setArguments(bundle)
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.view_container, addTrackFragment)
            transaction.commit()
        }


        // back to the former page
        back_button = view.findViewById(R.id.backButton)

        back_button.setOnClickListener {
            mp.stop()
            mp.release()
            getActivity()?.finish()
        }

        //function for preview track button
        try{
            mp.setDataSource (url)
            mp.prepare ()
        }catch(e: IOException){
            val myToast =
                Toast.makeText(getActivity(), "File not exist", Toast.LENGTH_SHORT)
            myToast.show()
        }
        previewBtn.setOnClickListener{
            mp.start ()
            previewBtn.isEnabled = false
            stopBtn.isEnabled = true
        }
        stopBtn.isEnabled = false
        stopBtn.setOnClickListener{
            mp.pause()
            previewBtn.isEnabled = true
            stopBtn.isEnabled = false

        }
    }

}