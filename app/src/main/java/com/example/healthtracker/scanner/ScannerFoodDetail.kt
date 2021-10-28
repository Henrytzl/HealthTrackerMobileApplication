package com.example.healthtracker.scanner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.activity_scanner_food_detail.*

class ScannerFoodDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_food_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //if the food is already inside the food list
//        scannerFoodDetail_BtnAction.setBackgroundColor(Color.parseColor("#FF0000"))
//        scannerFoodDetail_BtnAction.text = "Delete from Food List"

        scannerFoodDetail_BtnOk.setOnClickListener {
            val foodName = scanner_foodName.text.toString()
            if(foodName == "Default Food Name"){
                Toast.makeText(this,"Please provide a name for the food", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"The food detail has been added into history", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        scannerFoodDetail_BtnAction.setOnClickListener {
            val foodName = scanner_foodName.text.toString()
            if(foodName == "Default Food Name"){
                Toast.makeText(this,"Please provide a name for the food", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"The food detail has been added into the food list", Toast.LENGTH_LONG).show()
            }
        }
    }
}