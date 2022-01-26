package com.yash1307.photobook.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.yash1307.photobook.R

class ProfileActivity : AppCompatActivity() {

    lateinit var userEmailText:TextView
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userEmailText = findViewById(R.id.userEmailText)
        firebaseAuth = FirebaseAuth.getInstance()

        val getUserEmail = firebaseAuth.currentUser!!.email
        userEmailText.text = getUserEmail
    }
}