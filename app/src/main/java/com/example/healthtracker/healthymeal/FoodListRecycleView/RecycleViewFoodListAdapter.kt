package com.example.healthtracker.healthymeal.FoodListRecycleView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.food_list_view.view.*


class RecycleViewFoodListAdapter(
    private val list: ArrayList<RecycleViewFoodList>,
    private val listener: OnItemClickListener,
    private val addListener: OnAddClickListener): RecyclerView.Adapter<RecycleViewFoodListAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.food_list_view, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecycleViewFoodListAdapter.MyViewHolder, position: Int) {
        val food: RecycleViewFoodList = list[position]
        holder.foodName.text = food.getFoodName()
        holder.addBtn.setOnClickListener {
            addListener.onAddClick(position)
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
        var foodName : TextView = itemView.txt_recycleView_foodName
        var addBtn : ImageButton = itemView.btn_recycleView_addFood

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v:View?){
            if(this.adapterPosition != RecyclerView.NO_POSITION){
                listener.onItemClick(this.adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    interface OnAddClickListener{
        fun onAddClick(position: Int)
    }
}