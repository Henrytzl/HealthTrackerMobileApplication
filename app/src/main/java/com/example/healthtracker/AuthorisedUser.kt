package com.example.healthtracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class AuthorisedUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        //gender dropdown list
        val gender = resources.getStringArray(R.array.Gender)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewGender)
        val adapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, gender)

        autoCompleteTextView.setAdapter(adapter)

        //save button
        val saveBtn = findViewById<Button>(R.id.buttonSave)

        saveBtn.setOnClickListener {
            Toast.makeText(this, "Saved Successfully!!!", Toast.LENGTH_SHORT).show()
        }
    }

}