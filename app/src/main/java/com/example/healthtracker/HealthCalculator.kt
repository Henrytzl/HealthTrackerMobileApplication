package com.example.healthtracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity


class HealthCalculator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_calculator_form)

        val gender = resources.getStringArray(R.array.Gender)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewGender)

        val adapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, gender)

        autoCompleteTextView.setAdapter(adapter)

        val activityLvl = resources.getStringArray(R.array.Activity_Lvl)
        val autoCompleteTextView2 = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewActivityLvl)
        val adapter2 = ArrayAdapter(applicationContext, R.layout.dropdown_item, activityLvl)

        autoCompleteTextView2.setAdapter(adapter2)
    }

}