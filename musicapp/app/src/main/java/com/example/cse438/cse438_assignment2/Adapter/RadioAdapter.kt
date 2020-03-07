package com.example.cse438.cse438_assignment2.Adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cse438.cse438_assignment2.Activities.RadioActivity
import com.example.cse438.cse438_assignment2.R
import com.example.cse438.cse438_assignment2.data.Radio
import com.example.cse438.cse438_assignment2.data.RadioData
import com.squareup.picasso.Picasso

// show radios in radioFragment
class RadioViewHolder(inflater: LayoutInflater, parent: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.track_item, parent, false)) {
    public val radioName : TextView
    public val radioPic: ImageView
    public var radioId: Int

    init {
        radioName = itemView.findViewById(R.id.track_name)
        radioPic = itemView.findViewById(R.id.track_pic)
        this.radioId = 0
    }

    fun bind(radio: Radio) {
        radioName.text = radio.title
        Picasso.get().load(radio.picture).into(radioPic)
        radioId = radio.id
    }
}

class RadioAdapter(private val list: ArrayList<Radio>, context: Context)
    : RecyclerView.Adapter<RadioViewHolder>() {
    val myContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RadioViewHolder(inflater,parent)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        val radio = list[position]
        holder.bind(radio)
        // after clicking the radio picture, show tracks in the radio
        holder.radioPic.setOnClickListener {
            val intent = Intent(myContext, RadioActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("radioId", holder.radioId)
            bundle.putString("radioName", holder.radioName.text.toString())

            intent.putExtras(bundle)
            myContext.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}