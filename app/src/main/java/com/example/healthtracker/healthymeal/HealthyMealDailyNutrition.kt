package com.example.healthtracker.healthymeal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_healthy_meal_daily_nutrition.*

class HealthyMealDailyNutrition : AppCompatActivity() {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthy_meal_daily_nutrition)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpFirebase()

        val day = intent.getStringExtra("day")

        val textDay = "Day $day"
        val dayTxt = when(day.toString().toInt()){
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            7 -> "Sunday"
            else -> ""
        }
        txtNoDay.text = dayTxt

        val dailyNutritionRef = firebase.collection("Days").document(userID).collection("$textDay").document("detail")
        val mealRef = firebase.collection("Meals").document(userID).collection("Meal Detail")
        var kcalSum: Int = 0; var carbSum: Int = 0; var fatSum: Int = 0; var proteinSum: Int = 0; var sugarSum: Int = 0;
        mealRef.whereEqualTo("noOfDay", day.toString().toInt()).get().addOnSuccessListener { result ->
            for(i in result.documents.indices) {
                kcalSum += result.documents[i].get("kcal").toString().toInt()
                carbSum += result.documents[i].get("carb").toString().toInt()
                fatSum += result.documents[i].get("fat").toString().toInt()
                proteinSum += result.documents[i].get("protein").toString().toInt()
                sugarSum += result.documents[i].get("sugar").toString().toInt()
            }
            dailyNutritionRef.update("carb", carbSum, "fat", fatSum, "kcal", kcalSum, "protein", proteinSum, "sugar", sugarSum).addOnSuccessListener {
                dailyNutritionRef.get().addOnSuccessListener { result ->
                    if(result != null){
                        dailyKcal.text = result.get("kcal").toString()
                        dailyProtein.text = result.get("protein").toString()
                        dailyFat.text = result.get("fat").toString()
                        dailyCarb.text = result.get("carb").toString()
                        dailySugar.text = result.get("sugar").toString()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT)
                }
            }.addOnFailureListener {
                Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT)
            }
        }.addOnFailureListener {
            Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT)
        }

        dailyBtnOk.setOnClickListener {
            finish()
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

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}