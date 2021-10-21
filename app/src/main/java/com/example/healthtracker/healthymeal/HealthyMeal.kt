package com.example.healthtracker.healthymeal

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.login.LoginActivity
import kotlinx.android.synthetic.main.activity_healthy_meal.*

class HealthyMeal : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthy_meal)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.mHome -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
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

        imageHome.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        //View total nutrition value
        day1.setOnClickListener {
            val intent = Intent(this,HealthyMealDailyNutrition::class.java)
            intent.putExtra("Kcal","1")
            startActivity(intent)
        }
        day2.setOnClickListener {
            val intent = Intent(this,HealthyMealDailyNutrition::class.java)
            intent.putExtra("Kcal","2")
            startActivity(intent)
        }
        day3.setOnClickListener {

        }
        day4.setOnClickListener {

        }
        day5.setOnClickListener {

        }
        day6.setOnClickListener {

        }
        day7.setOnClickListener {

        }
        //Direct to Edit meal
        day1Edit.setOnClickListener {
            val intent = Intent(this,Meals::class.java)
            intent.putExtra("test","123")
            startActivity(intent)
        }
        day2Edit.setOnClickListener {

        }
        day3Edit.setOnClickListener {

        }
        day4Edit.setOnClickListener {

        }
        day5Edit.setOnClickListener {

        }
        day6Edit.setOnClickListener {

        }
        day7Edit.setOnClickListener {

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}