package com.example.healthtracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.health_calculator_form.*
import kotlinx.android.synthetic.main.health_calculator_result.*
import kotlin.math.pow
import kotlin.math.roundToInt


class HealthCalculator : AppCompatActivity() {

    var age = 18

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_calculator_form)

        //gender dropdown list
        val gender = resources.getStringArray(R.array.Gender)
        val adapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, gender)

        autoCompleteTextViewGender.setAdapter(adapter)

        //activity level dropdown list
        val activityLvl = resources.getStringArray(R.array.Activity_Lvl)
        val adapter2 = ArrayAdapter(applicationContext, R.layout.dropdown_item, activityLvl)

        autoCompleteTextViewActivityLvl.setAdapter(adapter2)

        //age stuff
        val addBtn = findViewById<Button>(R.id.buttonAdd)
        val minusBtn = findViewById<Button>(R.id.buttonMinus)

        textInputEditTextAge.isEnabled = false

        //add button
        addBtn.setOnClickListener {
            age++
            if (age > 100) {
                addBtn.isEnabled = false
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            } else {
                minusBtn.isEnabled = true
                textInputEditTextAge.setText("$age")
                textInputEditTextAge.isEnabled = false
                textInputEditTextAge.setTextColor(Color.parseColor("#FF000000"))
            }
        }

        //minus button
        minusBtn.setOnClickListener {
            age--
            if (age <= 0) {
                minusBtn.isEnabled = false
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            } else {
                addBtn.isEnabled = true
                textInputEditTextAge.setText("$age")
                textInputEditTextAge.isEnabled = false
                textInputEditTextAge.setTextColor(Color.parseColor("#FF000000"))
            }
        }

        //weight stuff
        val btnKg = findViewById<Button>(R.id.buttonKg)
        val btnLb = findViewById<Button>(R.id.buttonLb)
        val weightTextLayout = findViewById<TextInputLayout>(R.id.textInputLayoutWeight)

        btnKg.setOnClickListener {
            btnKg.isSelected = true
            btnLb.setTextColor(Color.BLACK)
            btnKg.setBackgroundResource(R.drawable.custom_button_kg_checked)
            btnLb.setBackgroundResource(R.drawable.custom_button_lb_unchecked)
            btnKg.setTextColor(Color.parseColor("#FF6200EE"))
            weightTextLayout.suffixText = "Kg"
        }

        btnLb.setOnClickListener {
            btnLb.isSelected = true
            btnKg.setTextColor(Color.BLACK)
            btnLb.setBackgroundResource(R.drawable.custom_button_lb_checked)
            btnKg.setBackgroundResource(R.drawable.custom_button_kg_unchecked)
            btnLb.setTextColor(Color.parseColor("#FF6200EE"))
            weightTextLayout.suffixText = "Lb"
        }

        //height stuff
        val btnCm = findViewById<Button>(R.id.buttonCm)
        val btnIn = findViewById<Button>(R.id.buttonIn)
        val heightTextLayout = findViewById<TextInputLayout>(R.id.textInputLayoutHeight)

        btnCm.setOnClickListener {
            btnCm.isSelected = true
            btnIn.setTextColor(Color.BLACK)
            btnCm.setBackgroundResource(R.drawable.custom_button_cm_checked)
            btnIn.setBackgroundResource(R.drawable.custom_button_in_unchecked)
            btnCm.setTextColor(Color.parseColor("#FF6200EE"))
            heightTextLayout.suffixText = "Cm"
        }

        btnIn.setOnClickListener {
            btnIn.isSelected = true
            btnCm.setTextColor(Color.BLACK)
            btnIn.setBackgroundResource(R.drawable.custom_button_in_checked)
            btnCm.setBackgroundResource(R.drawable.custom_button_cm_unchecked)
            btnIn.setTextColor(Color.parseColor("#FF6200EE"))
            heightTextLayout.suffixText = "In"
        }

        //calculate button
        val calBtn = findViewById<Button>(R.id.buttonCalculate)

        calBtn.setOnClickListener {
            val intent = Intent(this, Result::class.java)

            //BMI variables
            var wValue = 0.0
            var hValue = 0.0
            var bmiValue = 0.0

            //BFP variables
            val male = 1
            val female = 0
            var genderV = 2
            var ageV = 0
            var bfpValue = 0.0

            //BMI
            if (textInputEditTextWeight.text.toString().isNotEmpty()) {
                wValue = textInputEditTextWeight.text.toString().toDouble()
            }
            if (textInputEditTextHeight.text.toString().isNotEmpty()) {
                hValue = (textInputEditTextHeight.text.toString().toDouble() / 100)
            }

            if (wValue > 0.0 && hValue > 0.0) {
                bmiValue = wValue / hValue.pow(2)
                bmiValue.toString().format("%.1f").toDouble()
            } else
                Toast.makeText(
                    this,
                    "Please input weight and height values greater than 0",
                    Toast.LENGTH_LONG
                ).show()

            //BFP
            if (autoCompleteTextViewGender.text.toString().isNotEmpty()) {
                if (autoCompleteTextViewGender.text.toString() == "Male") {
                    genderV = male
                } else
                    genderV = female
            }

            if (textInputEditTextAge.text.toString().isNotEmpty()) {
                ageV = age
                val bmiValueRound = String.format("%.1f", bmiValue).toDouble()
                bfpValue = (1.39 * bmiValueRound) + (0.16 * ageV) - (10.34 * genderV) - 9

            } else
                Toast.makeText(this, "Please select your age", Toast.LENGTH_LONG).show()

            Toast.makeText(this, "Calculate Successfully!!!", Toast.LENGTH_SHORT).show()
            intent.putExtra("BMI", bmiValue)
            intent.putExtra("BFP", bfpValue.roundToInt())
            startActivity(intent)

        }
    }

}