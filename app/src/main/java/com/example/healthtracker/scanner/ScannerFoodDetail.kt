package com.example.healthtracker.scanner

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import com.example.healthtracker.dataclass.FoodDC
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

        val extra = intent.getStringExtra("Action")
        if(extra == "C"){
            scannerFoodDetail_BtnAction.visibility = GONE
            scannerFoodDetail_BtnUpdate.visibility = GONE
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
                    val foodRef = firebase.collection("Food").document()
                    val historyRef = firebase.collection("Food History")
                    val food = FoodDC(foodName, kcal.toInt(), protein.toInt(), fat.toInt(), carb.toInt(), sugar.toInt(), noOfUnit.toInt(), userID, foodRef.id)
                    val foodHistory = FoodDC(foodName, kcal.toInt(), protein.toInt(), fat.toInt(), carb.toInt(), sugar.toInt(), noOfUnit.toInt(), userID, foodRef.id)
                    //save to database (History and food list)
                    foodRef.set(food).addOnSuccessListener {
                        historyRef.document(foodRef.id).set(foodHistory).addOnSuccessListener {
                            Toast.makeText(this, "Food detail is recorded in history and food list", Toast.LENGTH_SHORT).show()
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else if(extra == "S"){
            scannerFoodDetail_BtnAction.visibility = VISIBLE
            scannerFoodDetail_BtnUpdate.visibility = GONE
            scannerFoodDetail_BtnOk.setOnClickListener {
                val foodName = scanner_foodName.text.toString()
                val kcal = foodKcal.text.toString()
                val protein = foodProtein.text.toString()
                val fat = foodFat.text.toString()
                val carb = foodCarb.text.toString()
                val sugar = foodSugar.text.toString()
                val noOfUnit = noOfUnit.text.toString()
                if(inputValidation(foodName, kcal, protein, fat, carb, sugar, noOfUnit)){

                    val historyRef = firebase.collection("Food History").document()
                    val foodHistory = FoodDC(foodName, kcal.toInt(), protein.toInt(), fat.toInt(), carb.toInt(), sugar.toInt(), noOfUnit.toInt(), userID, historyRef.id)
                    //save to database (History)
                    historyRef.set(foodHistory).addOnSuccessListener {
                        Toast.makeText(this,"Food detail is recorded in history", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                    }
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

                    val foodRef = firebase.collection("Food").document()
                    val historyRef = firebase.collection("Food History").document()
                    val food = FoodDC(foodName, kcal.toInt(), protein.toInt(), fat.toInt(), carb.toInt(), sugar.toInt(), noOfUnit.toInt(), userID, foodRef.id)
                    val foodHistory = FoodDC(foodName, kcal.toInt(), protein.toInt(), fat.toInt(), carb.toInt(), sugar.toInt(), noOfUnit.toInt(), userID, foodRef.id)
                    //save to database (History and food list)
                    foodRef.set(food).addOnSuccessListener {
                        historyRef.set(foodHistory).addOnSuccessListener {
                            Toast.makeText(this, "Food detail is recorded in history and food list", Toast.LENGTH_SHORT).show()
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else if(extra == "H"){
            val intentFoodID = intent.getStringExtra("foodID")

            if(!intentFoodID.isNullOrEmpty()){
                scannerFoodDetail_BtnUpdate.visibility = VISIBLE
                scannerFoodDetail_BtnOk.text = "OK"
                val foodRef = firebase.collection("Food").document(intentFoodID)
                val foodHistoryRef = firebase.collection("Food History").document(intentFoodID)

                foodHistoryRef.get().addOnSuccessListener { result ->
                    if(result != null) {
                        if(result.getString("userID") == userID) {
                            scanner_foodName.setText(result.getString("foodName"))
                            noOfUnit.setText(result.get("noOfUnit").toString())
                            foodKcal.setText(result.get("kcal").toString())
                            foodProtein.setText(result.get("protein").toString())
                            foodFat.setText(result.get("fat").toString())
                            foodCarb.setText(result.get("carb").toString())
                            foodSugar.setText(result.get("sugar").toString())
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                }

                foodRef.get().addOnSuccessListener { result ->
                    if(result != null){
                        if(result.getString("userID") == userID){
                            scannerFoodDetail_BtnAction.text = "Delete from Food List"
                            scannerFoodDetail_BtnAction.setBackgroundResource(R.drawable.custom_button_red)
                            //Delete from food list
                            scannerFoodDetail_BtnAction.setOnClickListener {
                                val deleteViewBuilder = AlertDialog.Builder(this).setTitle("Delete Food from Food List")
                                    .setIcon(R.drawable.ic_delete2).setMessage("Are you sure to delete this food from food list?")
                                    .setCancelable(false).setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                                        dialog.dismiss()
                                        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                                    })
                                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                                        foodRef.delete().addOnSuccessListener {
                                            dialog.dismiss()
                                            finish()
                                            Toast.makeText(this, "Food deleted from food list", Toast.LENGTH_SHORT).show()
                                        }.addOnFailureListener {
                                            Toast.makeText(this," " + it.message, Toast.LENGTH_LONG).show()
                                        }
                                    })
                                //show dialog
                                val displayDialog = deleteViewBuilder.create()
                                displayDialog.show()
                            }

                            // Update both history food and food list details
                            scannerFoodDetail_BtnUpdate.setOnClickListener {
                                val foodName = scanner_foodName.text.toString()
                                val kcal = foodKcal.text.toString()
                                val protein = foodProtein.text.toString()
                                val fat = foodFat.text.toString()
                                val carb = foodCarb.text.toString()
                                val sugar = foodSugar.text.toString()
                                val noOfUnit = noOfUnit.text.toString()

                                if(inputValidation(foodName, kcal, protein, fat, carb, sugar, noOfUnit)){
                                    val foodUpdated = FoodDC(foodName, kcal.toInt(), protein.toInt(), fat.toInt(), carb.toInt(), sugar.toInt(), noOfUnit.toInt(), userID, intentFoodID)
                                    foodHistoryRef.set(foodUpdated).addOnSuccessListener {
                                        foodRef.set(foodUpdated).addOnSuccessListener {
                                            val listOfDocument: ArrayList<String> = ArrayList()
                                            val abc = firebase.collection("Meals").document(userID).collection("Meal Detail")
                                            abc.get().addOnSuccessListener { it ->
                                                for(i in it){
                                                    listOfDocument.add(i.reference.id)
                                                }
                                                for(i in listOfDocument.indices){
                                                    Toast.makeText(this,"${listOfDocument[i]}", Toast.LENGTH_SHORT).show()
                                                    val mealFoods = firebase.collection("Meals").document(userID).collection("Meal Detail").document(listOfDocument[i]).collection("Foods").document(intentFoodID)
                                                    firebase.runTransaction { transaction ->
                                                        transaction.update(mealFoods,
                                                            "carb", carb.toInt(),
                                                            "fat", fat.toInt(),
                                                            "foodName", foodName,
                                                            "kcal", kcal.toInt(),
                                                            "noOfUnit", noOfUnit.toInt(),
                                                            "protein", protein.toInt(),
                                                            "sugar", sugar.toInt()
                                                        )
                                                    }.addOnFailureListener {
                                                        Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                            Toast.makeText(this,"Food detail has been updated", Toast.LENGTH_SHORT).show()
                                            finish()
                                        }.addOnFailureListener {
                                            Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }.addOnFailureListener {
                                        Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                }

                //Add to food list
                scannerFoodDetail_BtnAction.setOnClickListener {
                    val addToFoodListRef = firebase.collection("Food")
                    foodHistoryRef.get().addOnSuccessListener { result ->
                        if(result != null){
                            val food: FoodDC = FoodDC(result.get("foodName").toString(), result.get("kcal").toString().toInt(), result.get("protein").toString().toInt(),
                                result.get("fat").toString().toInt(), result.get("carb").toString().toInt(), result.get("sugar").toString().toInt(), result.get("noOfUnit").toString().toInt(),
                                result.get("userID").toString(), result.get("foodID").toString())
                            addToFoodListRef.document(intentFoodID).set(food)
                            Toast.makeText(this,"Food added to food list", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                //Update history food only
                scannerFoodDetail_BtnUpdate.setOnClickListener {
                    val foodName = scanner_foodName.text.toString()
                    val kcal = foodKcal.text.toString()
                    val protein = foodProtein.text.toString()
                    val fat = foodFat.text.toString()
                    val carb = foodCarb.text.toString()
                    val sugar = foodSugar.text.toString()
                    val noOfUnit = noOfUnit.text.toString()

                    if(inputValidation(foodName, kcal, protein, fat, carb, sugar, noOfUnit)){
                        val foodHistoryUpdated = FoodDC(foodName, kcal.toInt(), protein.toInt(), fat.toInt(), carb.toInt(), sugar.toInt(), noOfUnit.toInt(), userID, intentFoodID)
                        foodHistoryRef.set(foodHistoryUpdated).addOnSuccessListener {
                            Toast.makeText(this,"Food detail in history has been updated", Toast.LENGTH_SHORT).show()
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this," " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                scannerFoodDetail_BtnOk.setOnClickListener {
                    finish()
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