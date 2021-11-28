package com.example.healthtracker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.dataclass.User
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.user_profile.*
import kotlinx.android.synthetic.main.user_profile.autoCompleteTextViewGender
import kotlinx.android.synthetic.main.user_profile.textInputLayoutAge
import kotlinx.android.synthetic.main.user_profile.textInputLayoutGender
import kotlinx.android.synthetic.main.user_profile.toolbar

class AuthorisedUser : AppCompatActivity() {
    private lateinit var authentication: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String
    private lateinit var option: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        //Tool Bar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpFirebase()

        disableEdit()

        //gender dropdown list
        val genderDd = resources.getStringArray(R.array.Gender)
        val adapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, genderDd)

        autoCompleteTextViewGender.setAdapter(adapter)

        //profile form
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("User").document(userID)
        userDocRef
            .get()
            .addOnSuccessListener { user ->
                textInputEditTextUsername.setText(user.getString("userName"))
                textInputEditTextEmail.setText(user.getString("email"))
                textInputEditTextAge.setText(user.get("age").toString())
                textInputLayoutAge.isEndIconVisible = false
                textInputEditTextCalories.setText(user.get("calories").toString())
                textInputLayoutCalories.isEndIconVisible = false
                autoCompleteTextViewGender.setText(user.get("gender").toString(), false)
            }

        //save button
        buttonSave.setOnClickListener {
            if (option == "edit") {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Alert!!!")
                alertDialog.setMessage("Do you want to save your profile details ?")
                alertDialog.setCancelable(true)
                alertDialog.setPositiveButton("Yes") { dialog, id ->
                    editProfile()
                }
                alertDialog.setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
                alertDialog.show()
            } else if (option == "change") {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Alert!!!")
                alertDialog.setMessage("Do you want to change your password ?")
                alertDialog.setCancelable(true)
                alertDialog.setPositiveButton("Yes") { dialog, id ->
                    chgPassword()
                }
                alertDialog.setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.editable, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editable -> {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Choose Option")
                alertDialog.setMessage("Choose EDIT to edit profile or choose CHANGE to change password")
                alertDialog.setCancelable(true)
                alertDialog.setPositiveButton("Edit") { dialog, id ->
                    enableEdit()
                    option = "edit"
                }
                alertDialog.setNegativeButton("Change") { dialog, id ->
                    enablePasswordEdit()
                    option = "change"
                }
                alertDialog.setNeutralButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }
                alertDialog.show()
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
        buttonSave.isEnabled = false
    }

    private fun enableEdit() {
        textInputLayoutUsername.isEnabled = true
        textInputLayoutAge.isEnabled = true
        textInputLayoutGender.isEnabled = true
        textInputLayoutCurrentPassword.isEnabled = false
        textInputLayoutNewPassword.isEnabled = false
        buttonSave.isEnabled = true
    }

    private fun enablePasswordEdit() {
        textInputLayoutUsername.isEnabled = false
        textInputLayoutAge.isEnabled = false
        textInputLayoutGender.isEnabled = false
        textInputLayoutCurrentPassword.isEnabled = true
        textInputLayoutNewPassword.isEnabled = true
        buttonSave.isEnabled = true
    }

    private fun editProfile() {
        if (textInputEditTextUsername.text.toString()
                .isNotEmpty() && textInputEditTextAge.text.toString()
                .isNotEmpty() && autoCompleteTextViewGender.text.toString().isNotEmpty()
        ) {

            if (textInputEditTextAge.text.toString().length <= 2) {
            val db = FirebaseFirestore.getInstance()
            val documentRef = db.collection("User").document(userID)
            val user = User(
                textInputEditTextUsername.text.toString(),
                textInputEditTextAge.text.toString().toInt(),
                autoCompleteTextViewGender.text.toString(),
                textInputEditTextCalories.text.toString().toInt(),
                textInputEditTextEmail.text.toString()
            )
            documentRef
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Saved Successfully!!!", Toast.LENGTH_SHORT).show()
                    disableEdit()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                }

            } else Toast.makeText(this, "Invalid age", Toast.LENGTH_SHORT).show()

        } else Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
    }

    private fun chgPassword() {
        if (textInputEditTextCurrentPassword.text.toString()
                .isNotEmpty() && textInputEditTextNewPassword.text.toString().isNotEmpty()
        ) {

            if (textInputEditTextCurrentPassword.text.toString() != textInputEditTextNewPassword.text.toString()) {
                if (textInputEditTextNewPassword.text.toString().length <= 5) {
                    Toast.makeText(
                        this,
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val user = authentication.currentUser
                    if (user != null && user.email != null) {
                        val credential = EmailAuthProvider
                            .getCredential(
                                user.email!!,
                                textInputEditTextCurrentPassword.text.toString()
                            )

                        // Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Re-Authentication success",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    user!!.updatePassword(textInputEditTextNewPassword.text.toString())
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Password changed successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                authentication.signOut()
                                                startActivity(
                                                    Intent(
                                                        this,
                                                        LoginActivity::class.java
                                                    )
                                                )
                                                finish()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Re-Authentication failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "The new password cannot same with current password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
        }
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

    private fun setUpFirebase() {
        authentication = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        if (authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}