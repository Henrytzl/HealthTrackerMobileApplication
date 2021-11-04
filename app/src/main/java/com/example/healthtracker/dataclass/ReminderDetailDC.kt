package com.example.healthtracker.dataclass

import java.sql.Timestamp

data class ReminderDetailDC(var reminderTitle:String?="", var reminderDesc:String?="", var reminderTime:Timestamp, var reminderActivate: Boolean)
