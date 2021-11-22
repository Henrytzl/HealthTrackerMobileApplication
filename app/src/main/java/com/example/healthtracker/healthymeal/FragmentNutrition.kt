package com.example.healthtracker.healthymeal

import android.app.AlertDialog
import android.content.DialogInterface
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        list[position].qty++
        adapter.notifyDataSetChanged()
        val mealRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID)
        val foodInMealRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document(list[position].foodID)
        //Update food qty and UI meal nutrition
        foodInMealRef.update("qty", list[position].qty).addOnSuccessListener {
            mealKcal.text = (mealKcal.text.toString().toInt() + list[position].kcal).toString()
            mealProtein.text = (mealProtein.text.toString().toInt() + list[position].protein).toString()
            mealFat.text = (mealFat.text.toString().toInt() + list[position].fat).toString()
            mealCarb.text = (mealCarb.text.toString().toInt() + list[position].carb).toString()
            mealSugar.text = (mealSugar.text.toString().toInt() + list[position].sugar).toString()

            //Update Meal Nutrition
            mealRef.update(
                "carb", mealCarb.text.toString().toInt(),
                "kcal", mealKcal.text.toString().toInt(),
                "protein", mealProtein.text.toString().toInt(),
                "fat", mealFat.text.toString().toInt(),
                "sugar", mealSugar.text.toString().toInt()
            ).addOnFailureListener {
                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMinusClick(position: Int) {
        if(list[position].qty > 1){
            list[position].qty--
            adapter.notifyDataSetChanged()
            val mealRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID)
            val foodInMealRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document(list[position].foodID)
            //Update food qty and UI meal nutrition
            foodInMealRef.update("qty", list[position].qty).addOnSuccessListener {
                mealKcal.text = (mealKcal.text.toString().toInt() - list[position].kcal).toString()
                mealProtein.text = (mealProtein.text.toString().toInt() - list[position].protein).toString()
                mealFat.text = (mealFat.text.toString().toInt() - list[position].fat).toString()
                mealCarb.text = (mealCarb.text.toString().toInt() - list[position].carb).toString()
                mealSugar.text = (mealSugar.text.toString().toInt() - list[position].sugar).toString()

                //Update Meal Nutrition
                mealRef.update(
                    "carb", mealCarb.text.toString().toInt(),
                    "kcal", mealKcal.text.toString().toInt(),
                    "protein", mealProtein.text.toString().toInt(),
                    "fat", mealFat.text.toString().toInt(),
                    "sugar", mealSugar.text.toString().toInt()
                ).addOnFailureListener {
                    Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(context, "Minimum quantity is 1", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeleteClick(position: Int) {
        val clickedItem = list[position]
        val foodInMealRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document(list[position].foodID)
        val mealRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID)
        val deleteViewBuilder = AlertDialog.Builder(context).setTitle("Delete Food")
            .setIcon(R.drawable.ic_delete2).setMessage("Are you sure to delete this food?")
            .setCancelable(false).setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            }).setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                foodInMealRef.delete().addOnSuccessListener {
                    mealKcal.text = (mealKcal.text.toString().toInt() - (clickedItem.kcal * clickedItem.qty)).toString()
                    mealProtein.text = (mealProtein.text.toString().toInt() - (clickedItem.protein * clickedItem.qty)).toString()
                    mealFat.text = (mealFat.text.toString().toInt() - (clickedItem.fat * clickedItem.qty)).toString()
                    mealCarb.text = (mealCarb.text.toString().toInt() - (clickedItem.carb * clickedItem.qty)).toString()
                    mealSugar.text = (mealSugar.text.toString().toInt() - (clickedItem.sugar * clickedItem.qty)).toString()
                    dialog.dismiss()
                    Toast.makeText(context, "Food deleted successfully", Toast.LENGTH_SHORT).show()
                    list.removeAt(position)
                    adapter.notifyDataSetChanged()

                    mealRef.update(
                        "carb", mealCarb.text.toString().toInt(),
                        "kcal", mealKcal.text.toString().toInt(),
                        "protein", mealProtein.text.toString().toInt(),
                        "fat", mealFat.text.toString().toInt(),
                        "sugar", mealSugar.text.toString().toInt()
                    ).addOnFailureListener {
                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context," " + it.message, Toast.LENGTH_LONG).show()
                }
            })
        //show dialog
        val displayDialog = deleteViewBuilder.create()
        displayDialog.show()
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

//    fun receiveFoodID(foodID: String?){
//        if (foodID != null) {
//            var food: RecycleViewFoodAdded = RecycleViewFoodAdded()
//            firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document(foodID).get().addOnSuccessListener { it ->
//                food.carb = it.get("carb").toString().toInt()
//                food.fat = it.get("fat").toString().toInt()
//                food.foodID = foodID
//                food.foodName = it.get("foodName").toString()
//                food.kcal = it.get("kcal").toString().toInt()
//                food.protein = it.get("protein").toString().toInt()
//                food.sugar = it.get("sugar").toString().toInt()
//                food.noOfUnit = it.get("noOfUnit").toString().toInt()
//                food.qty = 1
//                food.userID = it.get("userID").toString()
//
//                adapter.notifyDataSetChanged()
//            }.addOnFailureListener {
//                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//            }
//
////        val item = "l"
////        val insertIndex = 2
////        data.add(insertIndex, item)
////        adapter.notifyItemInserted(insertIndex)
//        }
//    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}