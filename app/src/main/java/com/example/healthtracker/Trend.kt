package com.example.healthtracker

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.login.LoginActivity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.trend.*
import kotlinx.android.synthetic.main.trend.imageHome
import java.util.*
import kotlin.collections.ArrayList


class Trend : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var authentication: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String
    lateinit var lineList: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData

    var day = 0
    var month = 0
    var year = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trend)

        //Tool Bar
        setSupportActionBar(toolbarTrend)
        supportActionBar?.title = ""
        toggle =
            ActionBarDrawerToggle(this, drawerLayoutTrend, R.string.nav_open, R.string.nav_close)
        drawerLayoutTrend.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //getInstance database
        setUpFirebase()

        //Drawer
        navViewTrend.setNavigationItemSelectedListener {
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

        //trend option
        weightTrend.paintFlags = weightTrend.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        setLineChartData()
        weightTrend.setOnClickListener {
            weightTrend.isSelected = true
            setLineChartData()
            weightTrend.paintFlags = weightTrend.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            weightTrend.setTextColor(Color.parseColor("#0B89FE"))
            bmiTrend.paintFlags = bmiTrend.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            bmiTrend.setTextColor(Color.parseColor("#5A5A5A"))
            bfpTrend.paintFlags = bfpTrend.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            bfpTrend.setTextColor(Color.parseColor("#5A5A5A"))
        }

        bmiTrend.setOnClickListener {
            bmiTrend.isSelected = true
            bmiTrend.paintFlags = bmiTrend.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            bmiTrend.setTextColor(Color.parseColor("#0B89FE"))
            weightTrend.paintFlags = weightTrend.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            weightTrend.setTextColor(Color.parseColor("#5A5A5A"))
            bfpTrend.paintFlags = bfpTrend.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            bfpTrend.setTextColor(Color.parseColor("#5A5A5A"))
        }

        bfpTrend.setOnClickListener {
            bfpTrend.isSelected = true
            bfpTrend.paintFlags = bfpTrend.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            bfpTrend.setTextColor(Color.parseColor("#0B89FE"))
            weightTrend.paintFlags = weightTrend.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            weightTrend.setTextColor(Color.parseColor("#5A5A5A"))
            bmiTrend.paintFlags = bmiTrend.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            bmiTrend.setTextColor(Color.parseColor("#5A5A5A"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.calendar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.calendar -> {
                pickFromDate()
                pickToDate()
            }
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLineChartData() {
        lineList = ArrayList()
        lineList.add(Entry(20f, 55f))
        lineList.add(Entry(40f, 58f))
        lineList.add(Entry(60f, 63f))
        lineList.add(Entry(80f, 60f))
        lineList.add(Entry(100f, 57f))

        lineDataSet = LineDataSet(lineList, "Entries")
        lineData = LineData(lineDataSet)
        weightLineChart.data = lineData
        lineDataSet.color = (Color.parseColor("#0B89FE"))
        lineDataSet.valueTextColor = Color.parseColor("#0B89FE")
        lineDataSet.valueTextSize = 13f
        lineDataSet.circleRadius = 0f
        lineDataSet.fillAlpha = 30
        lineDataSet.setDrawFilled(true)
    }

    private fun getDateCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    private fun pickFromDate() {
        getDateCalendar()

//        DatePickerDialog.BUTTON_POSITIVE
        DatePickerDialog(this, this, year, month, day).show()

        Toast.makeText(this, "Select date from", Toast.LENGTH_SHORT).show()
    }

    private fun pickToDate() {
        getDateCalendar()

        DatePickerDialog(this, this, year, month, day).show()
        Toast.makeText(this, "Select date from", Toast.LENGTH_SHORT).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateCalendar()
    }

    override fun onStart() {
        super.onStart()

        // Get current User id and email
        if (authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
            navViewTrend.getHeaderView(0).dhEmail.text = authentication.currentUser!!.email
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            finishAffinity()
        }
        val nameDocRef = db.collection("User").document(userID)
        nameDocRef.get().addOnSuccessListener { name ->
            if (name != null) {
                navViewTrend.getHeaderView(0).dhName.text = name.getString("userName")
            }
        }
        val caloriesDocRef = db.collection("User").document(userID)
        caloriesDocRef.get().addOnSuccessListener { kcal ->
            if (kcal != null) {
                navViewTrend.getHeaderView(0).dhKcal.text = kcal.get("calories").toString()
            }
        }
    }

    private fun setUpFirebase() {
        authentication = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        if (authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }

}