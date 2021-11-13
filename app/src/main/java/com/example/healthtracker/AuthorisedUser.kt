package com.example.healthtracker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.dataclass.User
import com.example.healthtracker.login.LoginActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.user_profile.*
import kotlinx.android.synthetic.main.user_profile.autoCompleteTextViewGender
import kotlinx.android.synthetic.main.user_profile.textInputLayoutAge
import kotlinx.android.synthetic.main.user_profile.textInputLayoutGender
import kotlinx.android.synthetic.main.user_profile.toolbar


class AuthorisedUser : AppCompatActivity() {
    private lateinit var authentication: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        //Tool Bar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        authentication = FirebaseAuth.getInstance()

        //disableEdit()

        val usernameTxt = findViewById<TextInputEditText>(R.id.textInputEditTextUsername)
        val emailTxt = findViewById<TextInputEditText>(R.id.textInputEditTextEmail)
        val ageTxt = findViewById<TextInputEditText>(R.id.textInputEditTextAge)
        val caloriesTxt = findViewById<TextInputEditText>(R.id.textInputEditTextCalories)
        val genderTxt = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewGender)
        val currentTxt = findViewById<TextInputEditText>(R.id.textInputEditTextCurrentPassword)
        val newTxt = findViewById<TextInputEditText>(R.id.textInputEditTextNewPassword)



        //gender dropdown list
        val genderDdl = resources.getStringArray(R.array.Gender)
        val adapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, genderDdl)

        autoCompleteTextViewGender.setAdapter(adapter)

        //profile form
        val db = FirebaseFirestore.getInstance()
        val emailDocRef = db.collection("User").document(userID)
        emailDocRef
            .get()
            .addOnSuccessListener { email ->
                if (email != null) {
//                    textInputEditTextEmail.text = email.getString("email")
                }
            }
//        val caloriesDocRef = db.collection("User").document(userID)
//        caloriesDocRef.get().addOnSuccessListener { kcal ->
//            if(kcal != null){
//                textInputEditTextCalories.text.toString().toInt() = kcal.get("calories").toString().toInt()
//            }
//        }

        //save button
        buttonSave.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Alert!!!")
            alertDialog.setMessage("Do you want to save your profile details ?")
            alertDialog.setCancelable(true)
            alertDialog.setPositiveButton("Yes") { dialog, id ->
                val db = FirebaseFirestore.getInstance()
                val documentRef = db.collection("User").document(userID)
                val user = User(textInputEditTextUsername.text.toString(),  textInputEditTextAge.text.toString().toInt(), autoCompleteTextViewGender.text.toString(), 0, "")
                documentRef
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Saved Successfully!!!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                    }
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
        textInputLayoutUsername.isEnabled = false
        textInputLayoutEmail.isEnabled = false
        textInputLayoutAge.isEnabled = false
        textInputLayoutCalories.isEnabled = false
        textInputLayoutGender.isEnabled = false
        textInputLayoutCurrentPassword.isEnabled = false
        textInputLayoutNewPassword.isEnabled = false
    }

    private fun enableEdit() {
        textInputLayoutUsername.isEnabled = true
        textInputLayoutAge.isEnabled = true
        textInputLayoutGender.isEnabled = true
        textInputLayoutCurrentPassword.isEnabled = true
        textInputLayoutNewPassword.isEnabled = true
    }

    override fun onStart() {
        super.onStart()

        // Get current User id and email
        if (authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            finishAffinity()
        }
    }
}