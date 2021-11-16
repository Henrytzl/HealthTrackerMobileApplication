package com.example.healthtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.health_calculator_result.*
import kotlin.collections.HashMap

class Result : AppCompatActivity() {
    private lateinit var authentication: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_calculator_result)

        setSupportActionBar(toolbarResult)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpFirebase()

        //get values
        val weight = intent.getDoubleExtra("Weight", 0.0)
        val bmi = intent.getDoubleExtra("BMI", 0.0)
        val bfp = intent.getIntExtra("BFP", 0)
        val dc = intent.getIntExtra("DC", 0)
        val weightRound = String.format("%.2f", weight).toDouble()
        val bmiRound = String.format("%.1f", bmi).toDouble()

        bmiResult.text = "%.1f".format(bmi)
        bfpResult.text = "$bfp" + "%"
        dcResult.text = "$dc" + " kcal / day"

        //store button
        buttonStore.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Alert!!!")
            alertDialog.setMessage("Do you want to store your results ?")
            alertDialog.setCancelable(true)
            alertDialog.setPositiveButton("Yes") { dialog, id ->
                addFireStore(weightRound, bmiRound, bfp, dc)
            }
            alertDialog.setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
            alertDialog.show()
        }

        //again button
        buttonAgain.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Alert!!!")
            alertDialog.setMessage("Do you want to calculate again ?")
            alertDialog.setCancelable(true)
            alertDialog.setPositiveButton("Yes") { dialog, id ->
                startActivity(Intent(this, HealthCalculator::class.java))
                finish()
            }
            alertDialog.setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
    }

    private fun addFireStore(weightRound: Double, bmiRound: Double, bfp: Int, dc: Int) {
        val db = FirebaseFirestore.getInstance()
        val result: MutableMap<String, Any> = HashMap()
        result["weight"] = weightRound
        result["bmi"] = bmiRound
        result["bfp"] = bfp
        result["dc"] = dc
        result["date"] = FieldValue.serverTimestamp()

        val documentRef = db.collection("Results").document(userID)
        documentRef.collection("Result Details")
            .add(result)
            .addOnSuccessListener {
                Toast.makeText(this, "Stored Successfully!!!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to store", Toast.LENGTH_SHORT).show()
            }

        val updates: MutableMap<String, Any> = HashMap()
        updates["calories"] = dc

        val calDocRef = db.collection("User").document(userID)
        calDocRef
            .update(updates)
            .addOnSuccessListener {

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

    private fun setUpFirebase() {
        authentication = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        if (authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }

}