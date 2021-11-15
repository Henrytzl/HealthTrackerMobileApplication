package com.example.healthtracker.healthymeal.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.healthtracker.healthymeal.FragmentFood
import com.example.healthtracker.healthymeal.FragmentNutrition


class SectionsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, bundle: Bundle)
    : FragmentStateAdapter(fm, lifecycle) {

    private val activityBundle = bundle

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                val mealID = activityBundle.get("mealID").toString()
                val bundle: Bundle = Bundle()
                bundle.putString("mealID", mealID)
                val fragmentFood:FragmentFood = FragmentFood()
                fragmentFood.arguments = bundle
                fragmentFood
            }
            else -> {
                FragmentNutrition()
            }
        }
    }

//    override fun getItem(position: Int): Fragment {
//        return when(position){
//            0 -> FragmentFood()
//            else -> FragmentNutrition()
//        }
//        //return PlaceholderFragment.newInstance(position + 1)
//    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
//        return context.resources.getString(TAB_TITLES[position])
//    }
//
//    override fun getCount(): Int {
//        // Show 2 total pages.
//        return 2
//    }
}