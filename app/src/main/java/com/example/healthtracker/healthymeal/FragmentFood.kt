package com.example.healthtracker.healthymeal

import android.content.Intent
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
            pd = activity as PassingDataTabs
            var breakfast1: FoodWithQtyDC? = null       //apple     1
            var breakfast2: FoodWithQtyDC? = null       //milk      1
            var breakfast3: FoodWithQtyDC? = null       //bread     1
            for (i in list.indices) {
                if (list[i].foodID == "apple") {
                    breakfast1 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 1)
                }else if(list[i].foodID == "milk"){
                    breakfast2 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 1)
                }else if(list[i].foodID == "bread"){
                    breakfast3 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 1)
                }
            }
            val check = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").whereIn("foodID", listOf("apple","milk","bread"))
            val appleRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("apple")
            val milkRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("milk")
            val breadRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("bread")

            check.get().addOnSuccessListener { result ->
                if(!result.isEmpty) {
                    var appleExist = false
                    var milkExist = false
                    var breadExist = false
                    for (i in result.documents.indices) {
                        if (result.documents[i].get("foodID").toString() == "apple") {
                            appleExist = true
                            Toast.makeText(context, "Apple is already added", Toast.LENGTH_SHORT).show()
                        } else if (result.documents[i].get("foodID").toString() == "milk") {
                            milkExist = true
                            Toast.makeText(context, "Milk is already added", Toast.LENGTH_SHORT).show()
                        } else if (result.documents[i].get("foodID").toString() == "bread") {
                            breadExist = true
                            Toast.makeText(context, "Bread is already added", Toast.LENGTH_SHORT).show()
                        }
                    }
                    var carb: Int = 0
                    var fat: Int = 0
                    var kcal: Int = 0
                    var protein: Int = 0
                    var sugar: Int = 0
                    if (!appleExist){
                        appleRef.set(breakfast1!!).addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                        carb += breakfast1!!.carb
                        fat += breakfast1!!.fat
                        kcal += breakfast1!!.kcal
                        protein += breakfast1!!.protein
                        sugar += breakfast1!!.sugar
                    }
                    if(!milkExist){
                        milkRef.set(breakfast2!!).addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                        carb += breakfast2!!.carb
                        fat += breakfast2!!.fat
                        kcal += breakfast2!!.kcal
                        protein += breakfast2!!.protein
                        sugar += breakfast2!!.sugar
                    }
                    if(!breadExist){
                        breadRef.set(breakfast3!!).addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                        carb += breakfast3!!.carb
                        fat += breakfast3!!.fat
                        kcal += breakfast3!!.kcal
                        protein += breakfast3!!.protein
                        sugar += breakfast3!!.sugar
                    }
                    if(!appleExist || !milkExist || !breadExist){
                        Toast.makeText(context, "Meal 1 (Breakfast) added", Toast.LENGTH_SHORT).show()
                    }
                    firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { getResult ->
                        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update(
                            "carb", (getResult.get("carb").toString().toInt() + carb),
                            "fat", (getResult.get("fat").toString().toInt() + fat),
                            "kcal", (getResult.get("kcal").toString().toInt() + kcal),
                            "protein", (getResult.get("protein").toString().toInt() + protein),
                            "sugar", (getResult.get("sugar").toString().toInt() + sugar),
                        ).addOnSuccessListener {
                            pd.sendMealID(mealID)
                        }.addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { getResult ->
                        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update(
                            "carb", (getResult.get("carb").toString().toInt() + breakfast1!!.carb + breakfast2!!.carb + breakfast3!!.carb),
                            "fat", (getResult.get("fat").toString().toInt() + breakfast1!!.fat + breakfast2!!.fat + breakfast3!!.fat),
                            "kcal", (getResult.get("kcal").toString().toInt() + breakfast1!!.kcal + breakfast2!!.kcal + breakfast3!!.kcal),
                            "protein", (getResult.get("protein").toString().toInt() + breakfast1!!.protein + breakfast2!!.protein + breakfast3!!.protein),
                            "sugar", (getResult.get("sugar").toString().toInt() + breakfast1!!.sugar + breakfast2!!.sugar + breakfast3!!.sugar),
                        ).addOnSuccessListener {
                            appleRef.set(breakfast1!!).addOnFailureListener {
                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                            }
                            milkRef.set(breakfast2!!).addOnFailureListener {
                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                            }
                            breadRef.set(breakfast3!!).addOnFailureListener {
                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                            }
                            pd.sendMealID(mealID)
                            Toast.makeText(context, "Meal 1 (Breakfast) added", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        view.btn_add_defaultMeal2.setOnClickListener {
            pd = activity as PassingDataTabs
            var breakfast1: FoodWithQtyDC? = null       //asparagus     2
            var breakfast2: FoodWithQtyDC? = null       //whiteRice     1
            var breakfast3: FoodWithQtyDC? = null       //salmon        2
            for (i in list.indices) {
                if (list[i].foodID == "asparagus") {
                    breakfast1 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 2)
                }else if(list[i].foodID == "whiteRice"){
                    breakfast2 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 1)
                }else if(list[i].foodID == "salmon"){
                    breakfast3 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 2)
                }
            }
            val check = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").whereIn("foodID", listOf("asparagus","whiteRice","salmon"))
            val asparagusRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("asparagus")
            val whiteRiceRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("whiteRice")
            val salmonRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("salmon")

            check.get().addOnSuccessListener { result ->
                if(!result.isEmpty) {
                    var asparagusExist = false
                    var whiteRiceExist = false
                    var salmonExist = false
                    for (i in result.documents.indices) {
                        if (result.documents[i].get("foodID").toString() == "asparagus") {
                            asparagusExist = true
                            Toast.makeText(context, "Asparagus is already added", Toast.LENGTH_SHORT).show()
                        } else if (result.documents[i].get("foodID").toString() == "whiteRice") {
                            whiteRiceExist = true
                            Toast.makeText(context, "White Rice is already added", Toast.LENGTH_SHORT).show()
                        } else if (result.documents[i].get("foodID").toString() == "salmon") {
                            salmonExist = true
                            Toast.makeText(context, "Salmon is already added", Toast.LENGTH_SHORT).show()
                        }
                    }
                    var carb: Int = 0
                    var fat: Int = 0
                    var kcal: Int = 0
                    var protein: Int = 0
                    var sugar: Int = 0
                    if (!asparagusExist){
                        asparagusRef.set(breakfast1!!).addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                        carb += breakfast1!!.carb * breakfast1!!.qty
                        fat += breakfast1!!.fat * breakfast1!!.qty
                        kcal += breakfast1!!.kcal * breakfast1!!.qty
                        protein += breakfast1!!.protein * breakfast1!!.qty
                        sugar += breakfast1!!.sugar * breakfast1!!.qty
                    }
                    if(!whiteRiceExist){
                        whiteRiceRef.set(breakfast2!!).addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                        carb += breakfast2!!.carb * breakfast2!!.qty
                        fat += breakfast2!!.fat * breakfast2!!.qty
                        kcal += breakfast2!!.kcal * breakfast2!!.qty
                        protein += breakfast2!!.protein * breakfast2!!.qty
                        sugar += breakfast2!!.sugar * breakfast2!!.qty
                    }
                    if(!salmonExist){
                        salmonRef.set(breakfast3!!).addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                        carb += breakfast3!!.carb * breakfast3!!.qty
                        fat += breakfast3!!.fat * breakfast3!!.qty
                        kcal += breakfast3!!.kcal * breakfast3!!.qty
                        protein += breakfast3!!.protein * breakfast3!!.qty
                        sugar += breakfast3!!.sugar * breakfast3!!.qty
                    }
                    if(!asparagusExist || !whiteRiceExist || !salmonExist){
                        Toast.makeText(context, "Meal 2 (Lunch) added", Toast.LENGTH_SHORT).show()
                    }
                    firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { getResult ->
                        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update(
                            "carb", (getResult.get("carb").toString().toInt() + carb),
                            "fat", (getResult.get("fat").toString().toInt() + fat),
                            "kcal", (getResult.get("kcal").toString().toInt() + kcal),
                            "protein", (getResult.get("protein").toString().toInt() + protein),
                            "sugar", (getResult.get("sugar").toString().toInt() + sugar),
                        ).addOnSuccessListener {
                            pd.sendMealID(mealID)
                        }.addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { getResult ->
                        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update(
                            "carb", (getResult.get("carb").toString().toInt() + breakfast1!!.carb * breakfast1!!.qty + breakfast2!!.carb * breakfast2!!.qty + breakfast3!!.carb * breakfast3!!.qty),
                            "fat", (getResult.get("fat").toString().toInt() + breakfast1!!.fat * breakfast1!!.qty + breakfast2!!.fat * breakfast2!!.qty + breakfast3!!.fat * breakfast3!!.qty),
                            "kcal", (getResult.get("kcal").toString().toInt() + breakfast1!!.kcal * breakfast1!!.qty + breakfast2!!.kcal * breakfast2!!.qty + breakfast3!!.kcal * breakfast3!!.qty),
                            "protein", (getResult.get("protein").toString().toInt() + breakfast1!!.protein * breakfast1!!.qty + breakfast2!!.protein * breakfast2!!.qty + breakfast3!!.protein * breakfast3!!.qty),
                            "sugar", (getResult.get("sugar").toString().toInt() + breakfast1!!.sugar * breakfast1!!.qty + breakfast2!!.sugar * breakfast2!!.qty + breakfast3!!.sugar * breakfast3!!.qty),
                        ).addOnSuccessListener {
                            asparagusRef.set(breakfast1!!).addOnFailureListener {
                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                            }
                            whiteRiceRef.set(breakfast2!!).addOnFailureListener {
                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                            }
                            salmonRef.set(breakfast3!!).addOnFailureListener {
                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                            }
                            pd.sendMealID(mealID)
                            Toast.makeText(context, "Meal 2 (Lunch) added", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        view.btn_add_defaultMeal3.setOnClickListener {
            pd = activity as PassingDataTabs
            var breakfast1: FoodWithQtyDC? = null       //potato        2
            var breakfast2: FoodWithQtyDC? = null       //chickenBreast 1
            var breakfast3: FoodWithQtyDC? = null       //broccoli      1
            for (i in list.indices) {
                if (list[i].foodID == "potato") {
                    breakfast1 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 2)
                }else if(list[i].foodID == "chickenBreast"){
                    breakfast2 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 1)
                }else if(list[i].foodID == "broccoli"){
                    breakfast3 = FoodWithQtyDC(list[i].foodName, list[i].kcal, list[i].protein, list[i].fat, list[i].carb, list[i].sugar, list[i].noOfUnit, list[i].userID, list[i].foodID, 1)
                }
            }
            val check = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").whereIn("foodID", listOf("potato","chickenBreast","broccoli"))
            val potatoRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("potato")
            val chickenBreastRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("chickenBreast")
            val broccoliRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).collection("Foods").document("broccoli")

            check.get().addOnSuccessListener { result ->
                if(!result.isEmpty) {
                    var potatoExist = false
                    var chickenBreastExist = false
                    var broccoliExist = false
                    for (i in result.documents.indices) {
                        if (result.documents[i].get("foodID").toString() == "potato") {
                            potatoExist = true
                            Toast.makeText(context, "Potato is already added", Toast.LENGTH_SHORT).show()
                        } else if (result.documents[i].get("foodID").toString() == "chickenBreast") {
                            chickenBreastExist = true
                            Toast.makeText(context, "Chicken Breast is already added", Toast.LENGTH_SHORT).show()
                        } else if (result.documents[i].get("foodID").toString() == "broccoli") {
                            broccoliExist = true
                            Toast.makeText(context, "Broccoli is already added", Toast.LENGTH_SHORT).show()
                        }
                    }
                    var carb: Int = 0
                    var fat: Int = 0
                    var kcal: Int = 0
                    var protein: Int = 0
                    var sugar: Int = 0
                    if (!potatoExist){
                        potatoRef.set(breakfast1!!).addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                        carb += breakfast1!!.carb * breakfast1!!.qty
                        fat += breakfast1!!.fat * breakfast1!!.qty
                        kcal += breakfast1!!.kcal * breakfast1!!.qty
                        protein += breakfast1!!.protein * breakfast1!!.qty
                        sugar += breakfast1!!.sugar * breakfast1!!.qty
                    }
                    if(!chickenBreastExist){
                        chickenBreastRef.set(breakfast2!!).addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                        carb += breakfast2!!.carb * breakfast2!!.qty
                        fat += breakfast2!!.fat * breakfast2!!.qty
                        kcal += breakfast2!!.kcal * breakfast2!!.qty
                        protein += breakfast2!!.protein * breakfast2!!.qty
                        sugar += breakfast2!!.sugar * breakfast2!!.qty
                    }
                    if(!broccoliExist){
                        broccoliRef.set(breakfast3!!).addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                        carb += breakfast3!!.carb * breakfast3!!.qty
                        fat += breakfast3!!.fat * breakfast3!!.qty
                        kcal += breakfast3!!.kcal * breakfast3!!.qty
                        protein += breakfast3!!.protein * breakfast3!!.qty
                        sugar += breakfast3!!.sugar * breakfast3!!.qty
                    }
                    if(!potatoExist || !chickenBreastExist || !broccoliExist){
                        Toast.makeText(context, "Meal 3 (Dinner) added", Toast.LENGTH_SHORT).show()
                    }
                    firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { getResult ->
                        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update(
                            "carb", (getResult.get("carb").toString().toInt() + carb),
                            "fat", (getResult.get("fat").toString().toInt() + fat),
                            "kcal", (getResult.get("kcal").toString().toInt() + kcal),
                            "protein", (getResult.get("protein").toString().toInt() + protein),
                            "sugar", (getResult.get("sugar").toString().toInt() + sugar),
                        ).addOnSuccessListener {
                            pd.sendMealID(mealID)
                        }.addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { getResult ->
                        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).update(
                            "carb", (getResult.get("carb").toString().toInt() + breakfast1!!.carb * breakfast1!!.qty + breakfast2!!.carb * breakfast2!!.qty + breakfast3!!.carb * breakfast3!!.qty),
                            "fat", (getResult.get("fat").toString().toInt() + breakfast1!!.fat * breakfast1!!.qty + breakfast2!!.fat * breakfast2!!.qty + breakfast3!!.fat * breakfast3!!.qty),
                            "kcal", (getResult.get("kcal").toString().toInt() + breakfast1!!.kcal * breakfast1!!.qty + breakfast2!!.kcal * breakfast2!!.qty + breakfast3!!.kcal * breakfast3!!.qty),
                            "protein", (getResult.get("protein").toString().toInt() + breakfast1!!.protein * breakfast1!!.qty + breakfast2!!.protein * breakfast2!!.qty + breakfast3!!.protein * breakfast3!!.qty),
                            "sugar", (getResult.get("sugar").toString().toInt() + breakfast1!!.sugar * breakfast1!!.qty + breakfast2!!.sugar * breakfast2!!.qty + breakfast3!!.sugar * breakfast3!!.qty),
                        ).addOnSuccessListener {
                            potatoRef.set(breakfast1!!).addOnFailureListener {
                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                            }
                            chickenBreastRef.set(breakfast2!!).addOnFailureListener {
                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                            }
                            broccoliRef.set(breakfast3!!).addOnFailureListener {
                                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                            }
                            pd.sendMealID(mealID)
                            Toast.makeText(context, "Meal 3 (Dinner) added", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }
    override fun onItemClick(position: Int) {
        val clickedItem = list[position]

        val intent = Intent(activity, FoodDetail::class.java)
        intent.putExtra("foodID", clickedItem.foodID)
        requireActivity().startActivity(intent)
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