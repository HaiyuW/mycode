package com.example.cse438.cse438_assignment2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cse438.cse438_assignment2.Database.PlayList
import com.example.cse438.cse438_assignment2.Activities.MainActivity
import com.example.cse438.cse438_assignment2.PlayListViewModel
import com.example.cse438.cse438_assignment2.R
import kotlinx.android.synthetic.main.playlist_add.*

// add a playlist and insert into playlist_table
class addPlaylistFragment: Fragment() {
    private var playListViewModel: PlayListViewModel?= null
    public lateinit var cancelButton: Button
    public lateinit var createPlayListButton : Button
    public lateinit var inputName: EditText
    public lateinit var inputDesc: EditText
    public lateinit var inputGenre: EditText
    public lateinit var inputRating: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.playlist_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playListViewModel = ViewModelProvider(this).get(PlayListViewModel::class.java)

        createPlayListButton = create_playlist_button
        cancelButton = back_button
        inputName = playlist_name
        inputDesc = playlist_desc
        inputGenre = playlist_genre
        inputRating = playlist_rating

        // create a playlist and insert into playlist_table
        createPlayListButton.setOnClickListener{
            val inputNameVal: String? = inputName.text.toString()
            val inputDescVal: String? = inputDesc.text.toString()
            val inputGenreVal: String? = inputGenre.text.toString()
            val inputRatingVal: Int? = inputRating.text.toString().toInt()

            if (!inputDescVal!!.equals("") && !inputNameVal!!.equals("") && !inputGenreVal!!.equals("") && inputRatingVal!!.compareTo(0)>=0 && inputRatingVal!!.compareTo(10)<=0){
                playListViewModel!!.insertPlaylist(PlayList(0,inputNameVal as String, inputDescVal as String, inputGenreVal as String, inputRatingVal as Int))
                Toast.makeText(this.context, "Playlist Created", Toast.LENGTH_SHORT).show()
                getActivity()?.finish()
            }else{
                Toast.makeText(this.context, "Value cannot be null", Toast.LENGTH_SHORT).show()
            }

        }

        cancelButton.setOnClickListener {
            getActivity()?.finish()
        }
    }
}