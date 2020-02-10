package com.example.cse438.cse438_assignment2.activities

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cse438.cse438_assignment2.R
import kotlinx.android.synthetic.main.roll_list.view.*

// RollAdapter, display list of rolls and seqs in HistoryActivity
// First ArrayList is Seq, second is results
class RollAdapter (private var activity: Activity, private var items: ArrayList<Int>, private var nums: ArrayList<Int>): BaseAdapter() {
    private class ViewHolder(row: View?) {
        var rollVal: TextView? = null
        var seqNum: TextView? = null

        init {
            this.rollVal = row?.findViewById(R.id.rollVal)
            this.seqNum = row?.findViewById(R.id.seqNum)
        }
    }

    override fun getView(position: Int, convertView: View?, p2: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater =
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.roll_list, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.rollVal?.text = items[position].toString()
        viewHolder.seqNum?.text = nums[position].toString()

        return view as View
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}