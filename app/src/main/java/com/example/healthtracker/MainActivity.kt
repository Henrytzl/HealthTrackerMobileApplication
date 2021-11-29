package com.example.healthtracker

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.healthymeal.HealthyMeal
import com.example.healthtracker.login.LoginActivity
import com.example.healthtracker.reminder.Reminder
import com.example.healthtracker.scanner.Scanner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpFirebase()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.mProfile -> {
                    startActivity(Intent(this, AuthorisedUser::class.java))
                }
                R.id.mFAQ -> {

                }
                R.id.mHelp -> {
                    startActivity(Intent(this, ChatBot::class.java))
                }
                R.id.mAboutUs -> {

                }
                R.id.mLogout -> {
                    authentication.signOut()
                    Toast.makeText(this, "Successfully Logout", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    finishAffinity()
                }
            }
            true
        }

        btnReminder.setOnClickListener {
            startActivity(Intent(this, Reminder::class.java))
        }

        btnHealthMeal.setOnClickListener {
            startActivity(Intent(this, HealthyMeal::class.java))
        }

        btnScanner.setOnClickListener {
            startActivity(Intent(this, Scanner::class.java))
        }

        btnHealthCalculator.setOnClickListener {
            startActivity(Intent(this, HealthCalculator::class.java))
        }

        btnArticle.setOnClickListener {
            startActivity(Intent(this, ArticleMenu::class.java))
        }

        btnTrend.setOnClickListener {
            startActivity(Intent(this, Trend::class.java))
        }

        btnVideo.setOnClickListener {
            startActivity(Intent(this, VideoMenu::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, AuthorisedUser::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        // Get current User id and email
        if (authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
            navView.getHeaderView(0).dhEmail.text = authentication.currentUser!!.email
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            finishAffinity()
        }
        // Get Drawer Header information from database
        val nameDocRef = firebase.collection("User").document(userID)
        nameDocRef.get().addOnSuccessListener { name ->
            if (name != null) {
                navView.getHeaderView(0).dhName.text = name.getString("userName")
            }
        }
        val caloriesDocRef = firebase.collection("User").document(userID)
        caloriesDocRef.get().addOnSuccessListener { kcal ->
            if (kcal != null) {
                navView.getHeaderView(0).dhKcal.text =
                    kcal.get("calories").toString().toInt().toString()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpFirebase() {
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
    }
}