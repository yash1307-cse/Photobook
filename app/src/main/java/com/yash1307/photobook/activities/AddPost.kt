package com.yash1307.photobook.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yash1307.photobook.R
import com.yash1307.photobook.models.Posts
import java.text.SimpleDateFormat
import java.util.*


class AddPost : AppCompatActivity() {

    lateinit var postBtn: Button
    lateinit var openGalleryForImageView: ImageView
    lateinit var descET: EditText
    private val GALLERY_CODE = 123
    lateinit var imageUri: Uri
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var fireStore: FirebaseFirestore
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var collectionReference: CollectionReference
    lateinit var postDate: String
    lateinit var progressDialog: ProgressDialog

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        postBtn = findViewById(R.id.postBtn)
        openGalleryForImageView = findViewById(R.id.addImageViewBtn)
        descET = findViewById(R.id.descET)
        fireStore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)

        // for current date and time
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        postDate = sdf.format(Date())

        storageReference = firebaseStorage.getReference("All_images")
            .child(firebaseAuth.currentUser!!.email.toString())


        collectionReference = fireStore.collection(firebaseAuth.currentUser!!.email.toString())

        openGalleryForImageView.setOnClickListener {
            openGallery()
        }

        postBtn.setOnClickListener {
            postNewImage()
        }
    }

    private fun postNewImage() {

        val desc = descET.text.toString()
        if (imageUri != null && desc.isEmpty()) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        } else {

            progressDialog.setMessage("Posting")
            progressDialog.show()
            val newImage =
                storageReference.child("all_images").child(imageUri.lastPathSegment!!)

            newImage.putFile(imageUri).addOnSuccessListener{
                Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_SHORT).show()

                newImage.downloadUrl.addOnSuccessListener { downloadImageURI ->
                    val posts = Posts(
                        description = desc,
                        imageUri = downloadImageURI.toString(),
                        postDate = postDate
                    )

                    val addPost = hashMapOf<String, Any>(
                        "description" to posts.description,
                        "imageUri" to posts.imageUri,
                        "postDate" to posts.postDate
                    )
                    Log.d("IMAGE_URI", posts.imageUri)
                    collectionReference.document().set(addPost).addOnCompleteListener {
                        if (it.isComplete) {
                            Log.d("Success", "Data added successfully")
                        } else {
                            Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                progressDialog.dismiss()
                val intent = Intent(this, AllPhotos::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            openGalleryForImageView.setImageURI(imageUri)
        }
    }
}