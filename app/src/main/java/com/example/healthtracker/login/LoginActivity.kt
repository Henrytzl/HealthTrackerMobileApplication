package com.example.healthtracker.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.HealthCalculator
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_forget_password.view.*

class LoginActivity : AppCompatActivity() {

    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        toSignUp.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        setUpFirebase()

        login.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()
            if(loginValidation(email, password)){
                authentication.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                            Toast.makeText(this,"Welcome To Health Tracker", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }.addOnFailureListener{
                            Toast.makeText(this," "+it.message, Toast.LENGTH_SHORT).show()
                        }
            }
        }

        forgetPassword.setOnClickListener {
            val resetView = LayoutInflater.from(this).inflate(R.layout.layout_forget_password, null)
            val resetViewBuilder = AlertDialog.Builder(this, R.style.PopUpWindow).setView(resetView).setTitle("Reset Password").setIcon(R.drawable.ic_key)
            //show dialog
            val displayDialog = resetViewBuilder.show()

            //Cancel
            resetView.btn_cancel_reset.setOnClickListener{
                displayDialog.dismiss()
                Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show()
            }
            //send reset email
            resetView.btn_send_reset_email.setOnClickListener{
                val resetEmail = resetView.resetPEmailText.text.toString().trim()
                if(resetEmailValidation(resetEmail)){
                    if(authentication.sendPasswordResetEmail(resetEmail).isSuccessful){
                        displayDialog.dismiss()
                        Toast.makeText(this,"The reset password link has been sent to your email", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,"Error occurred", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun loginValidation(email: String, password: String): Boolean{
        if(email.isEmpty()){ //if email is empty then false
            Toast.makeText(this,"Please enter an email", Toast.LENGTH_SHORT).show()
            return false
        }else if(password.isEmpty()){ //if password is empty then false
            Toast.makeText(this,"Please enter a password", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //if email is not a valid email then false
            Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(password.length <= 5) { //if password is less than 6 characters then false
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun resetEmailValidation(email: String): Boolean{
        if(email.isEmpty()){
            Toast.makeText(this,"Please enter an email", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
    }
}