package com.yash1307.photobook.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.yash1307.photobook.R
import com.yash1307.photobook.models.Details
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CreateAccount : AppCompatActivity() {

    lateinit var signUpEmailET: EditText
    lateinit var signUpPassET: EditText
    lateinit var signUpBtn: Button
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    private lateinit var details: Details



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        signUpEmailET = findViewById(R.id.userSignUpEmailET)
        signUpPassET = findViewById(R.id.userSignupPassET)
        signUpBtn = findViewById(R.id.signUpBtn)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("user_details")




        signUpBtn.setOnClickListener {
            MainScope().launch {
                signUpUser()
            }
        }
    }

    private fun signUpUser() {
        val getUserEmail = signUpEmailET.text.toString()
        val getUserPass = signUpPassET.text.toString()

        details = Details(getUserEmail, getUserPass)

        if (getUserEmail.isEmpty()) {
            signUpEmailET.error = "Please enter email"
            return
        } else if (getUserPass.isEmpty()) {
            signUpPassET.error = "Please enter password"
            return
        } else if (getUserEmail.isEmpty() && getUserPass.isEmpty()) {
            Toast.makeText(this, "Please enter email and password please", Toast.LENGTH_SHORT)
                .show()
        }

        firebaseAuth.createUserWithEmailAndPassword(details.email, details.pass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val userDetails: HashMap<String, Any> = hashMapOf(
                        "email" to details.email,
                        "pass" to details.pass
                    )
                    val newReference = databaseReference.push()

                    newReference.setValue(userDetails).addOnCompleteListener(this) { it ->
                        if (it.isComplete) {
                            Toast.makeText(
                                this,
                                "User account created successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(this, "Error ", Toast.LENGTH_SHORT).show()
                        }
                    }


                    val intent = Intent(this, AllPhotos::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
    }
}