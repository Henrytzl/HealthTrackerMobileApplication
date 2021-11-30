package com.example.healthtracker.reminder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String

    override fun onReceive(context: Context?, intent: Intent?) {

        val i = Intent(context, MainActivity::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //set up database
        setUpFirebase()
        //get information from the pending intent of the setAlarm
        val getTitle = intent.getStringExtra("reminderTitle").toString()
        val getDesc = intent.getStringExtra("reminderDesc").toString()
        val getRequestCodeID = intent.getIntExtra("requestCodeID", 0)
        val getReminderID = intent.getStringExtra("reminderID").toString()
        //get today
        val calendar: Calendar = Calendar.getInstance()
        val day: Int = calendar.get(Calendar.DAY_OF_WEEK)
        var today: String? = null
        if (day == 2) {
            today = "Monday"
        } else if (day == 3) {
            today = "Tuesday"
        } else if (day == 4) {
            today = "Wednesday"
        } else if (day == 5) {
            today = "Thursday"
        } else if (day == 6) {
            today = "Friday"
        } else if (day == 7) {
            today = "Saturday"
        } else if (day == 1) {
            today = "Sunday"
        }

        val pendingIntent = PendingIntent.getActivity(context, getRequestCodeID, i, 0)

        val builder = NotificationCompat.Builder(context!!, "notification")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getTitle)
            .setContentText(getDesc)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)

        firebase.collection("Reminder").document(userID).collection("Reminder Detail").document(getReminderID).get().addOnSuccessListener { result ->
            if(result.exists()) {
                val dayListFromDb: ArrayList<String> = result.get("selectedDay") as ArrayList<String>
                for (i in dayListFromDb.indices) {
                    if (today.equals(dayListFromDb[i], ignoreCase = true)) {
                        val notificationManager = NotificationManagerCompat.from(context)
                        notificationManager.notify(getRequestCodeID, builder.build())
                    }
                }
                Log.d("Got it", "Got the value of reminder ID $getTitle")
            }else{
                Log.d("Empty ReminderID", "Got null value of reminder ID $getTitle")
            }
        }
    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}