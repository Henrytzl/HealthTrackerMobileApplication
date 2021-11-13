package com.example.healthtracker.scanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.scanner_history_view.view.*

class RecycleViewFoodHistoryAdapter(
    private val list: ArrayList<RecycleViewFoodHistory>,
    private val listener: OnItemClickListener) : RecyclerView.Adapter<RecycleViewFoodHistoryAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.scanner_history_view, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val foodHistory: RecycleViewFoodHistory = list[position]
        holder.foodName.text = foodHistory.getFoodHistoryName()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
        var foodName: Button = itemView.btn_scannerFoodHistory

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
}