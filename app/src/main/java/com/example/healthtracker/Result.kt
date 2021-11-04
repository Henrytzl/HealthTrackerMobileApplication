package com.example.healthtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.health_calculator_result.*


class Result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_calculator_result)

        //get values
        val bmi = intent.getDoubleExtra("BMI", 0.0)
        val bfp = intent.getIntExtra("BFP", 0)
        val dc = intent.getIntExtra("DC", 0)

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
            }
            alertDialog.setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
    }

}