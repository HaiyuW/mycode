package com.example.cse438.cse438_assignment2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cse438.cse438_assignment2.Adapter.RadioAdapter
import com.example.cse438.cse438_assignment2.DataViewModel
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.data.Radio
import kotlinx.android.synthetic.main.fragment_radio.*

// show all radios
class RadioFragment: Fragment() {
    lateinit var viewModel: DataViewModel
    var radioList: ArrayList<Radio> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_radio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        val recyclerView = radio_recycler_view
        val adapter = this.context?.let { RadioAdapter(radioList, it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this.context, 2)

        // show all radios
        viewModel!!.radioList.observe(this, Observer { radiodatas ->
            radioList.clear()
            radioList.addAll(radiodatas.data)
            adapter!!.notifyDataSetChanged()
        })

        viewModel.getRadio()
    }
}