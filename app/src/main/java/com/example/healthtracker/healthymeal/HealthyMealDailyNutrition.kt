package com.example.healthtracker.healthymeal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.activity_healthy_meal_daily_nutrition.*
import kotlinx.android.synthetic.main.activity_main.toolbar

class HealthyMealDailyNutrition : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthy_meal_daily_nutrition)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val calories=intent.getStringExtra("Kcal")
        txtNoDay.text = calories
    }
}