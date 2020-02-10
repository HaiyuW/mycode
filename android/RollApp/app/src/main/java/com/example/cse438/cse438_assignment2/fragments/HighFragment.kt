package com.example.cse438.cse438_assignment2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cse438.cse438_assignment2.R

import kotlinx.android.synthetic.main.fragment_stats.*
import com.example.cse438.cse438_assignment2.activities.rollList


class HighFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    // set the Buttons and value
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scoreName.text = "Highest Roll"

        leftButton.text ="AVERAGE"
        rightButton.text = "LOWEST"


        val value = rollList.max()

        dispVal.text=value.toString()

        leftButton.setOnClickListener {
            val fragment = AvgFragment()
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frag_container, fragment)
            transaction.commit()
        }

        rightButton.setOnClickListener {
            val fragment = LowFragment()
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frag_container, fragment)
            transaction.commit()
        }

    }

}