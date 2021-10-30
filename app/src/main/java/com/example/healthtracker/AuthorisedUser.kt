package com.example.healthtracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class AuthorisedUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        disableEdit()



        val usernameTxt = findViewById<TextInputEditText>(R.id.textInputEditTextUsername)
        val emailTxt = findViewById<TextInputEditText>(R.id.textInputEditTextEmail)
        val ageTxt = findViewById<TextInputEditText>(R.id.textInputEditTextAge)
        val caloriesTxt = findViewById<TextInputEditText>(R.id.textInputEditTextCalories)
        val genderTxt = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewGender)
        val currentTxt = findViewById<TextInputEditText>(R.id.textInputEditTextCurrentPassword)
        val newTxt = findViewById<TextInputEditText>(R.id.textInputEditTextNewPassword)



        //gender dropdown list
        val genderDdl = resources.getStringArray(R.array.Gender)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewGender)
        val adapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, genderDdl)

        autoCompleteTextView.setAdapter(adapter)

        //save button
        val saveBtn = findViewById<Button>(R.id.buttonSave)

        saveBtn.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Alert!!!")
            alertDialog.setMessage("Do you want to save your profile details ?")
            alertDialog.setCancelable(true)
            alertDialog.setPositiveButton("Yes") { dialog, id ->
                Toast.makeText(this, "Saved Successfully!!!", Toast.LENGTH_SHORT).show()
            }
            alertDialog.setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.editable,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editable -> {
                enableEdit()
                Toast.makeText(this, "You Can Edit Now", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun disableEdit() {
        val username = findViewById<TextInputLayout>(R.id.textInputLayoutUsername)
        val email = findViewById<TextInputLayout>(R.id.textInputLayoutEmail)
        val age = findViewById<TextInputLayout>(R.id.textInputLayoutAge)
        val calories = findViewById<TextInputLayout>(R.id.textInputLayoutCalories)
        val gender = findViewById<TextInputLayout>(R.id.textInputLayoutGender)
        val current = findViewById<TextInputLayout>(R.id.textInputLayoutCurrentPassword)
        val new = findViewById<TextInputLayout>(R.id.textInputLayoutNewPassword)

        username.isEnabled = false
        email.isEnabled = false
        age.isEnabled = false
        calories.isEnabled = false
        gender.isEnabled = false
        current.isEnabled = false
        new.isEnabled = false
    }

    private fun enableEdit() {
        val age = findViewById<TextInputLayout>(R.id.textInputLayoutAge)
        val gender = findViewById<TextInputLayout>(R.id.textInputLayoutGender)
        val current = findViewById<TextInputLayout>(R.id.textInputLayoutCurrentPassword)
        val new = findViewById<TextInputLayout>(R.id.textInputLayoutNewPassword)

        age.isEnabled = true
        gender.isEnabled = true
        current.isEnabled = true
        new.isEnabled = true
    }

}