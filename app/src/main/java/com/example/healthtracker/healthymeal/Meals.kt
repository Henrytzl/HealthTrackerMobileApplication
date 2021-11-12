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
    private var noOfDay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meals)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intentDay = intent.getStringExtra("day")
        if(!intentDay.isNullOrEmpty()){
            noOfDay = intentDay.toInt()!!
        }
        setUpFirebase()

        //Add Meal
        addMeal.setOnClickListener {
            if(noOfDay == 0){
                Toast.makeText(this, "Error Occurred", Toast.LENGTH_LONG).show()
            }else {
                val createMealView = LayoutInflater.from(this).inflate(R.layout.layout_create_meal, null)
                val createMealViewBuilder = AlertDialog.Builder(this, R.style.PopUpWindow).setView(createMealView).setTitle("Create a meal").setIcon(R.drawable.ic_meal1)
                //show dialog
                val displayDialog = createMealViewBuilder.show()

                //Cancel
                createMealView.btn_cancel.setOnClickListener {
                    displayDialog.dismiss()
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                }
                //send reset email
                createMealView.btn_create.setOnClickListener {
                    val mealName = createMealView.mealName.text.toString().trim()
                    if (mealName.isNotEmpty()) {
                        val documentRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document()
                        val mealDetailData = MealDetailDC(mealName, 0, 0, 0, 0, 0, noOfDay, documentRef.id)
                        documentRef.set(mealDetailData).addOnSuccessListener {
                            displayDialog.dismiss()
                            Toast.makeText(this, "Successfully created a meal", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MealDetail::class.java)
                            intent.putExtra("mealID", documentRef.id)
                            intent.putExtra("noOfDay", noOfDay)
                            startActivity(intent)
                        }
                    }
                }
            }
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