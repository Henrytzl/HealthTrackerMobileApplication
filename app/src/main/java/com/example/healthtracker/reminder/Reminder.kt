package com.example.healthtracker.reminder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reminder.*
import kotlinx.android.synthetic.main.nav_header.view.*

class Reminder : AppCompatActivity(), RecycleViewReminderAdapter.OnItemClickListener, RecycleViewReminderAdapter.OnSwitchClickListener {

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String
    //Recycle View
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<RecycleViewReminder>
    private lateinit var adapter: RecycleViewReminderAdapter

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

        //Drawer
        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.mHome -> {
                    finish()
                }
                R.id.mProfile -> {

                }
                R.id.mFAQ -> {

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
        val text: String =
            if(switchClickedItem.reminderActivate){
                "Activated"
            }else{
                "Deactivated"
            }
        Toast.makeText(this,"${switchClickedItem.reminderTitle} Reminder" + " " + "${text}", Toast.LENGTH_SHORT).show()
        firebase.collection("Reminder").document("$userID")
            .collection("Reminder Detail").document("${switchClickedItem.getReminderID()}").update("reminderActivate", switchClickedItem.reminderActivate)
        adapter.notifyItemChanged(position)
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