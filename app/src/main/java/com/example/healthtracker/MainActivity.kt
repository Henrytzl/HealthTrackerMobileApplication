package com.example.healthtracker

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.healthymeal.HealthyMeal
import com.example.healthtracker.login.LoginActivity
import com.example.healthtracker.reminder.Reminder
import com.example.healthtracker.scanner.Scanner
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.mProfile -> {

                }
                R.id.mFAQ -> {

                }
                R.id.mFeedback -> {

                }
                R.id.mHelp -> {

                }
                R.id.mAboutUs -> {

                }
                R.id.mLogout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
//        if (item != null && item.getItemId() == android.R.id.home) {
//            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//                drawerLayout.closeDrawer(Gravity.RIGHT);
//            }
//            else {
//                drawerLayout.openDrawer(Gravity.RIGHT);
//            }
//        }
        //return false
        return super.onOptionsItemSelected(item)
    }
}