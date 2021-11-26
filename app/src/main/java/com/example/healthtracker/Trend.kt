package com.example.healthtracker

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.datamodel.Results
import com.example.healthtracker.login.LoginActivity
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.trend.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class Trend : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var authentication: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String
    lateinit var lineList: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData
    private lateinit var selectedDateFrom: String
    private lateinit var selectedDateTo: String
    private var newResultList = ArrayList<Results>()
    private var selectedResultList = ArrayList<Results>()

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

        //retrieve data
        retrieveData()

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
                    startActivity(Intent(this, ChatBot::class.java))
                    finish()
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
//        setLineChartData()
        weightLineChart()
        weightTrend.setOnClickListener {
            weightTrend.isSelected = true
//            setLineChartData()
            weightLineChart()
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
                calendar()
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

        lineDataSet = LineDataSet(lineList, "Weight")
        lineData = LineData(lineDataSet)
        weightLineChart.data = lineData
        lineDataSet.color = (Color.parseColor("#0B89FE"))
        lineDataSet.valueTextColor = Color.parseColor("#0B89FE")
        lineDataSet.valueTextSize = 13f
        lineDataSet.circleRadius = 0f
        lineDataSet.fillAlpha = 30
        lineDataSet.setDrawFilled(true)
    }

    private fun calendar() {
        showDataRangePicker()
    }

    private fun showDataRangePicker() {
        val dateRangePicker = MaterialDatePicker
            .Builder.dateRangePicker()
            .setTitleText("Select Date")
            .build()

        dateRangePicker.show(
            supportFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { dateSelected ->
            val startDate = dateSelected.first
            val endDate = dateSelected.second

            if (startDate != null && endDate != null) {
                selectedDateFrom = convertLongToTime(startDate)
                selectedDateTo = convertLongToTime(endDate)

                Toast.makeText(
                    this,
                    "Date Range:  $selectedDateFrom - $selectedDateTo",
                    Toast.LENGTH_SHORT
                ).show()

                showTrend()
            }
        }

    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun retrieveData() {
        val db = FirebaseFirestore.getInstance()
        val resultList = ArrayList<Results>()
        val trendADocRef = db.collection("Results").document(userID)
        trendADocRef.collection("Result Details")
            .orderBy("date")
            .get()
            .addOnSuccessListener() {

                if (it.isEmpty) {
                    Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (doc in it) {
                    val results = doc.toObject(Results::class.java)
                    resultList.add(results)
                }

                newResultList = ArrayList<Results>()
                val simpleDateFormat =
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    )   // to format date to compare date ONLY

                var larger: Results?                    // condition: same date but latest (bcz of the time)
                for (x in resultList.indices) {
                    larger = resultList[x]
                    for (y in resultList.indices) {
                        if (simpleDateFormat.format(larger!!.date!!) == simpleDateFormat.format(
                                resultList[y].date!!
                            )
                        ) {
                            if (larger.date!!.time < resultList[y].date!!.time) {
                                larger = resultList[y]    // get the latest within the same date
                            }
                        }
                    }

                    if (newResultList.isNotEmpty()) {
                        if (newResultList[newResultList.size - 1] != larger) {
                            newResultList.add(larger!!)         // if the same date and latest time already added in then will not come in
                        }
                    } else {
                        newResultList.add(larger!!)
                    }
                }
            }
    }

    private fun showTrend() {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        if (selectedResultList.isEmpty()) {
            for (i in newResultList.indices) {
                if (simpleDateFormat.format(newResultList[i].date!!) >= selectedDateFrom && simpleDateFormat.format(
                        newResultList[i].date!!
                    ) <= selectedDateTo
                ) {
                    selectedResultList.add(newResultList[i])
                }
            }
        }

        Toast.makeText(
            this,
            "${selectedResultList[0].date}, ${selectedResultList[1].date}, ${selectedResultList[2].date}, ${selectedResultList[3].date}, ${selectedResultList[4].date}, ${selectedResultList[5].date}, ${selectedResultList[6].date}, ${selectedResultList[7].date} ",
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun weightLineChart () {
        //line chart
        lineList = ArrayList()
        for (i in selectedResultList.indices) {
            lineList.add(Entry(selectedResultList[i].date!!.time.toFloat(), selectedResultList[i].weight!!.toFloat()))

        }


        lineDataSet = LineDataSet(lineList, "Weight")
        lineData = LineData(lineDataSet)
        weightLineChart.data = lineData
        lineDataSet.color = (Color.parseColor("#0B89FE"))
        lineDataSet.valueTextColor = Color.parseColor("#0B89FE")
        lineDataSet.valueTextSize = 13f
        lineDataSet.circleRadius = 0f
        lineDataSet.fillAlpha = 30
        lineDataSet.setDrawFilled(true)
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