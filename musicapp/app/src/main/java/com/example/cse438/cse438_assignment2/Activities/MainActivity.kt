package com.example.cse438.cse438_assignment2.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cse438.cse438_assignment2.DataViewModel
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.ViewPagerAdapter
import com.example.cse438.cse438_assignment2.data.Track
import com.example.cse438.cse438_assignment2.fragments.HomeFragment
import com.example.cse438.cse438_assignment2.fragments.PlaylistFragment
import com.example.cse438.cse438_assignment2.fragments.RadioFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: DataViewModel
    var trackList: ArrayList<Track> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tabAdapter =
            ViewPagerAdapter(supportFragmentManager)
        // Add fragment
        tabAdapter.addFragment(HomeFragment(), "HOME")
        tabAdapter.addFragment(PlaylistFragment(),"PLAYLISTS")
        tabAdapter.addFragment(RadioFragment(), "RADIO")
        viewPager?.adapter = tabAdapter
        tabs.setupWithViewPager(viewPager)

    }
}
