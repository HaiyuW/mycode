package com.example.cse438.caloriecounter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class FoodAdapter (private var activity: Activity, private var items: ArrayList<String>, private var nums: ArrayList<Int>): BaseAdapter() {

    private class ViewHolder(row: View?){
        var foodName: TextView? = null
        var remainCal: TextView? = null
        var totalCal: TextView? = null
        var calNum: TextView? = null

        init {
            this.foodName = row?.findViewById(R.id.foodName)
            this.remainCal = row?.findViewById(R.id.remainNum)
            this.totalCal = row?.findViewById(R.id.totalNum)
            this.calNum = row?.findViewById(R.id.calNum)
        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.food_list, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        }
        else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val food = items[position]
        val cal = nums[position]
        viewHolder.foodName?.text = food
        viewHolder.calNum?.text = cal.toString()


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