package com.example.healthtracker.healthymeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import com.example.healthtracker.dataclass.MealDetailDC
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_meals.*
import kotlinx.android.synthetic.main.layout_create_meal.view.*

class Meals : AppCompatActivity() {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meals)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intentDay = intent.getStringExtra("day")
        val textDay : String = when (intentDay?.toInt()) {
            1 -> "Day 1"
            2 -> "Day 2"
            3 -> "Day 3"
            4 -> "Day 4"
            5 -> "Day 5"
            6 -> "Day 6"
            7 -> "Day 7"
            else -> null.toString()
        }
        setUpFirebase()

        //Add Meal
        addMeal.setOnClickListener {
            val createMealView = LayoutInflater.from(this).inflate(R.layout.layout_create_meal, null)
            val createMealViewBuilder = AlertDialog.Builder(this, R.style.PopUpWindow).setView(createMealView).setTitle("Reset Password").setIcon(R.drawable.ic_meal1)
            //show dialog
            val displayDialog = createMealViewBuilder.show()

            //Cancel
            createMealView.btn_cancel.setOnClickListener{
                displayDialog.dismiss()
                Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show()
            }
            //send reset email
            createMealView.btn_create.setOnClickListener{
                val documentRef = firebase.collection("Days").document(userID).collection(textDay).document()
                val mealDetailData = MealDetailDC(0,0,0,0,0)
                documentRef.set(mealDetailData)
            }
//            val intent = Intent(this, MealDetail::class.java)
//            //intent.putExtra("Kcal","2")
//            startActivity(intent)
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