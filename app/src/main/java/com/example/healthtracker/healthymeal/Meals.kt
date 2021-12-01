package com.example.healthtracker.healthymeal

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import com.example.healthtracker.dataclass.MealDetailDC
import com.example.healthtracker.healthymeal.MealRecyclerView.RecycleViewMeal
import com.example.healthtracker.healthymeal.MealRecyclerView.RecycleViewMealAdapter
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_meals.*
import kotlinx.android.synthetic.main.layout_create_meal.view.*

class Meals : AppCompatActivity(), RecycleViewMealAdapter.OnItemClickListener, RecycleViewMealAdapter.OnDeleteClickListener {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String
    private var noOfDay: Int = 0
    //Recycle View
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<RecycleViewMeal>
    private lateinit var adapter: RecycleViewMealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meals)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Meals"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intentDay = intent.getStringExtra("day")
        if(!intentDay.isNullOrEmpty()){
            noOfDay = intentDay.toInt()!!
        }
        setUpFirebase()

        //Recycle View
        recyclerView = recyclerViewMeals
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.setHasFixedSize(true)
        list = arrayListOf()

        adapter = RecycleViewMealAdapter(list, this, this)
        recyclerView.adapter = adapter
        noDataTxt.visibility = View.GONE
        firebase.collection("Meals/$userID/Meal Detail").whereEqualTo("noOfDay", intent.getStringExtra("day")!!.toInt())
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FireStore Error", error.message.toString())
                }else {
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            list.add(dc.document.toObject(RecycleViewMeal::class.java))
                        }
                    }
                    if(list.isEmpty()){
                        noDataTxt.visibility = View.VISIBLE
                    }
                    adapter.notifyDataSetChanged()
                }
            }

        //Add Meal
        addMeal.setOnClickListener {
            if(noOfDay == 0){
                Toast.makeText(this, "Error Occurred", Toast.LENGTH_LONG).show()
            }else {
                val createMealView = LayoutInflater.from(this).inflate(R.layout.layout_create_meal, null)
                val createMealViewBuilder = AlertDialog.Builder(this, R.style.PopUpWindow).setView(createMealView).setTitle("Create a meal").setIcon(R.drawable.ic_meal1)
                //show dialog
                val displayDialog = createMealViewBuilder.show()

                //Cancel
                createMealView.btn_cancel.setOnClickListener {
                    displayDialog.dismiss()
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                }

                createMealView.btn_create.setOnClickListener {
                    val mealName = createMealView.mealName.text.toString().trim()
                    if (mealName.isNotEmpty()) {
                        val documentRef = firebase.collection("Meals").document(userID).collection("Meal Detail").document()
                        val mealDetailData = MealDetailDC(mealName, 0, 0, 0, 0, 0, noOfDay, documentRef.id)
                        documentRef.set(mealDetailData).addOnSuccessListener {
                            displayDialog.dismiss()
                            Toast.makeText(this, "Successfully created a meal", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MealDetail::class.java)
                            intent.putExtra("mealID", documentRef.id)
                            intent.putExtra("noOfDay", noOfDay)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Get current User id and email
        if (authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            finishAffinity()
        }
        if(list.isNotEmpty()){
            noDataTxt.visibility = View.GONE
        }
    }

    override fun onItemClick(position: Int) {
        val clickedItem = list[position]
        val intent = Intent(this, MealDetail::class.java)
        intent.putExtra("mealID", clickedItem.mealID)
        startActivity(intent)
    }

    override fun onItemDelete(position: Int) {
        val clickedItem = list[position]
        val deleteViewBuilder = AlertDialog.Builder(this).setTitle("Delete Meal")
            .setIcon(R.drawable.ic_delete2).setMessage("Are you sure to delete this meal?")
            .setCancelable(false).setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }).setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                firebase.collection("Meals").document(userID).collection("Meal Detail").document(clickedItem.mealID).delete().addOnSuccessListener {
                    dialog.dismiss()
                    Toast.makeText(this, "Meal deleted successfully", Toast.LENGTH_SHORT).show()
                    list.removeAt(position)
                    adapter.notifyDataSetChanged()
                    if(list.isEmpty()){
                        noDataTxt.visibility = View.VISIBLE
                    }
                }.addOnFailureListener {
                    Toast.makeText(this," " + it.message, Toast.LENGTH_LONG).show()
                }
            })
        //show dialog
        val displayDialog = deleteViewBuilder.create()
        displayDialog.show()
    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}