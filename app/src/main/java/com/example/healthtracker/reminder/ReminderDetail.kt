package com.example.healthtracker.reminder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_reminder_detail.*

class ReminderDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtReminderTime.setIs24HourView(true)


        save_reminder.setOnClickListener {
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

        }
    }
}