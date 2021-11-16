package com.example.healthtracker.healthymeal.MealRecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.meals_view.view.*

class RecycleViewMealAdapter(
    private val list: ArrayList<RecycleViewMeal>,
    private val listener: OnItemClickListener,
    private val deleteListener: OnDeleteClickListener) : RecyclerView.Adapter<RecycleViewMealAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.meals_view, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val meal: RecycleViewMeal = list[position]
        holder.mealName.text = meal.getMealName()
        holder.deleteBtn.setOnClickListener {
            deleteListener.onItemDelete(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
        var mealName: TextView = itemView.mealName
        var deleteBtn: ImageButton = itemView.btnDeleteMeal

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
    interface OnDeleteClickListener{
        fun onItemDelete(position: Int)
    }
}