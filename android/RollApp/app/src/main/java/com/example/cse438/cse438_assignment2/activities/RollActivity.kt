package com.example.cse438.cse438_assignment2.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.cse438.cse438_assignment2.R
import kotlinx.android.synthetic.main.activity_roll.*


class RollActivity : AppCompatActivity() {

    // onCreate, roll the dice.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll)
        val rollIntent = intent
        val bundle = rollIntent.extras

        // get the parameters from bundle
        val diceNumber = bundle.get("diceNumber").toString().toInt()
        val maxVal = bundle.get("maxVal").toString().toInt()

        val value = roll(diceNumber,maxVal)

        displayValue.text = value.toString()
        rollList.add(value)
        seqList.add(rollList.size)

        // set the color
        chooseColor(diceNumber,maxVal, value)

    }

    // choose the Colors
    fun chooseColor (diceNum: Int, maxVal: Int, value: Int) {
        val max: Int = diceNum*maxVal
        val min: Int = diceNum


        // cutoff is the average of the max and min
        val cutoff: Int = (max+min)/2

        if (value>cutoff){
            displayValue.setTextColor(Color.GREEN)
        } else if (value<cutoff) {
            displayValue.setTextColor(Color.RED)
        } else{
            displayValue.setTextColor(Color.BLACK)
        }

    }

    // get random number from each dice and add them all
    fun roll(diceNum: Int, maxVal: Int): Int {
        var i: Int = 0
        var sum: Int = 0
        while (i<diceNum) {
            sum += (1..maxVal).random()
            i++
        }
        return sum
    }

    // reroll the dice
    fun reRoll (view: View) {
        val rollIntent = intent
        val bundle = rollIntent.extras
        val diceNumber = bundle.get("diceNumber").toString().toInt()
        val maxVal = bundle.get("maxVal").toString().toInt()

        val value = roll(diceNumber,maxVal)

        displayValue.text = value.toString()
        rollList.add(value)
        seqList.add(rollList.size)

        chooseColor(diceNumber,maxVal, value)
    }

    // undo function
    fun undo(view: View) {
        var seq = rollList.size-1

        if(seq==0) {
            val myToast = Toast.makeText(this, "Only One Roll Left", Toast.LENGTH_SHORT)
            myToast.show()
        } else {
            // remove the last member
            rollList.remove(rollList[seq])
            seqList.remove(seqList[seq])

            seq--

            // fetch the currently last member and display
            val value = rollList[seq]
            displayValue.text = value.toString()

            val rollIntent = intent
            val bundle = rollIntent.extras
            val diceNumber = bundle.get("diceNumber").toString().toInt()
            val maxVal = bundle.get("maxVal").toString().toInt()

            chooseColor(diceNumber, maxVal, value)
        }
    }

    // history function, head to HistoryActivity
    // since it needs to head back, send the bundle to history
    fun history (view: View) {
        val rollIntent = intent
        val bundle = rollIntent.extras
        val historyIntent = Intent(this, HistoryActivity::class.java)
        historyIntent.putExtras(bundle)
        startActivity(historyIntent)
    }

    // home function, head back to Main
    fun home(view: View) {
        val backIntent = Intent(this, MainActivity::class.java)
        startActivity(backIntent)
        this.finish()
    }

    // see result function, head to Stats
    fun seeResults(view: View) {
        val seeIntent = Intent(this, StatsActivity::class.java)
        startActivity(seeIntent)
        this.finish()
    }
}