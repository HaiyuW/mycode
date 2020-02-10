package com.example.cse438.caloriecounter

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.widget.Toast
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.enter_cal.*
import kotlinx.android.synthetic.main.enter_cal.view.*


class GreetingActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.greeting)
        val time: Int = 3000
        val handler = Handler()
        handler.postDelayed(Runnable {
            val intent = Intent(this@GreetingActivity, MainActivity::class.java)
            startActivity(intent)
            this@GreetingActivity.finish()
        }, time.toLong())
    }

}

class MainActivity : AppCompatActivity() {

    private var listofFood = ArrayList<String>()
    private var listofCal = ArrayList<Int>()

    private var listView: ListView? = null

    private var Cal = 0

    private var remainCal = 0

    private var totalCal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getInitial()
        dialogView()
    }

    private fun getInitial() {
        listView = Food

        val adapter = FoodAdapter(this, listofFood, listofCal)

        listView?.adapter = adapter

        adapter.notifyDataSetChanged()
    }

    private fun dialogView(){

        val dialogView = LayoutInflater.from(this).inflate(R.layout.enter_cal, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Enter Total Calories")

        val mAlertDialog = mBuilder.show()

        mAlertDialog.submitCal.setOnClickListener {
            val temp = dialogView.calorie.text.toString()
            if (temp != ""){
                mAlertDialog.dismiss()
                Cal = temp.toInt()
                remainCal = Cal
                remainNum.text = remainCal.toString()
            }
        }
    }

    fun addFood(view: View?) {
        val foodToAdd = newFood.text.toString()
        val calToAdd = newCal.text.toString()
        if (foodToAdd == "") {
            val myToast = Toast.makeText(this, "Please enter food", Toast.LENGTH_SHORT)
            myToast.show()
        }
        else if (calToAdd == "") {
            val myToast = Toast.makeText(this, "Please enter calories", Toast.LENGTH_SHORT)
            myToast.show()
        }
        else {
            listofFood.add(foodToAdd)
            listofCal.add(calToAdd.toInt())
            remainCal -= calToAdd.toInt()
            if (remainCal<0) {
                val msg = Toast.makeText(this, "Over Set Calories", Toast.LENGTH_SHORT)
                msg.show()
                remainNum.setTextColor(Color.RED)
            }
            totalCal += calToAdd.toInt()
            remainNum.text = remainCal.toString()
            totalNum.text = totalCal.toString()
            (listView?.adapter as? FoodAdapter)?.notifyDataSetChanged()
            newFood.setText("")
            newCal.setText("")
        }

    }
}
