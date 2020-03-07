package com.example.cse438.cse438_assignment2.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.fragments.viewFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_view.*

// show information of a certain track
class ViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

    }

    override fun onStart() {
        super.onStart()
        val fragment = viewFragment()
        val intent = intent
        val bundle = intent.extras
        // start viewFragment
        val transaction = supportFragmentManager.beginTransaction()
        fragment.setArguments(bundle)
        transaction.replace(R.id.view_container, fragment)
        transaction.commit()
    }

}