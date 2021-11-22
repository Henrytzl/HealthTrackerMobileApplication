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
import com.example.healthtracker.dataclass.FoodWithQtyDC
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

        view.btn_add_defaultMeal1.setOnClickListener {
//            pd = activity as PassingDataTabs
//            var breakfast1: FoodWithQtyDC? = null
//            var breakfast2: FoodWithQtyDC? = null
//            var breakfast3: FoodWithQtyDC? = null
//            val checkApple = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("apple")
//            val checkMilk = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("milk")
//            val checkBread = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("bread")
//            checkApple.get().addOnSuccessListener { result ->
//                if (result.exists()) {
//                    Toast.makeText(context, "Apple is already added", Toast.LENGTH_SHORT).show()
//                } else {
//                    for (i in list.indices) {
//                        if (list[i].foodID == "apple") {
//                            breakfast1 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 1)
//                            firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("apple").set(breakfast1!!).addOnSuccessListener {
//                                firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { getResult ->
//                                    firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update(
//                                        "carb", (getResult.get("carb").toString().toInt() + breakfast1!!.carb),
//                                        "fat", (getResult.get("fat").toString().toInt() + breakfast1!!.fat),
//                                        "kcal", (getResult.get("kcal").toString().toInt() + breakfast1!!.kcal),
//                                        "protein", (getResult.get("protein").toString().toInt() + breakfast1!!.protein),
//                                        "sugar", (getResult.get("sugar").toString().toInt() + breakfast1!!.sugar),
//                                    ).addOnSuccessListener {
//                                        Toast.makeText(context, "Apple added successfully", Toast.LENGTH_SHORT).show()
//                                        pd.sendMealID(mealID)
//                                    }.addOnFailureListener {
//                                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//                                    }
//                                }.addOnFailureListener {
//                                    Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//                                }
//                            }.addOnFailureListener {
//                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }
//            }.addOnFailureListener {
//                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//            }
//            checkMilk.get().addOnSuccessListener { result ->
//                if (result.exists()) {
//                    Toast.makeText(context, "Milk is already added", Toast.LENGTH_SHORT).show()
//                } else {
//                    for (i in list.indices) {
//                        if (list[i].foodID == "milk") {
//                            breakfast2 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 1)
//                            firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("milk").set(breakfast2!!).addOnSuccessListener {
//                                firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { getResult ->
//                                    firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update(
//                                        "carb", (getResult.get("carb").toString().toInt() + breakfast2!!.carb),
//                                        "fat", (getResult.get("fat").toString().toInt() + breakfast2!!.fat),
//                                        "kcal", (getResult.get("kcal").toString().toInt() + breakfast2!!.kcal),
//                                        "protein", (getResult.get("protein").toString().toInt() + breakfast2!!.protein),
//                                        "sugar", (getResult.get("sugar").toString().toInt() + breakfast2!!.sugar),
//                                    ).addOnSuccessListener {
//                                        Toast.makeText(context, "Apple added successfully", Toast.LENGTH_SHORT).show()
//                                        pd.sendMealID(mealID)
//                                    }.addOnFailureListener {
//                                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//                                    }
//                                }.addOnFailureListener {
//                                    Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//                                }
//                            }.addOnFailureListener {
//                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }
//            }.addOnFailureListener {
//                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//            }
//            checkBread.get().addOnSuccessListener { result ->
//                if (result.exists()) {
//                    Toast.makeText(context, "Milk is already added", Toast.LENGTH_SHORT).show()
//                } else {
//                    for (i in list.indices) {
//                        if (list[i].foodID == "bread") {
//                            breakfast3 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 1)
//                            firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("bread").set(breakfast3!!).addOnSuccessListener {
//                                firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { getResult ->
//                                    firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update(
//                                        "carb", (getResult.get("carb").toString().toInt() + breakfast3!!.carb),
//                                        "fat", (getResult.get("fat").toString().toInt() + breakfast3!!.fat),
//                                        "kcal", (getResult.get("kcal").toString().toInt() + breakfast3!!.kcal),
//                                        "protein", (getResult.get("protein").toString().toInt() + breakfast3!!.protein),
//                                        "sugar", (getResult.get("sugar").toString().toInt() + breakfast3!!.sugar),
//                                    ).addOnSuccessListener {
//                                        Toast.makeText(context, "Apple added successfully", Toast.LENGTH_SHORT).show()
//                                        pd.sendMealID(mealID)
//                                    }.addOnFailureListener {
//                                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//                                    }
//                                }.addOnFailureListener {
//                                    Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//                                }
//                            }.addOnFailureListener {
//                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }
//            }.addOnSuccessListener {
//
//            }.addOnFailureListener {
//                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
//            }
        }

        view.btn_add_defaultMeal2.setOnClickListener {

        }

        view.btn_add_defaultMeal3.setOnClickListener {

        }

        return view
    }
    override fun onItemClick(position: Int) {
        val clickedItem = list[position]

    }

    override fun onAddClick(position: Int) {
        pd = activity as PassingDataTabs
        val clickedItem = list[position]
        val food: FoodWithQtyDC = FoodWithQtyDC(clickedItem.foodName, clickedItem.kcal, clickedItem.protein, clickedItem.fat, clickedItem.carb, clickedItem.sugar, clickedItem.noOfUnit, clickedItem.userID, clickedItem.foodID, 1)
        val checkFood = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document(clickedItem.foodID)
        checkFood.get().addOnSuccessListener { result->
            if (result.exists()){
                Toast.makeText(context, "This food is already added", Toast.LENGTH_SHORT).show()
            }else{
                firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document(clickedItem.foodID).set(food).addOnSuccessListener {
                    firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { getResult ->
                        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update(
                            "carb", (getResult.get("carb").toString().toInt() + food.carb),
                            "fat", (getResult.get("fat").toString().toInt() + food.fat),
                            "kcal", (getResult.get("kcal").toString().toInt() + food.kcal),
                            "protein", (getResult.get("protein").toString().toInt() + food.protein),
                            "sugar", (getResult.get("sugar").toString().toInt() + food.sugar),
                        ).addOnSuccessListener {
                            Toast.makeText(context, "Food added", Toast.LENGTH_SHORT).show()
                            pd.sendMealID(mealID)
                            //pd.sendFoodID(clickedItem.foodID)
                        }.addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pd = activity as PassingDataTabs
        view.btn_edit_mealName.setOnClickListener {
            val mealName = view.txtMealName.text.toString()
            if(mealNameValidation(mealName)){
                firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update("mealName", mealName).addOnSuccessListener {
                    Toast.makeText(context, "Meal Name has been updated", Toast.LENGTH_SHORT).show()
                    pd.sendMealID(mealID)
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