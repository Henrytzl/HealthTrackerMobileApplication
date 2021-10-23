package com.example.healthtracker

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class Result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_calculator_result)

        //store button
        val storeBtn = findViewById<Button>(R.id.buttonStore)

        storeBtn.setOnClickListener {
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
    }

}