package com.example.healthtracker.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity()  {

    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        toSignUp.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        forgetPassword.setOnClickListener {
            val resetView = LayoutInflater.from(this).inflate(R.layout.layout_forget_password, null)
            val resetViewBuilder = AlertDialog.Builder(this, R.style.PopUpWindow).setView(resetView).setTitle("Material Type Name")
            //show dialog
            val displayDialog = resetViewBuilder.show()

//            //Cancel
//            resetView.btn_cancel_reset.setOnClickListener{
//                displayDialog.dismiss()
//                Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show()
//            }
//            //send reset email
//            resetView.btn_send_reset_email.setOnClickListener{
//
//            }
        }
    }
}