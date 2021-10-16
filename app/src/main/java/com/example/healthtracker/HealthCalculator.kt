package com.example.healthtracker

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.health_calculator_form.*


class HealthCalculator : AppCompatActivity() {

    var age = 18

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_calculator_form)

        //gender dropdown list
        val gender = resources.getStringArray(R.array.Gender)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewGender)

        val adapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, gender)

        autoCompleteTextView.setAdapter(adapter)

        //activity level dropdown list
        val activityLvl = resources.getStringArray(R.array.Activity_Lvl)
        val autoCompleteTextView2 = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewActivityLvl)
        val adapter2 = ArrayAdapter(applicationContext, R.layout.dropdown_item, activityLvl)

        autoCompleteTextView2.setAdapter(adapter2)

        //age stuff
        val addBtn = findViewById<Button>(R.id.buttonAdd)
        val ageTextField = findViewById<TextInputEditText>(R.id.textInputEditTextAge)
        val ageTextLayout = findViewById<TextInputLayout>(R.id.textInputLayoutAge)
        val minusBtn = findViewById<Button>(R.id.buttonMinus)

        ageTextField.isEnabled = false

        //add button
        addBtn.setOnClickListener {
            age++
            if (age > 100) {
                addBtn.isEnabled = false
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            }
            else {
                minusBtn.isEnabled = true
                ageTextField.setText("$age")
                ageTextField.isEnabled = false
                ageTextField.setTextColor(Color.parseColor("#FF000000"))
            }
        }

        //minus button
        minusBtn.setOnClickListener {
            age--
            if(age <= 0 ) {
                minusBtn.isEnabled = false
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            }
            else {
                addBtn.isEnabled = true
                ageTextField.setText("$age")
                ageTextField.isEnabled = false
                ageTextField.setTextColor(Color.parseColor("#FF000000"))
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
            weightTextLayout.suffixText="Kg"
        }

        btnLb.setOnClickListener {
            btnLb.isSelected = true
            btnKg.setTextColor(Color.BLACK)
            btnLb.setBackgroundResource(R.drawable.custom_button_lb_checked)
            btnKg.setBackgroundResource(R.drawable.custom_button_kg_unchecked)
            btnLb.setTextColor(Color.parseColor("#FF6200EE"))
            weightTextLayout.suffixText="Lb"
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
            heightTextLayout.suffixText="Cm"
        }

        btnIn.setOnClickListener {
            btnIn.isSelected = true
            btnCm.setTextColor(Color.BLACK)
            btnIn.setBackgroundResource(R.drawable.custom_button_in_checked)
            btnCm.setBackgroundResource(R.drawable.custom_button_cm_unchecked)
            btnIn.setTextColor(Color.parseColor("#FF6200EE"))
            heightTextLayout.suffixText="In"
        }

        //calculate button
        val calBtn = findViewById<Button>(R.id.buttonCalculate)

        calBtn.setOnClickListener {
            Toast.makeText(this, "Calculate Successfully!!!", Toast.LENGTH_SHORT).show()
        }
    }

}