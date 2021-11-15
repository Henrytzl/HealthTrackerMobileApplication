package com.example.healthtracker.healthymeal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthtracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_food_layout.view.*

class FragmentFood: Fragment() {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_food_layout, container, false)

        setUpFirebase()

        val mealID = requireArguments().get("MealID").toString()
        Toast.makeText(context, " $mealID", Toast.LENGTH_SHORT).show()
        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { result->
            view.txtMealName.setText(result.getString("mealName"))
        }.addOnFailureListener {
            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
        }

        view.btn_edit_mealName.setOnClickListener {
            val txtMealName = view.txtMealName.text.toString()
            if(mealNameValidation(txtMealName)){
                firebase.collection("Meals").document(userID).collection("Meal Detail")
            }
        }
        return view
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