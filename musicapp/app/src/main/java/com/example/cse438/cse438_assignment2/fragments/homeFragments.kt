package com.example.cse438.cse438_assignment2.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse438.cse438_assignment2.Adapter.DataListAdapter
import com.example.cse438.cse438_assignment2.DataViewModel
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.data.Track
import kotlinx.android.synthetic.main.fragment_home.*

// home page, show tracks and search bar
class HomeFragment: Fragment() {
    lateinit var viewModel: DataViewModel
    var trackList: ArrayList<Track> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        val recyclerView = home_recycler_view
        val adapter = this.context?.let { DataListAdapter(trackList as ArrayList<Track>, it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this.context,2)

        // show all tracks from chart
        viewModel!!.dataList.observe(this, Observer {datas ->
            trackList.clear()
            trackList.addAll(datas.data)
            adapter!!.notifyDataSetChanged()
        })

        viewModel.getDataByChart()

        // search bar to search certain tracks by either tracks or artists
        searchBar.setOnEditorActionListener() { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                val input:String = searchBar.text.toString()
                if(input != ""){
                    viewModel.getDataBySearch(input)
                }
                else{
                    viewModel.getDataByChart()
                }
                val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                true
            }
            false
        }

    }
}