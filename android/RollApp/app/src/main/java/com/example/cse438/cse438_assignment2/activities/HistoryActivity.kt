package com.example.cse438.cse438_assignment2.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.example.cse438.cse438_assignment2.R
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    private var listView: View? = null


    // onCreate, show the lists and the summary
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        listView = resultList

        val adapter = RollAdapter(this, seqList, rollList)
        (listView as ListView?)?.adapter = adapter

        setValue()

        adapter.notifyDataSetChanged()

    }

    // complete the summary by traversing the rollList
    fun setValue () {
        var i: Int = 0
        var high: Int = 0
        var low: Int = 0
        var avg: Int = 0

        val cutoff = getCutOff()

        while (i < rollList.size) {
            if (rollList[i]>cutoff){
                high++
            } else if (rollList[i]<cutoff){
                low++
            }else{
                avg++
            }
            i++
        }
        highNum.text = high.toString()
        lowNum.text = low.toString()
        avgNum.text = avg.toString()

    }


    // head back to RollActivity
    fun back(view: View) {
        val historyIntent = intent
        val bundle = historyIntent.extras
        val backIntent = Intent(this, RollActivity::class.java)
        backIntent.putExtras(bundle)
        startActivity(backIntent)
        this.finish()
    }

    fun getCutOff (): Int{
        val historyIntent = intent
        val bundle = historyIntent.extras

        val diceNumber = bundle.get("diceNumber").toString().toInt()
        val maxVal = bundle.get("maxVal").toString().toInt()

        val max = maxVal*diceNumber
        val min = diceNumber

        val cutoff: Int = (max+min)/2

        return cutoff
    }

}