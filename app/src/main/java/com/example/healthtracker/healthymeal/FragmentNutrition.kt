package com.example.healthtracker.healthymeal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import com.example.healthtracker.healthymeal.FoodAdedReycleView.RecycleViewFoodAdded
import com.example.healthtracker.healthymeal.FoodAdedReycleView.RecycleViewFoodAddedAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_nutrition_layout.*
import kotlinx.android.synthetic.main.fragment_nutrition_layout.view.*




class FragmentNutrition: Fragment(), RecycleViewFoodAddedAdapter.OnQtyAddListener, RecycleViewFoodAddedAdapter.OnQtyMinusListener, RecycleViewFoodAddedAdapter.OnItemDeleteListener {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String
    private lateinit var mealID : String
    //Recycle View
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<RecycleViewFoodAdded>
    private lateinit var adapter: RecycleViewFoodAddedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nutrition_layout, container, false)

        setUpFirebase()

        mealID = requireArguments().get("mealID").toString()
        //Recycle View
        recyclerView = view.recyclerFoods
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.setHasFixedSize(true)
        list = arrayListOf()

        adapter = RecycleViewFoodAddedAdapter(list, this, this, this)
        recyclerView.adapter = adapter

        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FireStore Error", error.message.toString())
                }else {
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            list.add(dc.document.toObject(RecycleViewFoodAdded::class.java))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

        return view
    }

    override fun onStart() {
        super.onStart()
        mealID = requireArguments().get("mealID").toString()
        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { result ->
            nutrition_mealName.text = result.getString("mealName")
            mealKcal.text = result.get("kcal").toString()
            mealProtein.text = result.get("protein").toString()
            mealFat.text = result.get("fat").toString()
            mealCarb.text = result.get("carb").toString()
            mealSugar.text = result.get("sugar").toString()
        }.addOnFailureListener {
            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAddClick(position: Int) {
        val clickedItem = list[position]
    }

    override fun onMinusClick(position: Int) {
        val clickedItem = list[position]
    }

    override fun onDeleteClick(position: Int) {
        val clickedItem = list[position]
    }

    fun receiveMealID(data: String?){
        if (data != null) {
            mealID = data
            firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { result ->
                nutrition_mealName.text = result.getString("mealName")
                mealKcal.text = result.get("kcal").toString()
                mealProtein.text = result.get("protein").toString()
                mealFat.text = result.get("fat").toString()
                mealCarb.text = result.get("carb").toString()
                mealSugar.text = result.get("sugar").toString()
            }.addOnFailureListener {
                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun receiveFoodID(foodID: String?){
        if (foodID != null) {
            var food: RecycleViewFoodAdded = RecycleViewFoodAdded()
            firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document(foodID).get().addOnSuccessListener { it ->
                food.carb = it.get("carb").toString().toInt()
                food.fat = it.get("fat").toString().toInt()
                food.foodID = foodID
                food.foodName = it.get("foodName").toString()
                food.kcal = it.get("kcal").toString().toInt()
                food.protein = it.get("protein").toString().toInt()
                food.sugar = it.get("sugar").toString().toInt()
                food.noOfUnit = it.get("noOfUnit").toString().toInt()
                food.qty = 1
                food.userID = it.get("userID").toString()

                list.add(list.size, food)
                adapter.notifyItemInserted(list.size)
            }.addOnFailureListener {
                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
            }

//        val item = "l"
//        val insertIndex = 2
//        data.add(insertIndex, item)
//        adapter.notifyItemInserted(insertIndex)
        }
    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}