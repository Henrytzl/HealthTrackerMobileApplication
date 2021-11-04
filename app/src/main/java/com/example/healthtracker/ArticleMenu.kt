package com.example.healthtracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class ArticleMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_menu)

        val cardView1 = findViewById<CardView>(R.id.cardView1)
        val cardView2 = findViewById<CardView>(R.id.cardView2)
        val cardView3 = findViewById<CardView>(R.id.cardView3)
        val cardView4 = findViewById<CardView>(R.id.cardView4)
        val cardView5 = findViewById<CardView>(R.id.cardView5)
        val cardView6 = findViewById<CardView>(R.id.cardView6)

        cardView1.setOnClickListener() {
            startActivity(Intent(this, Article1::class.java))
        }

        cardView2.setOnClickListener() {
            startActivity(Intent(this, Article2::class.java))
        }

        cardView3.setOnClickListener() {
            startActivity(Intent(this, Article3::class.java))
        }

        cardView4.setOnClickListener() {
            startActivity(Intent(this, Article4::class.java))
        }

        cardView5.setOnClickListener() {
            startActivity(Intent(this, Article5::class.java))
        }

        cardView6.setOnClickListener() {
            startActivity(Intent(this, Article6::class.java))
        }
    }

}