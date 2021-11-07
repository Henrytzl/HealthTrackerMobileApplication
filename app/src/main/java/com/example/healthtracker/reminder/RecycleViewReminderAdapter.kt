package com.example.healthtracker.reminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.reminder_view.view.*

class RecycleViewReminderAdapter(
    private val list: ArrayList<RecycleViewReminder>,
    private val listener: OnItemClickListener,
    private val switchListener: OnSwitchClickListener) : RecyclerView.Adapter<RecycleViewReminderAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.reminder_view, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val reminder: RecycleViewReminder = list[position]
        holder.reminderTitle.text = reminder.getReminderTitle()
        holder.reminderTime.text = reminder.getReminderTime()
        holder.reminderActivate.isChecked = reminder.getReminderActivate()
        holder.reminderActivate.setOnClickListener {
            switchListener.onSwitchClickListener(position)
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
        var reminderTitle: TextView = itemView.reminder_name
        var reminderTime: TextView = itemView.reminder_time
        var reminderActivate: SwitchCompat = itemView.reminderSwitch

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v:View?){
            if(this.adapterPosition != RecyclerView.NO_POSITION){
                listener.onItemClick(this.adapterPosition)
            }
        }

    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    interface OnSwitchClickListener{
        fun onSwitchClickListener(position: Int)
    }
}