package com.example.healthtracker.scanner

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.healthtracker.R
import com.example.healthtracker.login.LoginActivity
import com.example.healthtracker.scanner.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_scanner.*

class Scanner : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#FF6200EE") )

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.mHome -> {
                    finish()
                }
                R.id.mProfile -> {

                }
                R.id.mFAQ -> {

                }
                R.id.mFeedback -> {

                }
                R.id.mHelp -> {

                }
                R.id.mAboutUs -> {

                }
                R.id.mLogout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            true
        }

        imageHome.setOnClickListener{
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        //return false
        return super.onOptionsItemSelected(item)
    }
}