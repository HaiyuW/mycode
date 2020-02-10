package com.example.cse438.cse438_assignment2.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.fragments.HighFragment

class StatsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
    }

    // start the high fragments
    override fun onStart() {
        super.onStart()
        val fragment = HighFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container, fragment)
        transaction.commit()
    }

    // cancel function, head back to Main
    fun cancel(view: View) {
        val cancelIntent = Intent(this, MainActivity::class.java)
        startActivity(cancelIntent)
        this.finish()
    }

}