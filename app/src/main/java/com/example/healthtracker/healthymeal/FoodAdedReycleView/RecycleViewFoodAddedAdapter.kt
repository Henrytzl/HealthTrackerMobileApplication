package com.example.healthtracker.healthymeal.FoodAdedReycleView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.foods_per_meal_view.view.*

class RecycleViewFoodAddedAdapter(
    private val list: ArrayList<RecycleViewFoodAdded>,
    private val addListener: OnQtyAddListener,
    private val minusListener: OnQtyMinusListener,
    private val deleteListener: OnItemDeleteListener): RecyclerView.Adapter<RecycleViewFoodAddedAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewFoodAddedAdapter.MyViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.foods_per_meal_view, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecycleViewFoodAddedAdapter.MyViewHolder, position: Int) {
        val food: RecycleViewFoodAdded = list[position]
        holder.foodName.text = food.getFoodName()
        holder.foodQty.text = food.getQty().toString()
        holder.add.setOnClickListener {
            addListener.onAddClick(position)
        }
        holder.minus.setOnClickListener {
            minusListener.onMinusClick(position)
        }
        holder.delete.setOnClickListener {
            deleteListener.onDeleteClick(position)
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var foodName: TextView = itemView.food_name
        var foodQty: TextView = itemView.food_qty
        var add: ImageButton = itemView.btn_add_foodQty
        var minus: ImageButton = itemView.btn_minus_foodQty
        var delete: ImageButton = itemView.btn_delete_food
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnQtyAddListener{
        fun onAddClick(position: Int)
    }
    interface OnQtyMinusListener{
        fun onMinusClick(position: Int)
    }
    interface OnItemDeleteListener{
        fun onDeleteClick(position: Int)
    }

}