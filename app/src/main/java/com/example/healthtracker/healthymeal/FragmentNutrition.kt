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
import kotlinx.android.synthetic.main.fragment_nutrition_layout.*

class FragmentNutrition: Fragment() {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String
    private lateinit var mealID : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_nutrition_layout, container, false)

        setUpFirebase()

        return view
    }

    override fun onStart() {
        super.onStart()
        mealID = requireArguments().get("mealID").toString()
        firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { result ->
            nutrition_mealName.text = result.getString("mealName")
        }.addOnFailureListener {
            Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun receiveMealID(data: String?){
        if (data != null) {
            mealID = data
            firebase.collection("Meals").document(userID).collection("Meal Detail").document(mealID).get().addOnSuccessListener { result ->
                nutrition_mealName.text = result.getString("mealName")
            }.addOnFailureListener {
                Toast.makeText(context, " " + it.message, Toast.LENGTH_SHORT).show()
            }
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