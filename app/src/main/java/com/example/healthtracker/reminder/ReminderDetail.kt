package com.example.healthtracker.reminder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import com.example.healthtracker.dataclass.ReminderDetailDC
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reminder_detail.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class ReminderDetail : AppCompatActivity() {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtReminderTime.setIs24HourView(true)

        setUpFirebase()

        save_reminder.setOnClickListener {
            val reminderTitle = txtReminderTitle.text.toString()
            val reminderDesc = txtReminderDesc.text.toString()
            val switch = txtReminderSwitch.isChecked
            val hr = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                txtReminderTime.hour
            } else {
                -1
            }
            val min = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                txtReminderTime.minute
            } else {
                -1
            }
            val todayDateTime: Calendar = Calendar.getInstance()
            val todayDate = SimpleDateFormat("YYYY-MM-dd").format(todayDateTime.time)
            var text: String
            if(hr <= 9){
                text = "$todayDate" + " " + "0$hr:$min:00"
            }else if(min <= 9){
                text = "$todayDate" + " " + "$hr:0$min:00"
            }else if (hr <= 9 && min <= 9){
                text = "$todayDate" + " " + "0$hr:0$min:00"
            }else{
                text = "$todayDate" + " " + "$hr:$min:00"
            }
            val timeStamp: Timestamp = Timestamp.valueOf(text)

            if(hr == -1 || min == -1){
                Toast.makeText(this, "SDK Version Problem Occured", Toast.LENGTH_SHORT).show()
            }else if(reminderTitle.isEmpty() || reminderDesc.isEmpty()){
                Toast.makeText(this, "Please fill in the reminder details", Toast.LENGTH_SHORT).show()
            }else{
                val documentRef = firebase.collection("Reminder").document(userID)
                val reminderDetail = ReminderDetailDC(reminderTitle, reminderDesc, timeStamp, switch)
                documentRef.collection("Reminder Detail").add(reminderDetail).addOnSuccessListener {
                    Toast.makeText(this, "Reminder added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this," "+ it.message, Toast.LENGTH_SHORT).show()
                }
            }
            //testing123.text = txtReminderDate.dayOfMonth.toString() + "/" + txtReminderDate.month + "/" + txtReminderDate.year
            //testing123.text = txtReminderTime.hour.toString() + txtReminderTime.minute.toString()
        }

//        txtReminderDate.addTextChangedListener {object : TextWatcher{
//            override fun beforeTextChanged (s: CharSequence, start: Int, count: Int, after: Int){
//
//            }
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//            }
//            override fun afterTextChanged(s: Editable) {
//
//            }
//        }
//        }

        deleteReminder.setOnClickListener {
            Toast.makeText(this, "Reminder deleted successfully", Toast.LENGTH_SHORT).show()
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

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
    }
}