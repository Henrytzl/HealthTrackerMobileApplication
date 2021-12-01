package com.example.healthtracker.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.FAQ
import com.example.healthtracker.R
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reminder.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.util.*

class Reminder : AppCompatActivity(), RecycleViewReminderAdapter.OnItemClickListener, RecycleViewReminderAdapter.OnSwitchClickListener {

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String
    //Recycle View
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<RecycleViewReminder>
    private lateinit var adapter: RecycleViewReminderAdapter

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        //Tool Bar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // getInstance database
        setUpFirebase()
        createNotificationChannel()
        //Drawer
        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.mHome -> {
                    finish()
                }
                R.id.mProfile -> {

                }
                R.id.mFAQ -> {
                    startActivity(Intent(this, FAQ::class.java))
                    finish()
                }
                R.id.mFeedback -> {

                }
                R.id.mHelp -> {

                }
                R.id.mAboutUs -> {

                }
                R.id.mLogout -> {
                    authentication.signOut()
                    Toast.makeText(this,"Successfully Logout", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    finishAffinity()
                }
            }
            true
        }
        //Home Image Icon
        imageHome.setOnClickListener{
            finish()
        }
        //Floating Button
        addReminder.setOnClickListener{
            startActivity(Intent(this, ReminderDetail::class.java))
        }
    }

    override fun onItemClick(position: Int) {
        val clickedItem = list[position]
        val intent = Intent(this, ReminderDetail::class.java)
        intent.putExtra("reminderID", clickedItem.reminderID)
        startActivity(intent)
    }

    override fun onSwitchClickListener(position: Int) {
        val switchClickedItem = list[position]
        switchClickedItem.reminderActivate = !switchClickedItem.getReminderActivate()
        // Reminder Calendar
        calendar = Calendar.getInstance()

        val time = list[position].getReminderTime()
        val hr: String = "${time?.get(0)}" + "${time?.get(1)}"
        val min: String = "${time?.get(3)}" + "${time?.get(4)}"
        calendar[Calendar.HOUR_OF_DAY] = hr.toInt()
        calendar[Calendar.MINUTE] = min.toInt()
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        var text: String? = null
        if(switchClickedItem.reminderActivate){
            text = "Activated"
            setAlarm(list[position].getReminderTitle(), list[position].getReminderDesc(), list[position].getRequestCodeID(), list[position].getReminderID())
        }else{
            text = "Deactivated"
            cancelAlarm(list[position].getRequestCodeID())
        }

        firebase.collection("Reminder").document("$userID").collection("Reminder Detail").document("${switchClickedItem.getReminderID()}").update("reminderActivate", switchClickedItem.reminderActivate).addOnFailureListener {
            Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT)
        }
        adapter.notifyItemChanged(position)
        Toast.makeText(this,"${switchClickedItem.reminderTitle} Reminder" + " " + "$text", Toast.LENGTH_SHORT).show()
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

    override fun onStart() {
        super.onStart()

        // Get current User id and email
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
            navView.getHeaderView(0).dhEmail.text = authentication.currentUser!!.email
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            finishAffinity()
        }
        val nameDocRef = firebase.collection("User").document(userID)
        nameDocRef.get().addOnSuccessListener{ name ->
            if(name != null){
                navView.getHeaderView(0).dhName.text = name.getString("userName")
            }
        }
        val caloriesDocRef = firebase.collection("User").document(userID)
        caloriesDocRef.get().addOnSuccessListener { kcal ->
            if(kcal != null){
                navView.getHeaderView(0).dhKcal.text = kcal.get("calories").toString()
            }
        }
        //Recycle View
        recyclerView = recyclerViewReminder
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.setHasFixedSize(true)
        list = arrayListOf()

        adapter = RecycleViewReminderAdapter(list, this, this)

        recyclerView.adapter = adapter
        noDataTxt.visibility = View.GONE
        firebase.collection("Reminder/$userID/Reminder Detail")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FireStore Error", error.message.toString())
                }else {
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            list.add(dc.document.toObject(RecycleViewReminder::class.java))
                        }
                    }
                    if(list.isEmpty()){
                        noDataTxt.visibility = View.VISIBLE
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}