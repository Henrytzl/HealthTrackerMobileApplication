package com.example.healthtracker.healthymeal.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.healthtracker.healthymeal.FragmentFood
import com.example.healthtracker.healthymeal.FragmentNutrition


class SectionsPagerAdapter(fm: FragmentManager, mealID: String): FragmentPagerAdapter(fm) {
    private val mealID = mealID
    override fun getItem(position: Int): Fragment {
        var getFragment :Fragment? = null
        val bundle: Bundle = Bundle()
        when(position){
            0 -> {
                getFragment = FragmentFood()
            }
            1 -> {
                getFragment = FragmentNutrition()
            }
        }
        bundle.putString("mealID", mealID)
        getFragment!!.arguments = bundle

        return  getFragment!!
        //return PlaceholderFragment.newInstance(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title :String? = null
        when(position){
            0 -> title = "Food"
            1 -> title = "Nutrition"
        }

        return title
    }

    override fun getCount(): Int {
        return 2
    }

//    override fun createFragment(position: Int): Fragment {
//        return when(position){
//            0 -> {
//                val mealID = activityBundle.get("mealID").toString()
//                val bundle: Bundle = Bundle()
//                bundle.putString("mealID", mealID)
//                val fragmentFood:FragmentFood = FragmentFood()
//                fragmentFood.arguments = bundle
//                fragmentFood
//            }
//            else -> {
//                FragmentNutrition()
//            }
//        }
//    }
}