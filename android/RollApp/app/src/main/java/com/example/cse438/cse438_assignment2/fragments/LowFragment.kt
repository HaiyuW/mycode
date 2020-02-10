package com.example.cse438.cse438_assignment2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.activities.rollList

import kotlinx.android.synthetic.main.fragment_stats.*




class LowFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    // set the Button and Value
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scoreName.text = "Lowest Roll"

        leftButton.text ="HIGHEST"
        rightButton.text = "AVERAGE"


        val value = rollList.min()

        dispVal.text=value.toString()

        leftButton.setOnClickListener {
            val fragment = HighFragment()
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frag_container, fragment)
            transaction.commit()
        }

        rightButton.setOnClickListener {
            val fragment = AvgFragment()
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frag_container, fragment)
            transaction.commit()
        }

    }

}