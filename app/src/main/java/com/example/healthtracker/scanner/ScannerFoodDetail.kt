package com.example.healthtracker.scanner

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_scanner_food_detail.*

class ScannerFoodDetail : AppCompatActivity() {
    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_food_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpFirebase()

        //if the food is already inside the food list
//        scannerFoodDetail_BtnAction.setBackgroundColor(Color.parseColor("#FF0000"))
//        scannerFoodDetail_BtnAction.text = "Delete from Food List"

        val extra = intent.getStringExtra("Action")
        if(extra == "C"){
            scannerFoodDetail_BtnAction.visibility = GONE
            scannerFoodDetail_BtnOk.text = "Save & Add to Food List"
            scannerFoodDetail_BtnOk.setOnClickListener {
                val foodName = scanner_foodName.text.toString()
                val kcal = foodKcal.text.toString()
                val protein = foodProtein.text.toString()
                val fat = foodFat.text.toString()
                val carb = foodCarb.text.toString()
                val sugar = foodSugar.text.toString()
                val noOfUnit = noOfUnit.text.toString()
                if(inputValidation(foodName, kcal, protein, fat, carb, sugar, noOfUnit)){
                    kcal.toInt()
                    protein.toInt()
                    fat.toInt()
                    carb.toInt()
                    sugar.toInt()
                    noOfUnit.toInt()

                    //save to database (History and food list)


                    Toast.makeText(this,"Food detail is recorded in history and food list", Toast.LENGTH_SHORT).show()
                    //finish()
                }
            }
        }
        else if(extra == "S"){
            scannerFoodDetail_BtnAction.visibility = VISIBLE
            scannerFoodDetail_BtnOk.setOnClickListener {
                val foodName = scanner_foodName.text.toString()
                val kcal = foodKcal.text.toString()
                val protein = foodProtein.text.toString()
                val fat = foodFat.text.toString()
                val carb = foodCarb.text.toString()
                val sugar = foodSugar.text.toString()
                val noOfUnit = noOfUnit.text.toString()
                if(inputValidation(foodName, kcal, protein, fat, carb, sugar, noOfUnit)){
                    kcal.toInt()
                    protein.toInt()
                    fat.toInt()
                    carb.toInt()
                    sugar.toInt()
                    noOfUnit.toInt()
                    //save to database (History)


                    Toast.makeText(this,"Food detail is recorded in history", Toast.LENGTH_SHORT).show()
                }
            }

            scannerFoodDetail_BtnAction.setOnClickListener {
                val foodName = scanner_foodName.text.toString()
                val kcal = foodKcal.text.toString()
                val protein = foodProtein.text.toString()
                val fat = foodFat.text.toString()
                val carb = foodCarb.text.toString()
                val sugar = foodSugar.text.toString()
                val noOfUnit = noOfUnit.text.toString()
                if(inputValidation(foodName, kcal, protein, fat, carb, sugar, noOfUnit)){
                    kcal.toInt()
                    protein.toInt()
                    fat.toInt()
                    carb.toInt()
                    sugar.toInt()
                    noOfUnit.toInt()
                    //save to database (History and food list)


                    Toast.makeText(this,"Food detail is recorded in history and food list", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun inputValidation(foodName: String, kcal: String, protein: String, fat:String, carb: String, sugar:String, noOfUnit: String) : Boolean{
        if(foodName.isEmpty() || kcal.isEmpty() || protein.isEmpty() || fat.isEmpty() || carb.isEmpty() || sugar.isEmpty() || noOfUnit.isEmpty()){
            Toast.makeText(this,"Please fill in all the food details", Toast.LENGTH_SHORT).show()
            return false
        }else if(!TextUtils.isDigitsOnly(kcal)){
            Toast.makeText(this,"Energy(Kcal) value must be in digit only", Toast.LENGTH_SHORT).show()
            return false
        }else if(!TextUtils.isDigitsOnly(protein)){
            Toast.makeText(this,"Protein value must be in digit only", Toast.LENGTH_SHORT).show()
            return false
        }else if(!TextUtils.isDigitsOnly(fat)){
            Toast.makeText(this,"Fat value must be in digit only", Toast.LENGTH_SHORT).show()
            return false
        }else if(!TextUtils.isDigitsOnly(carb)){
            Toast.makeText(this,"Carbohydrate value must be in digit only", Toast.LENGTH_SHORT).show()
            return false
        }else if(!TextUtils.isDigitsOnly(sugar)){
            Toast.makeText(this,"Sugar value must be in digit only", Toast.LENGTH_SHORT).show()
            return false
        }else if(!TextUtils.isDigitsOnly(noOfUnit)){
            Toast.makeText(this,"Serving value must be in digit only", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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