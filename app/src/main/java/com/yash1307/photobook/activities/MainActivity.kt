package com.yash1307.photobook.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.yash1307.photobook.R
import com.yash1307.photobook.models.Details
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var userEmailET: EditText
    lateinit var userPassET: EditText
    lateinit var login: Button
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var signUpText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userEmailET = findViewById(R.id.userEmailET)
        userPassET = findViewById(R.id.userPassET)
        login = findViewById(R.id.loginBtn)
        firebaseAuth = FirebaseAuth.getInstance()
        signUpText = findViewById(R.id.signUpText)

        when (firebaseAuth.currentUser != null) {
            true -> {
                val intent = Intent(this, AllPhotos::class.java)
                startActivity(intent)
                finish()
            }
            false -> {
                Toast.makeText(this, "Please log in or create account", Toast.LENGTH_SHORT).show()
                /* val intent = Intent(this, CreateAccount::class.java)
                 startActivity(intent)
                 finish()*/
            }
        }

        login.setOnClickListener {
            MainScope().launch {
                userLogin()
            }
        }

        signUpText.setOnClickListener {
            val intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
        }
    }

    private fun userLogin() {

        val getEmail = userEmailET.text.toString()
        val getPass = userPassET.text.toString()

        val details = Details(email = getEmail, pass = getPass)

        if (getEmail.isEmpty()) {
            userEmailET.error = "Please enter email"
        } else if (getPass.isEmpty()) {
            userPassET.error = "Please enter password"
            return
        } else if (getEmail.isEmpty() && getPass.isEmpty()) {
            userEmailET.error = "Please enter email"
            userPassET.error = "Please enter password"
            return
        }

        firebaseAuth.signInWithEmailAndPassword(details.email, details.pass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val intent = Intent(this, AllPhotos::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
