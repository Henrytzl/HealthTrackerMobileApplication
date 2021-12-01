package com.example.healthtracker.healthymeal

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_food_detail.*

class FoodDetail : AppCompatActivity() {
    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Food Detail"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpFirebase()

        val foodID = intent.getStringExtra("foodID").toString()
        val foodRef = firebase.collection("Food")
        foodRef.document(foodID).get().addOnSuccessListener {
            foodDetail_foodName.text = it.get("foodName").toString()
            foodDetail_noOfUnit.text = it.get("noOfUnit").toString()
            foodKcal.text = it.get("kcal").toString()
            foodFat.text = it.get("fat").toString()
            foodProtein.text = it.get("protein").toString()
            foodCarb.text = it.get("carb").toString()
            foodSugar.text = it.get("sugar").toString()
        }.addOnFailureListener {
            Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT)
        }


        foodDetail_BtnOk.setOnClickListener {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}