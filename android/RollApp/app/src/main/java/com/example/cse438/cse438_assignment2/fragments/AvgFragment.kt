package com.example.cse438.cse438_assignment2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.activities.rollList

import kotlinx.android.synthetic.main.fragment_stats.*




class AvgFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    // Set the left and right button and value displayed
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scoreName.text = "Average Score"

        leftButton.text ="LOWEST"
        rightButton.text = "HIGHEST"


        val value = rollList.average().toInt()

        dispVal.text=value.toString()

        // leftButton head to Low
        leftButton.setOnClickListener {
            val fragment = LowFragment()
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frag_container, fragment)
            transaction.commit()
        }

        // rightButton head to High
        rightButton.setOnClickListener {
            val fragment = HighFragment()
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frag_container, fragment)
            transaction.commit()
        }

    }

}