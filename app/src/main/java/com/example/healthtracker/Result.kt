package com.example.healthtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.health_calculator_result.*


class Result : AppCompatActivity() {
    private lateinit var authentication: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    //private lateinit var db: DocumentReference
    //private lateinit var resultID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_calculator_result)

        db = FirebaseFirestore.getInstance()
        //authentication = FirebaseAuth.getInstance()

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
                Toast.makeText(this, "$weightRound, $bmiRound, $bfp, $dc", Toast.LENGTH_SHORT).show()
                addFireStore(weightRound, bmiRound, bfp, dc)

                Toast.makeText(this, "Stored Successfully!!!", Toast.LENGTH_SHORT).show()
            }
            alertDialog.setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
            alertDialog.show()
            //startActivity(Intent(this, Result::class.java))
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

        db.collection("Results")
            .add(result)
            .addOnSuccessListener {
                Toast.makeText(this, "Stored liao la", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to store", Toast.LENGTH_SHORT).show()
            }
    }

}