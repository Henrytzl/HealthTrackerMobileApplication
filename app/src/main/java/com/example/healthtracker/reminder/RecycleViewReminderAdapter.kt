package com.example.healthtracker.reminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.reminder_view.view.*

class RecycleViewReminderAdapter(private val list: ArrayList<RecycleViewReminder>) : RecyclerView.Adapter<RecycleViewReminderAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.reminder_view, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val reminder: RecycleViewReminder = list.get(position)
        holder.reminderName.text = reminder.getReminderTitle()
        holder.reminderTime.text = reminder.getReminderTime()
        holder.reminderActivate.isChecked = reminder.getReminderActivate()
    }
    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var reminderName: TextView
        lateinit var reminderTime: TextView
        lateinit var reminderActivate: SwitchCompat

        init {
            reminderName = itemView.reminder_name
            reminderTime = itemView.reminder_time
            reminderActivate = itemView.reminderSwitch
        }
    }
}