package com.example.healthtracker.healthymeal

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_healthy_meal.*
import kotlinx.android.synthetic.main.nav_header.view.*

class HealthyMeal : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthy_meal)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpFirebase()

        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.mHome -> {
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
                    authentication.signOut()
                    Toast.makeText(this,"Successfully Logout", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    finishAffinity()
                }
            }
            true
        }

        imageHome.setOnClickListener{
            finish()
        }
        //View total nutrition value
        day1.setOnClickListener {
            val intent = Intent(this,HealthyMealDailyNutrition::class.java)
            intent.putExtra("day","1")
            startActivity(intent)
        }
        day2.setOnClickListener {
            val intent = Intent(this,HealthyMealDailyNutrition::class.java)
            intent.putExtra("day","2")
            startActivity(intent)
        }
        day3.setOnClickListener {
            val intent = Intent(this,HealthyMealDailyNutrition::class.java)
            intent.putExtra("day","3")
            startActivity(intent)
        }
        day4.setOnClickListener {
            val intent = Intent(this,HealthyMealDailyNutrition::class.java)
            intent.putExtra("day","4")
            startActivity(intent)
        }
        day5.setOnClickListener {
            val intent = Intent(this,HealthyMealDailyNutrition::class.java)
            intent.putExtra("day","5")
            startActivity(intent)
        }
        day6.setOnClickListener {
            val intent = Intent(this,HealthyMealDailyNutrition::class.java)
            intent.putExtra("day","6")
            startActivity(intent)
        }
        day7.setOnClickListener {
            val intent = Intent(this,HealthyMealDailyNutrition::class.java)
            intent.putExtra("day","7")
            startActivity(intent)
        }

        //Direct to Edit meal
        day1Edit.setOnClickListener {
            val intent = Intent(this,Meals::class.java)
            intent.putExtra("day","1")
            startActivity(intent)
        }
        day2Edit.setOnClickListener {
            val intent = Intent(this,Meals::class.java)
            intent.putExtra("day","2")
            startActivity(intent)
        }
        day3Edit.setOnClickListener {
            val intent = Intent(this,Meals::class.java)
            intent.putExtra("day","3")
            startActivity(intent)
        }
        day4Edit.setOnClickListener {
            val intent = Intent(this,Meals::class.java)
            intent.putExtra("day","4")
            startActivity(intent)
        }
        day5Edit.setOnClickListener {
            val intent = Intent(this,Meals::class.java)
            intent.putExtra("day","5")
            startActivity(intent)
        }
        day6Edit.setOnClickListener {
            val intent = Intent(this,Meals::class.java)
            intent.putExtra("day","6")
            startActivity(intent)
        }
        day7Edit.setOnClickListener {
            val intent = Intent(this,Meals::class.java)
            intent.putExtra("day","7")
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()

        // Get current User id and email
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
            navView.getHeaderView(0).dhEmail.text = authentication.currentUser!!.email
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            finishAffinity()
        }
        // Get Drawer Header information from database
        val nameDocRef = firebase.collection("User").document(userID)
        nameDocRef.get().addOnSuccessListener { name ->
            if(name != null){
                navView.getHeaderView(0).dhName.text = name.getString("userName")
            }
        }
        val caloriesDocRef = firebase.collection("User").document(userID)
        caloriesDocRef.get().addOnSuccessListener { kcal ->
            if(kcal != null){
                navView.getHeaderView(0).dhKcal.text = kcal.get("calories").toString()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}