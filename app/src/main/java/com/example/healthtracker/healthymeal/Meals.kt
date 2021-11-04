package com.example.healthtracker.healthymeal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.activity_meals.*

class Meals : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meals)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val calories=intent.getStringExtra("test")
//        test123.text = calories


        //Add Meal
        addMeal.setOnClickListener {
            val intent = Intent(this, MealDetail::class.java)
            //intent.putExtra("Kcal","2")
            startActivity(intent)
        }
    }
}