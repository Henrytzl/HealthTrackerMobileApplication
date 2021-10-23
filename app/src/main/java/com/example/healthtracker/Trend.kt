package com.example.healthtracker

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.trend.*
import java.util.*
import kotlin.collections.ArrayList


class Trend : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

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

        //trend option
        val weightBtn = findViewById<Button>(R.id.weightTrend)
        val weightText = findViewById<Button>(R.id.weightTrend)
        val bmiBtn = findViewById<Button>(R.id.bmiTrend)
        val bmiText = findViewById<Button>(R.id.bmiTrend)
        val bfpBtn = findViewById<Button>(R.id.bfpTrend)
        val bfpText = findViewById<Button>(R.id.bfpTrend)

        weightText.paintFlags = weightText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        setLineChartData()
        weightBtn.setOnClickListener {
            weightBtn.isSelected = true
            setLineChartData()
            weightText.paintFlags = weightText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            weightText.setTextColor(Color.parseColor("#0B89FE"))
            bmiText.paintFlags = bmiText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            bmiText.setTextColor(Color.parseColor("#5A5A5A"))
            bfpText.paintFlags = bfpText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            bfpText.setTextColor(Color.parseColor("#5A5A5A"))
        }

        bmiBtn.setOnClickListener {
            bmiBtn.isSelected = true
            bmiText.paintFlags = weightText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            bmiText.setTextColor(Color.parseColor("#0B89FE"))
            weightText.paintFlags = weightText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            weightText.setTextColor(Color.parseColor("#5A5A5A"))
            bfpText.paintFlags = bfpText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            bfpText.setTextColor(Color.parseColor("#5A5A5A"))
        }

        bfpBtn.setOnClickListener {
            bfpBtn.isSelected = true
            bfpText.paintFlags = weightText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            bfpText.setTextColor(Color.parseColor("#0B89FE"))
            weightText.paintFlags = weightText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            weightText.setTextColor(Color.parseColor("#5A5A5A"))
            bmiText.paintFlags = bmiText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            bmiText.setTextColor(Color.parseColor("#5A5A5A"))
        }

        //calendar
        pickDate()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.calendar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.calendar -> {
                pickDate()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLineChartData() {
        lineList = ArrayList()
        lineList.add(Entry(20f,55f))
        lineList.add(Entry(40f,58f))
        lineList.add(Entry(60f,63f))
        lineList.add(Entry(80f,60f))
        lineList.add(Entry(100f,57f))

        lineDataSet = LineDataSet(lineList,"Entries")
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
        val cal :Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    private fun pickDate() {
        getDateCalendar()

        DatePickerDialog(this, this, year, month, day).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateCalendar()
    }

}