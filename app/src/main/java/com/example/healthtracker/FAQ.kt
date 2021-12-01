package com.example.healthtracker

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_f_a_q.*
import kotlinx.android.synthetic.main.nav_header.view.*

class FAQ : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_a_q)

        //Tool Bar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpFirebase()
        //Drawer
        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.mHome -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    finishAffinity()
                }
                R.id.mProfile -> {
                    startActivity(Intent(this, AuthorisedUser::class.java))
                    finish()
                }
                R.id.mHelp -> {
                    startActivity(Intent(this, ChatBot::class.java))
                    finish()
                }
                R.id.mAboutUs -> {
                    startActivity(Intent(this, AboutUs::class.java))
                    finish()
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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            finishAffinity()
        }


        faq1Btn.setOnClickListener {
            if(faq1Answer.visibility == View.GONE){
                faq1Answer.visibility = View.VISIBLE
                faq1Btn.setImageResource(R.drawable.ic_up)
            }else{
                faq1Answer.visibility = View.GONE
                faq1Btn.setImageResource(R.drawable.ic_down)
            }
        }

        faq2Btn.setOnClickListener {
            if(faq2Answer.visibility == View.GONE){
                faq2Answer.visibility = View.VISIBLE
                faq2Btn.setImageResource(R.drawable.ic_up)
            }else{
                faq2Answer.visibility = View.GONE
                faq2Btn.setImageResource(R.drawable.ic_down)
            }
        }

        faq3Btn.setOnClickListener {
            if(faq3Answer.visibility == View.GONE){
                faq3Answer.visibility = View.VISIBLE
                faq3Btn.setImageResource(R.drawable.ic_up)
            }else{
                faq3Answer.visibility = View.GONE
                faq3Btn.setImageResource(R.drawable.ic_down)
            }
        }

        faq4Btn.setOnClickListener {
            if(faq4Answer.visibility == View.GONE){
                faq4Answer.visibility = View.VISIBLE
                faq4Btn.setImageResource(R.drawable.ic_up)
            }else{
                faq4Answer.visibility = View.GONE
                faq4Btn.setImageResource(R.drawable.ic_down)
            }
        }

        faq5Btn.setOnClickListener {
            if(faq5Answer.visibility == View.GONE){
                faq5Answer.visibility = View.VISIBLE
                faq5Btn.setImageResource(R.drawable.ic_up)
            }else{
                faq5Answer.visibility = View.GONE
                faq5Btn.setImageResource(R.drawable.ic_down)
            }
        }
    }
    override fun onStart() {
        super.onStart()

        // Get current User id and email
        if (authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
            navView.getHeaderView(0).dhEmail.text = authentication.currentUser!!.email
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            finishAffinity()
        }
        val nameDocRef = firebase.collection("User").document(userID)
        nameDocRef.get().addOnSuccessListener { name ->
            if (name != null) {
                navView.getHeaderView(0).dhName.text = name.getString("userName")
            }
        }
        val caloriesDocRef = firebase.collection("User").document(userID)
        caloriesDocRef.get().addOnSuccessListener { kcal ->
            if (kcal != null) {
                navView.getHeaderView(0).dhKcal.text = kcal.get("calories").toString()
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