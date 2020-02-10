package com.example.cse438.cse438_assignment2.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import com.example.cse438.cse438_assignment2.R
import kotlinx.android.synthetic.main.clear_msg.*

// rollList for results of rolls
// seqList for sequence Number
val rollList = ArrayList<Int>()
val seqList = ArrayList<Int>()

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    // start RollActivity with bundles passing.
    fun roll(view: View) {
        val rollIntent = Intent(this, RollActivity::class.java)
        val diceNumber = diceNum.text.toString()
        val maxVal = maxValue.text.toString()
        if (diceNumber == "") {
            val myToast = Toast.makeText(this, "Please Enter Dice Number", Toast.LENGTH_SHORT)
            myToast.show()
        } else if (maxVal == "") {
            val myToast = Toast.makeText(this, "Please Enter Max Dice Value", Toast.LENGTH_SHORT)
            myToast.show()
        } else {
            var bundle = Bundle()
            bundle.putString("diceNumber", diceNumber)
            bundle.putString("maxVal", maxVal)
            rollIntent.putExtras(bundle)
            startActivity(rollIntent)
        }
    }


    // clear function to clear all data in rollList and seqList
    // raise an alert before clear
    fun clear(view: View) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.clear_msg, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Alert")

        val mAlertDialog = mBuilder.show()

        mAlertDialog.confirm.setOnClickListener {
            mAlertDialog.dismiss()
        }

        rollList.clear()
        seqList.clear()
    }
}
