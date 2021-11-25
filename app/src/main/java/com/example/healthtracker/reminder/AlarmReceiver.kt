package com.example.healthtracker.reminder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val i = Intent(context, MainActivity::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, i, 0)

        val getTitle = intent.getStringExtra("reminderTitle").toString()
        val getDesc = intent.getStringExtra("reminderDesc").toString()

        val builder = NotificationCompat.Builder(context!!, "notification")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getTitle)
            .setContentText(getDesc)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build())
    }
}