package com.example.healthtracker.dataclass

data class ReminderDetailDC(var reminderID:String?="", var reminderTitle:String?="", var reminderDesc:String?="", var reminderTime:String?="", var reminderActivate: Boolean, var selectedDay: ArrayList<String>, var requestCodeID: Int)
