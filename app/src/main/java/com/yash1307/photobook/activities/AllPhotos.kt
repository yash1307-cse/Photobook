package com.yash1307.photobook.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.yash1307.photobook.R
import com.yash1307.photobook.adapter.PhotoAdapter
import com.yash1307.photobook.models.Posts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AllPhotos : AppCompatActivity() {

    lateinit var appBar: MaterialToolbar
    lateinit var appBarLayout: AppBarLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var addPostBtn: FloatingActionButton
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var navView: NavigationView
    lateinit var recyclerView: RecyclerView
    lateinit var firestore: FirebaseFirestore
    lateinit var collectionReference: CollectionReference
    lateinit var postsList: ArrayList<Posts>
    lateinit var photoAdapter: PhotoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_photos)

        firebaseAuth = FirebaseAuth.getInstance()
        appBar = findViewById(R.id.topAppBar)
        appBarLayout = findViewById(R.id.appBarLayout)
        drawerLayout = findViewById(R.id.drawerLayout)
        addPostBtn = findViewById(R.id.openAddPostScreen)
        navView = findViewById(R.id.navView)
        recyclerView = findViewById(R.id.recyclerView)
        firestore = FirebaseFirestore.getInstance()
        collectionReference = firestore.collection(firebaseAuth.currentUser!!.email.toString())
        postsList = arrayListOf()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        setUpDrawerLayout()

        addPostBtn.setOnClickListener {
            val intent = Intent(this, AddPost::class.java)
            startActivity(intent)
        }

        displayPosts()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> {
                    drawerLayout.closeDrawers()
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.signOut -> {

                    firebaseAuth.signOut()
                    drawerLayout.closeDrawers()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "User signed out", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        navView.menu.getItem(0).isChecked = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun displayPosts() {

        /*collectionReference.get(Source.SERVER).addOnCompleteListener {
          val querySnapshot =   it.result
            for (listDocument in querySnapshot!!.documentChanges) {
                if(listDocument.type == DocumentChange.Type.ADDED) {
                    postsList.add(listDocument.document.toObject(Posts::class.java))
                    Log.d("ALL_POSTS", listDocument.document.toObject(Posts::class.java).toString())
                }
            }
            photoAdapter = PhotoAdapter(this, postsList)
            recyclerView.adapter = photoAdapter
            photoAdapter.notifyDataSetChanged()
        }*/
        collectionReference.addSnapshotListener { value, error ->

            when (error != null) {
                true -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
                false -> {
                    for (listDocument in value!!.documentChanges) {
                        if (listDocument.type == DocumentChange.Type.ADDED) {
                            postsList.add(listDocument.document.toObject(Posts::class.java))
                            Log.d(
                                "ALL_POSTS",
                                listDocument.document.toObject(Posts::class.java).toString()
                            )
                        }
                    }
                    photoAdapter = PhotoAdapter(this, postsList)
                    recyclerView.adapter = photoAdapter
                    photoAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun setUpDrawerLayout() {
        setSupportActionBar(appBar)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}



