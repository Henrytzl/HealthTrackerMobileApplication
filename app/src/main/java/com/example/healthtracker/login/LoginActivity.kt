package com.example.healthtracker.login

import android.os.Bundle
import android.widget.EditText
import com.example.healthtracker.R
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity()  {

    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

}