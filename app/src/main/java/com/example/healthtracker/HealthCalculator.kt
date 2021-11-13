package com.example.healthtracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.health_calculator_form.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlin.math.pow
import kotlin.math.roundToInt

class HealthCalculator : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var authentication: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String
    private var age: Int = 0
    private var inputAge: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_calculator_form)

        //Tool Bar
        setSupportActionBar(toolbarHealth)
        supportActionBar?.title = ""
        toggle =
            ActionBarDrawerToggle(this, drawerLayoutHealth, R.string.nav_open, R.string.nav_close)
        drawerLayoutHealth.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //getInstance database
        setUpFirebase()

        //Drawer
        navViewHealth.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.mHome -> {
                    finish()
                }
                R.id.mProfile -> {
                    startActivity(Intent(this, AuthorisedUser::class.java))
                    finish()
                }
                R.id.mFAQ -> {

                }
                R.id.mFeedback -> {

                }
                R.id.mHelp -> {

                }
                R.id.mAboutUs -> {

                }
                R.id.mLogout -> {
                    authentication.signOut()
                    Toast.makeText(this, "Successfully Logout", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    finishAffinity()
                }
            }
            true
        }
        //Home Image Icon
        imageHome.setOnClickListener {
            finish()
        }

        //gender dropdown list
        val gender = resources.getStringArray(R.array.Gender)
        val adapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, gender)

        autoCompleteTextViewGender.setAdapter(adapter)

        //activity level dropdown list
        val activityLvl = resources.getStringArray(R.array.Activity_Lvl)
        val adapter2 = ArrayAdapter(applicationContext, R.layout.dropdown_item, activityLvl)

        autoCompleteTextViewActivityLvl.setAdapter(adapter2)

        //age stuff

        //add button
        buttonAdd.setOnClickListener {
            if (textInputEditTextAge.text.toString().isNotEmpty()) {
                inputAge = textInputEditTextAge.text.toString().trim().toInt()
            }
            if (inputAge != null) {
                age = inputAge as Int
            } else {
                age = 18
            }
            age++
            if (age > 100) {
                Toast.makeText(this, "Age range from 1 to 100.", Toast.LENGTH_SHORT).show()
            } else {
                textInputEditTextAge.setText("$age")
                textInputEditTextAge.setTextColor(Color.parseColor("#FF6200EE"))
                textInputEditTextAge.setSelection(textInputEditTextAge.length())
            }
        }

        //minus button
        buttonMinus.setOnClickListener {
            if (textInputEditTextAge.text.toString().isNotEmpty()) {
                inputAge = textInputEditTextAge.text.toString().trim().toInt()
            }
            if (inputAge != null) {
                age = inputAge as Int
            } else {
                age = 18
            }
            age--
            if (age <= 0) {
                Toast.makeText(this, "Age range from 1 to 100.", Toast.LENGTH_SHORT).show()
            } else {
                textInputEditTextAge.setText("$age")
                textInputEditTextAge.setTextColor(Color.parseColor("#FF6200EE"))
                textInputEditTextAge.setSelection(textInputEditTextAge.length())
            }
        }

        //weight stuff
        buttonKg.setOnClickListener {
            buttonKg.isSelected = true
            buttonLb.isChecked = false
            buttonLb.setTextColor(Color.BLACK)
            buttonKg.setBackgroundResource(R.drawable.custom_button_kg_checked)
            buttonLb.setBackgroundResource(R.drawable.custom_button_lb_unchecked)
            buttonKg.setTextColor(Color.parseColor("#FF6200EE"))
            textInputLayoutWeight.suffixText = "Kg"
        }

        buttonLb.setOnClickListener {
            buttonLb.isSelected = true
            buttonKg.isChecked = false
            buttonKg.setTextColor(Color.BLACK)
            buttonLb.setBackgroundResource(R.drawable.custom_button_lb_checked)
            buttonKg.setBackgroundResource(R.drawable.custom_button_kg_unchecked)
            buttonLb.setTextColor(Color.parseColor("#FF6200EE"))
            textInputLayoutWeight.suffixText = "Lb"
        }

        //height stuff
        buttonCm.setOnClickListener {
            buttonCm.isSelected = true
            buttonIn.isChecked = false
            buttonIn.setTextColor(Color.BLACK)
            buttonCm.setBackgroundResource(R.drawable.custom_button_cm_checked)
            buttonIn.setBackgroundResource(R.drawable.custom_button_in_unchecked)
            buttonCm.setTextColor(Color.parseColor("#FF6200EE"))
            textInputLayoutHeight.suffixText = "Cm"
        }

        buttonIn.setOnClickListener {
            buttonIn.isSelected = true
            buttonCm.isChecked = false
            buttonCm.setTextColor(Color.BLACK)
            buttonIn.setBackgroundResource(R.drawable.custom_button_in_checked)
            buttonCm.setBackgroundResource(R.drawable.custom_button_cm_unchecked)
            buttonIn.setTextColor(Color.parseColor("#FF6200EE"))
            textInputLayoutHeight.suffixText = "In"
        }

        //calculate button
        buttonCalculate.setOnClickListener {
            val intent = Intent(this, Result::class.java)

            //BMI
            if (autoCompleteTextViewGender.text.toString()
                    .isNotEmpty() && textInputEditTextAge.text.toString()
                    .isNotEmpty() && textInputEditTextWeight.text.toString()
                    .isNotEmpty() && textInputEditTextHeight.text.toString()
                    .isNotEmpty() && autoCompleteTextViewActivityLvl.text.toString().isNotEmpty()
            ) {

                if (textInputEditTextAge.text.toString().toInt() !in 1..100) {
                    Toast.makeText(this, "Age range from 1 to 100.", Toast.LENGTH_SHORT).show()
                } else {

                    //BMI variables
                    var wValue = 0.0
                    var hValue = 0.0
                    var wBValue = 0.0
                    var hBValue = 0.0
                    var bmiValue = 0.0

                    //BFP variables
                    val male = 1
                    val female = 0
                    var genderV = 2
                    var ageV = 0
                    var bfpValue = 0.0

                    //Daily Calories variables
                    var hDcValue = 0.0
                    var hDcBValue = 0.0
                    var dcValue = 0.0
                    val a1 = 1.2
                    val a2 = 1.4
                    val a3 = 1.6
                    val a4 = 1.75
                    val a5 = 2
                    val a6 = 2.3

                    if (textInputEditTextAge.text.toString().isNotEmpty()) {
                        inputAge = textInputEditTextAge.text.toString().trim().toInt()
                    }
                    ageV = inputAge!!

                    if (buttonKg.isChecked) {
                        wValue = textInputEditTextWeight.text.toString().toDouble()
                    }
                    if (buttonLb.isChecked) {
                        wBValue = (textInputEditTextWeight.text.toString().toDouble() * 0.453592)
                        wValue = String.format("%.2f", wBValue).toDouble()
                    }

                    if (buttonCm.isChecked) {
                        hValue = (textInputEditTextHeight.text.toString().toDouble() / 100)
                        hDcValue = textInputEditTextHeight.text.toString().toDouble()
                    }
                    if (buttonIn.isChecked) {
                        hBValue =
                            ((textInputEditTextHeight.text.toString().toDouble() * 2.54) / 100)
                        hDcBValue = (textInputEditTextHeight.text.toString().toDouble() * 2.54)
                        hValue = String.format("%.2f", hBValue).toDouble()
                        hDcValue = String.format("%.2f", hDcBValue).toDouble()
                    }

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

                    //BMI & BFP
                    if (wValue > 0.0 && hValue > 0.0 && ageV <= 18) {
                        bmiValue = wValue / hValue.pow(2)
                        bmiValue.toString().format("%.1f").toDouble()
                        val bmiValueRound = String.format("%.1f", bmiValue).toDouble()
                        bfpValue = (1.51 * bmiValueRound) - (0.70 * ageV) - (3.6 * genderV) + 1.4

                        intent.putExtra("Weight", wValue)
                        intent.putExtra("BMI", bmiValue)
                        intent.putExtra("BFP", bfpValue.roundToInt())
                        intent.putExtra("DC", dcValue.roundToInt())
                        startActivity(intent)
                    } else if (wValue > 0.0 && hValue > 0.0 && ageV >= 19) {
                        bmiValue = wValue / hValue.pow(2)
                        bmiValue.toString().format("%.1f").toDouble()
                        val bmiValueRound = String.format("%.1f", bmiValue).toDouble()
                        bfpValue = (1.39 * bmiValueRound) + (0.16 * ageV) - (10.34 * genderV) - 9

                        intent.putExtra("Weight", wValue)
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
                }
            } else Toast.makeText(this, "Please enter all the fields.", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onStart() {
        super.onStart()

        // Get current User id and email
        if (authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
            navViewHealth.getHeaderView(0).dhEmail.text = authentication.currentUser!!.email
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            finishAffinity()
        }
        val nameDocRef = db.collection("User").document(userID)
        nameDocRef.get().addOnSuccessListener { name ->
            if (name != null) {
                navViewHealth.getHeaderView(0).dhName.text = name.getString("userName")
            }
        }
        val caloriesDocRef = db.collection("User").document(userID)
        caloriesDocRef.get().addOnSuccessListener { kcal ->
            if (kcal != null) {
                navViewHealth.getHeaderView(0).dhKcal.text = kcal.get("calories").toString()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpFirebase() {
        authentication = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        if (authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}