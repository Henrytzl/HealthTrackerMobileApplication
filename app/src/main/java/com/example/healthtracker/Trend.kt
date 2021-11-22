package com.example.healthtracker

import android.app.DatePickerDialog
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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.trend.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Trend : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var authentication: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String
    lateinit var lineList: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData
    private lateinit var dateTo: Date
    private lateinit var dateFrom: Date
    private lateinit var dateFromFinal: String
    private lateinit var dateToFinal: String


    //var formatDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)

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

    private fun calendar() {
        dateTo()
        dateFrom()
    }

    private fun dateFrom() {
        val getDate = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->

                val selectDate = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, i)
                selectDate.set(Calendar.MONTH, i2)
                selectDate.set(Calendar.DAY_OF_MONTH, i3)
//                val date = formatDate.format(selectDate.time)
                val startDate = timeStampToString(System.currentTimeMillis())
//                val endDate = timeStampToString(System.currentTimeMillis())
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val dateFrom = simpleDateFormat.parse(startDate)
//                val dateTo = simpleDateFormat.parse(endDate)
                dateFromFinal = dateFrom.toString()


                Toast.makeText(this, "Date From: " + dateFromFinal, Toast.LENGTH_SHORT).show()

            },
            getDate.get(Calendar.YEAR),
            getDate.get(Calendar.MONTH),
            getDate.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
        retrieveData()
    }

    private fun timeStampToString(timeStamp: Long): String {
        val stamp = Timestamp(timeStamp)
        val simpleDataFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val date = simpleDataFormat.format((Date(stamp.time)))

        return date.toString()
    }

    private fun dateTo() {
        val getDate = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->

                val selectDate = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, i)
                selectDate.set(Calendar.MONTH, i2)
                selectDate.set(Calendar.DAY_OF_MONTH, i3)
                //val date = formatDate.format(selectDate.time)
//                val startDate = timeStampToString(System.currentTimeMillis())
                val endDate = timeStampToString(System.currentTimeMillis())
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
//                val dateFrom = simpleDateFormat.parse(startDate)
                dateTo = simpleDateFormat.parse(endDate)
                dateToFinal = dateTo.toString()
//                val date = formatDate.format(selectDate.time)
//                dateTo = date

                Toast.makeText(this, "Date To: " + dateToFinal, Toast.LENGTH_SHORT).show()

            },
            getDate.get(Calendar.YEAR),
            getDate.get(Calendar.MONTH),
            getDate.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()

    }

    private fun retrieveData() {
        val db = FirebaseFirestore.getInstance()
        val resultListA = ArrayList<Results>()
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
                    resultListA.add(results)
//                    Toast.makeText(this, "Done Retrieve A", Toast.LENGTH_SHORT).show()
                }


                val newResultList = ArrayList<Results>()
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")   // to format date to compare date ONLY

                var larger: Results?                    // condition: same date but later (bcz of the time)
                for(x in resultListA.indices){
                    larger = resultListA[x]
                    for(y in resultListA.indices){
                        if (simpleDateFormat.format(larger!!.date) == simpleDateFormat.format(resultListA[y].date)) {
                            if(larger!!.date!!.time < resultListA[y].date!!.time){
                                larger = resultListA[y]    // get the latest within the same date
                            }
                        }
                    }
                    if(newResultList.isNotEmpty()){
                        if(newResultList[newResultList.size - 1] != larger){
                            newResultList.add(larger!!)         // if the same date and latest time already added then will not come in
                        }
                    }else {
                        newResultList.add(larger!!)
                    }
                }

                Toast.makeText(this, "${newResultList[0].date}, ${newResultList[1].date}, ${newResultList[2].date}, ${newResultList[3].date}   oiiiasdasdasdasd", Toast.LENGTH_SHORT).show()
            }

//        val resultListB = ArrayList<Results>()
//        val trendBDocRef = db.collection("Results").document(userID)
//        trendBDocRef.collection("Result Details")
//            .get()
//            .addOnSuccessListener {
//
//                if (it.isEmpty) {
//                    Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
//                    return@addOnSuccessListener
//                }
//
//                for (doc in it) {
//                    val results = doc.toObject(Results::class.java)
//                    resultListB.add(results)
//                    Toast.makeText(this, "Done Retrieve B", Toast.LENGTH_SHORT).show()
//                }
//            }

//        for (i in resultListA.indices) {
//            for (j in resultListB.indices) {
//                val aDate = resultListA[i].date?.time
//                val bDate = resultListB[j].date?.time
//                val newResultList = ArrayList<Results>()
//                var difference = bDate!! - aDate!!
//                val minutes = difference / 60 / 1000
//                val hours = difference / 60 / 1000 / 60
//                val days = (difference / 60 / 1000 / 60) / 24
//                val months = (difference / 60 / 1000 / 60) / 24 / (365 / 12)
//                val years = difference / 60 / 1000 / 60 / 24 / 365
////                Toast.makeText(this, "Done Retrieve C", Toast.LENGTH_SHORT).show()
//
//                if (aDate < bDate) {
//                    if (years.equals(0)) {
//                        if (months.equals(0)) {
//                            if (days.equals(0)) {
//                                if (hours.equals(0)) {
//                                    if (minutes > 0) {
//                                        newResultList.add(resultListB[j])
//                                        Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                                    }
//                                    else {
//                                        newResultList.add(resultListA[i])
//                                        Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                                else {
//                                    newResultList.add(resultListB[j])
//                                    Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                            else {
//                                newResultList.add(resultListB[j])
//                                Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                        else {
//                            newResultList.add(resultListB[j])
//                            Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    else {
//                        newResultList.add(resultListB[j])
//                        Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                else if (aDate > bDate) {
//                    if (years.equals(0)) {
//                        if (months.equals(0)) {
//                            if (days.equals(0)) {
//                                if (hours.equals(0)) {
//                                    if (minutes > 0) {
//                                        newResultList.add(resultListA[i])
//                                        Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                                    }
//                                    else {
//                                        newResultList.add(resultListB[j])
//                                        Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                                else {
//                                    newResultList.add(resultListA[i])
//                                    Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                            else {
//                                newResultList.add(resultListA[i])
//                                Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                        else {
//                            newResultList.add(resultListA[i])
//                            Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    else {
//                        newResultList.add(resultListA[i])
//                        Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                else if (aDate == bDate) {
//                    newResultList.add(resultListA[i])
//                }
//
//                newResultList.sortByDescending { newResultList.size }
//
//                Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//            }
//        }
//        for (i in resultList.indices) {
//            val firstDate = resultList[i].date?.time
//            val compareDate = resultList[i + 1].date?.time
//            val newResultList = ArrayList<Results>()
//            var difference = compareDate!! - firstDate!!
//            val minutes = difference / 60 / 1000
//            val hours = difference / 60 / 1000 / 60
//            val days = (difference / 60 / 1000 / 60) / 24
//            val months = (difference / 60 / 1000 / 60) / 24 / (365 / 12)
//            val years = difference / 60 / 1000 / 60 / 24 / 365
//
//            if (firstDate!! < compareDate!!) {
//                if (years.equals(0)) {
//                    if (months.equals(0)) {
//                        if (days.equals(0)) {
//                            if (hours.equals(0)) {
//                                if (minutes > 0) {
//                                    newResultList.add(resultList[i + 1])
//                                    Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                                } else {
//                                    newResultList.add(resultList[i])
//                                    Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                                }
//                            } else {
//                                newResultList.add(resultList[i])
//                                Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }
//            }
//            else if (firstDate!! > compareDate!!) {
//                if (years.equals(0)) {
//                    if (months.equals(0)) {
//                        if (days.equals(0)) {
//                            if (hours.equals(0)) {
//                                if (minutes > 0) {
//                                    newResultList.add(resultList[i])
//                                    Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                                } else {
//                                    newResultList.add(resultList[i + 1])
//                                    Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                                }
//                            } else {
//                                newResultList.add(resultList[i + 1])
//                                Toast.makeText(this, "${newResultList[0]}", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }
//            }
//        }


    }

    private fun showTrend() {
        var difference = dateTo.time - dateFrom.time
        val days = difference / 60 / 1000 / 60 / 24


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