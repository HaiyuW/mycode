package com.example.cse438.cse438_assignment2.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.fragments.addPlaylistFragment

class PlaylistActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
    }

    override fun onStart() {
        super.onStart()
        // start addplaylistFragment
        val fragment = addPlaylistFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.playlist_container, fragment)
        transaction.commit()
    }
}