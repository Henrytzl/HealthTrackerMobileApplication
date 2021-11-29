package com.example.healthtracker.reminder

import android.app.*
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import com.example.healthtracker.dataclass.ReminderDetailDC
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reminder_detail.*
import java.util.*
import kotlin.collections.ArrayList

class ReminderDetail : AppCompatActivity() {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String
    var reminderID: String = ""
    private lateinit var dayList: List<String>
    private val dayArray = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    private var selectedDayListBoolean = booleanArrayOf(false, false, false, false, false, false, false, false)
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtReminderTime.setIs24HourView(true)

        setUpFirebase()
        createNotificationChannel()

        reminderID = intent.getStringExtra("reminderID").toString()
        dayList = Arrays.asList(*dayArray)
        if(reminderID == "null"){   // Add reminder
            deleteReminder.visibility = View.GONE
        }else{                      // Modify reminder
            deleteReminder.visibility = View.VISIBLE
            firebase.collection("Reminder").document(userID)
                .collection("Reminder Detail").document(reminderID).get().addOnSuccessListener { document ->
                    if(document != null) {
                        txtReminderSwitch.isChecked = document.getBoolean("reminderActivate")!!
                        txtReminderTitle.setText(document.getString("reminderTitle"))
                        txtReminderDesc.setText(document.getString("reminderDesc"))
                        val time = document.getString("reminderTime")
                        val hr: String = "${time?.get(0)}" + "${time?.get(1)}"
                        val min: String = "${time?.get(3)}" + "${time?.get(4)}"
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            txtReminderTime.hour = hr.toInt()
                            txtReminderTime.minute = min.toInt()
                        } else {
                            Toast.makeText(this, "SDK Version Problem Occurred", Toast.LENGTH_SHORT).show()
                        }
                        //set day list
                        val dayListFromDb: ArrayList<String> = document.get("selectedDay") as ArrayList<String>

                        for(x in dayList.indices){
                            for(y in dayListFromDb.indices){
                                if(dayList[x] == dayListFromDb[y]){
                                    selectedDayListBoolean[x] = true
                                }
                            }
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT)
                }

            deleteReminder.setOnClickListener {
                val deleteViewBuilder = AlertDialog.Builder(this).setTitle("Delete Reminder")
                    .setIcon(R.drawable.ic_delete2).setMessage("Are you sure to delete this reminder?")
                    .setCancelable(false).setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                        })
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                        firebase.collection("Reminder").document(userID).collection("Reminder Detail").document(reminderID).get().addOnSuccessListener { result ->
                            val cancelRequestCodeID = result.get("requestCodeID").toString().toInt()
                            val switchFromDB: Boolean = result.get("reminderActivate").toString().toBoolean()
                            firebase.collection("Reminder").document(userID).collection("Reminder Detail").document(reminderID).delete().addOnSuccessListener {
                                if(switchFromDB) {
                                    cancelAlarm(cancelRequestCodeID)
                                }
                                dialog.dismiss()
                                finish()
                                Toast.makeText(this, "Reminder deleted successfully", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(this, " " + it.message, Toast.LENGTH_LONG).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, " " + it.message, Toast.LENGTH_LONG).show()
                        }
                    })
                //show dialog
                val displayDialog = deleteViewBuilder.create()
                displayDialog.show()
            }
        }

        //Day selector onClickListener
        selectDayBtn.setOnClickListener {
            val daySelectorViewBuilder = AlertDialog.Builder(this).setTitle("Choose Repeat Day").setIcon(
                R.drawable.ic_day
            )

            daySelectorViewBuilder.setMultiChoiceItems(dayArray, selectedDayListBoolean) { dialog, which, isChecked ->
                selectedDayListBoolean[which] = isChecked
            }
            daySelectorViewBuilder.setCancelable(false).setPositiveButton("Done", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(this, "Done Selection", Toast.LENGTH_SHORT).show()
            })
            //show dialog
            val displayDialog = daySelectorViewBuilder.create()
            displayDialog.show()
        }

        //Clicking tick button
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

            var time: String
            if (hr <= 9 && min <= 9){
                time = "0$hr:0$min"
            }else if(hr <= 9){
                time = "0$hr:$min"
            }else if(min <= 9){
                time = "$hr:0$min"
            }else {
                time = "$hr:$min"
            }

            // Reminder Calendar
            calendar = Calendar.getInstance()
            val requestCodeID = System.currentTimeMillis().toInt()

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                calendar[Calendar.HOUR_OF_DAY] = txtReminderTime.hour
                calendar[Calendar.MINUTE] = txtReminderTime.minute
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0
//                var intDay = 0
//                for(i in selectedDayListBoolean.indices){
//                    intDay++
//                    if(selectedDayListBoolean[i]){
//                        when(intDay){
//                            1 -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
//                            2 -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
//                            3 -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
//                            4 -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
//                            5 -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
//                            6 -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
//                            7 -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
//                        }
//                    }
//                }
            }

            if(hr == -1 || min == -1){
                Toast.makeText(this, "SDK Version Problem Occurred", Toast.LENGTH_SHORT).show()
            }else if(reminderTitle.isEmpty() || reminderDesc.isEmpty()){
                Toast.makeText(this, "Please fill in the reminder details", Toast.LENGTH_SHORT).show()
            }else{
                if(checkSelectedDayList(selectedDayListBoolean)){
                    val documentRef = firebase.collection("Reminder").document(userID)
                    val insertDayList = convertToInsertDayList(selectedDayListBoolean, dayList)
                    if(reminderID == "null"){           // Create Reminder
                        val getDocID = documentRef.collection("Reminder Detail").document()
                        val reminderDetail = ReminderDetailDC(getDocID.id, reminderTitle, reminderDesc, time, switch, insertDayList, requestCodeID)
                        val detailDocumentRef1 = documentRef.collection("Reminder Detail")
                        detailDocumentRef1.document("${getDocID.id}").set(reminderDetail).addOnSuccessListener {
                            detailDocumentRef1.document("${getDocID.id}").get().addOnSuccessListener { result ->
                                if(switch) {
                                    setAlarm(result.get("reminderTitle").toString(), result.get("reminderDesc").toString(), result.get("requestCodeID").toString().toInt(), getDocID.id)
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT).show()
                            }

                            Toast.makeText(this, "Reminder added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }else{                              // Update Reminder
                        val update = documentRef.collection("Reminder Detail").document("$reminderID")
                        update.get().addOnSuccessListener { result ->
                            if(result.getBoolean("reminderActivate").toString().toBoolean()){
                                cancelAlarm(result.get("requestCodeID").toString().toInt())
                            }
                            val reminderDetail = ReminderDetailDC(reminderID, reminderTitle, reminderDesc, time, switch, insertDayList, result.get("requestCodeID").toString().toInt())
                            update.set(reminderDetail).addOnSuccessListener {
                                if(switch) {
                                    setAlarm(reminderTitle, reminderDesc, result.get("requestCodeID").toString().toInt(), result.get("reminderID").toString())
                                }
                                Toast.makeText(this, "Reminder updated successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Please select the reminder day", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cancelAlarm(requestCodeID: Int) {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, requestCodeID, intent, 0)

        alarmManager.cancel(pendingIntent)
    }

    private fun setAlarm(reminderTitle: String, reminderDesc: String, requestCodeID: Int, reminderID: String) {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("reminderTitle", reminderTitle)
        intent.putExtra("reminderDesc", reminderDesc)
        intent.putExtra("requestCodeID", requestCodeID)
        intent.putExtra("reminderID", reminderID)
        pendingIntent = PendingIntent.getBroadcast(this, requestCodeID, intent, 0)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

        // 24 * 7 * 60 * 60 * 1000  //for every week interval calculation
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name: CharSequence = "HealthTrackerReminderChannel"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("notification", name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkSelectedDayList(selectedDayListBoolean: BooleanArray):Boolean{
        for(i in selectedDayListBoolean.indices){
            val selected = selectedDayListBoolean[i]
            if(selected){
                return true
            }
        }
        return false
    }
    private fun convertToInsertDayList(selectedDayListBoolean: BooleanArray, dayList: List<String>): ArrayList<String>{
        var insertDayList: ArrayList<String> = ArrayList()
        for(i in selectedDayListBoolean.indices){
            val selected = selectedDayListBoolean[i]
            if(selected){
                insertDayList.add(dayList[i])
            }
        }
        return insertDayList
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
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}