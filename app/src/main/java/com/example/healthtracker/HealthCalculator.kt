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

            //Daily Calories variables
            var hDcValue = 0.0
            var dcValue = 0.0
            val a1 = 1.2
            val a2 = 1.4
            val a3 = 1.6
            val a4 = 1.75
            val a5 = 2
            val a6 = 2.3

            //BMI
            if (autoCompleteTextViewGender.text.toString()
                    .isNotEmpty() && textInputEditTextAge.text.toString()
                    .isNotEmpty() && textInputEditTextWeight.text.toString()
                    .isNotEmpty() && textInputEditTextHeight.text.toString()
                    .isNotEmpty() && autoCompleteTextViewActivityLvl.text.toString().isNotEmpty()
            ) {
                ageV = age
                wValue = textInputEditTextWeight.text.toString().toDouble()
                hValue = (textInputEditTextHeight.text.toString().toDouble() / 100)
                hDcValue = (textInputEditTextHeight.text.toString().toDouble())

                if (autoCompleteTextViewGender.text.toString() == "Male") {
                    genderV = male
                    //DC male
                    if (autoCompleteTextViewActivityLvl.text.toString() == "little/no exercise (sedentary lifestyle)") {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) + 5) * a1
                    } else if (autoCompleteTextViewActivityLvl.text.toString() == ("light exercise 1-2 times/weeks")) {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) + 5) * a2
                    } else if (autoCompleteTextViewActivityLvl.text.toString() == ("moderate exercise 2-3 times/weeks")) {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) + 5) * a3
                    } else if (autoCompleteTextViewActivityLvl.text.toString() == ("hard exercise 4-5 times/weeks")) {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) + 5) * a4
                    } else if (autoCompleteTextViewActivityLvl.text.toString() == ("physical job/hard exercise 6-7 times/weeks")) {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) + 5) * a5
                    } else if (autoCompleteTextViewActivityLvl.text.toString() == ("professional athlete")) {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) + 5) * a6
                    }
                } else {
                    genderV = female
                    //DC female
                    if (autoCompleteTextViewActivityLvl.text.toString() == "little/no exercise (sedentary lifestyle)") {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) - 161) * a1
                    } else if (autoCompleteTextViewActivityLvl.text.toString() == ("light exercise 1-2 times/weeks")) {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) - 161) * a2
                    } else if (autoCompleteTextViewActivityLvl.text.toString() == ("moderate exercise 2-3 times/weeks")) {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) - 161) * a3
                    } else if (autoCompleteTextViewActivityLvl.text.toString() == ("hard exercise 4-5 times/weeks")) {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) - 161) * a4
                    } else if (autoCompleteTextViewActivityLvl.text.toString() == ("physical job/hard exercise 6-7 times/weeks")) {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) - 161) * a5
                    } else if (autoCompleteTextViewActivityLvl.text.toString() == ("professional athlete")) {
                        dcValue = ((10 * wValue) + (6.25 * hDcValue) - (5 * ageV) - 161) * a6
                    }
                }

                //BFP
                val bmiValueRound = String.format("%.1f", bmiValue).toDouble()
                if (ageV <= 18) {
                    bfpValue = (1.51 * bmiValueRound) - (0.70 * ageV) - (3.6 * genderV) + 1.4
                } else {
                    bfpValue = (1.39 * bmiValueRound) + (0.16 * ageV) - (10.34 * genderV) - 9
                }

                //BMI
                if (wValue > 0.0 && hValue > 0.0) {
                    bmiValue = wValue / hValue.pow(2)
                    bmiValue.toString().format("%.1f").toDouble()
                    Toast.makeText(this, "Calculate Successfully!!!", Toast.LENGTH_SHORT).show()
                    intent.putExtra("BMI", bmiValue)
                    intent.putExtra("BFP", bfpValue.roundToInt())
                    intent.putExtra("DC", dcValue.roundToInt())
                    startActivity(intent)
                } else
                    Toast.makeText(
                        this,
                        "Please input weight and height values greater than 0.",
                        Toast.LENGTH_LONG
                    ).show()

            } else Toast.makeText(this, "Please enter all the fields.", Toast.LENGTH_SHORT).show()

        }
    }

}