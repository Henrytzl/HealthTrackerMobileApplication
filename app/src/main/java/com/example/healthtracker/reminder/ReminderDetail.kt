package com.example.healthtracker.reminder

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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

class ReminderDetail : AppCompatActivity() {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String
    var reminderID: String = ""
    private lateinit var selectedDayList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtReminderTime.setIs24HourView(true)

        setUpFirebase()

        selectedDayList = ArrayList()
        selectDayBtn.setOnClickListener {
            val daySelectorViewBuilder = AlertDialog.Builder(this).setTitle("Choose Repeat Day").setIcon(R.drawable.ic_day)
            daySelectorViewBuilder.setMultiChoiceItems(R.array.Day, null) { dialog, which, isChecked ->

                val items: Array<String> = resources.getStringArray(R.array.Day)

                if (isChecked) {
                    (selectedDayList as ArrayList<String>).add(items[which])
                } else if (selectedDayList.contains(items[which])) {
                    (selectedDayList as ArrayList<String>).remove(items[which])
                }
            }
            daySelectorViewBuilder.setCancelable(false).setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialog, which ->
                dialog.dismiss()
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }).setPositiveButton("Done", DialogInterface.OnClickListener{ dialog, which ->
                var text : String = ""
                for(item in selectedDayList){
                    text = text + "\n" + item
                }
                Toast.makeText(this, "Done Selection: $text", Toast.LENGTH_SHORT).show()
            })
            //show dialog
            val displayDialog = daySelectorViewBuilder.create()
            displayDialog.show()
        }

        val intentFound = intent
        reminderID = intent.getStringExtra("reminderID").toString()

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
                            Toast.makeText(this, "SDK Version Problem Occurred", Toast.LENGTH_SHORT)
                                .show()
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
                        firebase.collection("Reminder").document(userID)
                            .collection("Reminder Detail").document(reminderID).delete().addOnSuccessListener {
                                dialog.dismiss()
                                finish()
                                Toast.makeText(this, "Reminder deleted successfully", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(this," " + it.message, Toast.LENGTH_LONG).show()
                            }
                    })
                //show dialog
                val displayDialog = deleteViewBuilder.create()
                displayDialog.show()
            }
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

            if(hr == -1 || min == -1){
                Toast.makeText(this, "SDK Version Problem Occurred", Toast.LENGTH_SHORT).show()
            }else if(reminderTitle.isEmpty() || reminderDesc.isEmpty()){
                Toast.makeText(this, "Please fill in the reminder details", Toast.LENGTH_SHORT).show()
            }else{
                val documentRef = firebase.collection("Reminder").document(userID)
                if(reminderID == "null"){           // Create Reminder
                    val getDocID = documentRef.collection("Reminder Detail").document()
                    val reminderDetail = ReminderDetailDC(getDocID.id, reminderTitle, reminderDesc, time, switch)
                    val detailDocumentRef1 = documentRef.collection("Reminder Detail")
                    detailDocumentRef1.document("${getDocID.id}").set(reminderDetail).addOnSuccessListener {
                        Toast.makeText(this, "Reminder added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this," "+ it.message, Toast.LENGTH_SHORT).show()
                    }
                }else{                              // Update Reminder
                    val update = documentRef.collection("Reminder Detail").document("$reminderID")
                    val reminderDetail = ReminderDetailDC(reminderID, reminderTitle, reminderDesc, time, switch)
                    update.set(reminderDetail).addOnSuccessListener {
                        Toast.makeText(this, "Reminder updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this," "+ it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}