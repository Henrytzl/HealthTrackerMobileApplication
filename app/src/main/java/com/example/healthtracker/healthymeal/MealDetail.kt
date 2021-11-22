package com.example.healthtracker.healthymeal

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import com.example.healthtracker.healthymeal.ui.main.SectionsPagerAdapter
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_meal_detail.*

class MealDetail : AppCompatActivity(), PassingDataTabs {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String
    private lateinit var viewPagerAdapter: SectionsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpFirebase()

        val intentMeal = intent.getStringExtra("mealID")
        val mealID : String = intentMeal.toString()

        viewPagerAdapter = SectionsPagerAdapter(supportFragmentManager, mealID)
        view_pager.adapter = viewPagerAdapter
        tabs.setupWithViewPager(view_pager)
        tabs.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#FF6200EE") )
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                val noOfDay = intent.getIntExtra("noOfDay", 0)
                val intentSendBack: Intent = Intent()
                intentSendBack.putExtra("day", "$noOfDay")
                setResult(Activity.RESULT_OK, intentSendBack)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val noOfDay = intent.getIntExtra("noOfDay", 0)
        val intentSendBack: Intent = Intent()
        intentSendBack.putExtra("day", "$noOfDay")
        setResult(Activity.RESULT_OK, intentSendBack)
        finish()
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
    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }

    override fun sendMealID(mealID: String) {
        val tag = "android:switcher:" + R.id.view_pager.toString() + ":" + 1
        val f = supportFragmentManager.findFragmentByTag(tag) as FragmentNutrition?

        f!!.receiveMealID(mealID!!)
    }

//    override fun sendFoodID(foodID: String) {
//        val tag = "android:switcher:" + R.id.view_pager.toString() + ":" + 1
//        val f = supportFragmentManager.findFragmentByTag(tag) as FragmentNutrition?
//
//        f!!.receiveFoodID(foodID!!)
//    }
}