package com.example.healthtracker.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthtracker.R
import com.example.healthtracker.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var toLogin: Button
    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        toLogin = findViewById(R.id.toLogin)
        toLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        setUpFirebase()

        register.setOnClickListener {
            val email = emailTextRegister.text.toString().trim()
            val password = passwordTextRegister.text.toString().trim()
            val confirmPassword = confirmPasswordTextRegister.text.toString().trim()
            if(check(email, password, confirmPassword)){
                authentication.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        try{
                            val currentUser = authentication.currentUser
                            if(currentUser != null){
                                userID = currentUser.uid
                            }
                            val documentRef = firebase.collection("User").document(userID)
                            val user = User(userID, "",  0, "", 0)
                            documentRef.set(user).addOnSuccessListener {
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(this,"Your account has successfully registered", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(this," "+ it.message, Toast.LENGTH_SHORT).show()
                            }
                        }catch (e :Exception){
                            Toast.makeText(this," "+e.message, Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener{
                        Toast.makeText(this," "+it.message, Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    private fun check(email: String, password: String, confirmPassword: String): Boolean{

        if(email.isEmpty()){
            Toast.makeText(this,"Please enter your email", Toast.LENGTH_SHORT).show()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Please enter correct email", Toast.LENGTH_SHORT).show()
        }else if(password.isEmpty()){
            Toast.makeText(this,"Please enter your password", Toast.LENGTH_SHORT).show()
        }else if(confirmPassword.isEmpty()){
            Toast.makeText(this,"Please enter your confirm password", Toast.LENGTH_SHORT).show()
        }

        if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            if(password == confirmPassword){
                return true
            }else{
                Toast.makeText(this,"The password and confirm password must be same ", Toast.LENGTH_SHORT).show()
            }
            return false
        }
        return false
    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
    }
}