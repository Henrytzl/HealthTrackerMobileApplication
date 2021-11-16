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
import com.example.healthtracker.healthymeal.FoodListRecycleView.RecycleViewFoodList
import com.example.healthtracker.healthymeal.FoodListRecycleView.RecycleViewFoodListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_food_layout.view.*

class FragmentFood: Fragment(), RecycleViewFoodListAdapter.OnItemClickListener, RecycleViewFoodListAdapter.OnAddClickListener {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String
    private lateinit var pd : PassingDataTabs
    private lateinit var mealID : String
    //Recycle View
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<RecycleViewFoodList>
    private lateinit var adapter: RecycleViewFoodListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_food_layout, container, false)

        setUpFirebase()

        mealID = requireArguments().get("mealID").toString()

        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { result->
            view.txtMealName.setText(result.getString("mealName"))
        }.addOnFailureListener {
            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
        }

        //Recycle View
        recyclerView = view.recyclerViewFoodList
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.setHasFixedSize(true)
        list = arrayListOf()

        adapter = RecycleViewFoodListAdapter(list, this, this)
        recyclerView.adapter = adapter

        firebase.collection("Food").whereIn("userID", listOf(userID, ""))
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FireStore Error", error.message.toString())
                }else {
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            list.add(dc.document.toObject(RecycleViewFoodList::class.java))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

        return view
    }
    override fun onItemClick(position: Int) {
        val clickedItem = list[position]
    }

    override fun onAddClick(position: Int) {
        val clickedItem = list[position]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pd = activity as PassingDataTabs
        view.btn_edit_mealName.setOnClickListener {
            val mealName = view.txtMealName.text.toString()
            if(mealNameValidation(mealName)){
                firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update("mealName", mealName).addOnSuccessListener {
                    Toast.makeText(context, "Meal Name has been updated", Toast.LENGTH_SHORT).show()
                    pd.sendData(mealID)
                    view.txtMealName.clearFocus()
                }.addOnFailureListener {
                    Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mealNameValidation(mealName: String):Boolean{
        var valid = true
        if(mealName == ""){
            Toast.makeText(context, "Please enter a meal name", Toast.LENGTH_SHORT).show()
            valid = false
        }
        return valid
    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }


}