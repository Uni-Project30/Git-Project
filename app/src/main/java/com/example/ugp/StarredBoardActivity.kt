package com.example.ugp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ugp.loginFeatures.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StarredBoardActivity : AppCompatActivity() {

    // Variables for side navigation
    private lateinit var toggleStar : ActionBarDrawerToggle
    private lateinit var drawerStar : DrawerLayout
    private lateinit var sideNavStar : NavigationView
    private lateinit var toolbarStar : Toolbar

    private val db = Firebase.firestore

    // Variables for signing out
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val mAuth = FirebaseAuth.getInstance()

    // Variables for recyclerView adapter
    private lateinit var recyclerViewStar : RecyclerView
    private lateinit var linearLayoutManagerStar : LinearLayoutManager
    private lateinit var listOfStarBoards : ArrayList<String>
    private lateinit var listOfStarFavourites : ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starred_board)

        //set status bar white
        window.statusBarColor = ContextCompat.getColor(this,R.color.blue1)

        // Assigning variables
        recyclerViewStar = findViewById(R.id.recycler_view_star_boards)
        drawerStar = findViewById(R.id.star_drawer_layout)
        sideNavStar = findViewById(R.id.star_side_nav)
        toolbarStar = findViewById(R.id.star_toolbar)
        toolbarStar.title = "Starred Boards"
        setSupportActionBar(toolbarStar)

        linearLayoutManagerStar = LinearLayoutManager(applicationContext)
        recyclerViewStar.layoutManager = linearLayoutManagerStar

        listOfStarBoards = arrayListOf()
        listOfStarFavourites = arrayListOf()

        db.collection("boards").get()
            .addOnSuccessListener { querySnapshot ->

                for(document in querySnapshot) {
                    if(document["favourite"] == "true") {

                        listOfStarBoards.add(document.getString("board name")!!)
                        listOfStarFavourites.add(document.getString("favourite")!!)
                    }

                    recyclerViewStar.adapter = BoardsAdapter(this, listOfStarBoards,
                        listOfStarFavourites)
                }
            }.addOnFailureListener { exception ->
                Log.e("Star Board Activity", exception.message.toString())
            }

        // Setting the toggle action
        toggleStar = ActionBarDrawerToggle(this, drawerStar, R.string.open, R.string.close)
        drawerStar.addDrawerListener(toggleStar)
        toggleStar.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Operations for side nav header view
        val headerStar = sideNavStar.getHeaderView(0)
        val image = headerStar.findViewById<ImageView>(R.id.nav_image)
        val name = headerStar.findViewById<TextView>(R.id.nav_name)
        val email = headerStar.findViewById<TextView>(R.id.nav_email)

        if(mAuth.currentUser!!.displayName.isNullOrEmpty()) {
            db.collection("users").document(mAuth.currentUser!!.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    name.text = documentSnapshot.getString("name")
                }.addOnFailureListener { exception ->
                    Log.e("Header View Star", exception.message.toString())
                }
        }
        else {
            name.text = mAuth.currentUser!!.displayName
        }

        email.text = mAuth.currentUser!!.email.toString()
        if (mAuth.currentUser!!.photoUrl != null) {
            val url = mAuth.currentUser!!.photoUrl
            Glide.with(this)
                .load(url)
                .into(image)
        }

        // Making Starred Board option as invisible
        val menu = sideNavStar.menu
        menu.findItem(R.id.starred_boards).isVisible = false

        // Setting onClick for side nav
        sideNavStar.setNavigationItemSelectedListener { menuItem ->
            drawerStar.closeDrawer(GravityCompat.START)
            when(menuItem.itemId) {

                R.id.home -> {
                    // this will take to main activity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                R.id.profile -> {
                    // this will take to profile activity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                R.id.logout -> {
                    // This will show a dialog box foe logging out
                    val builder = this.let { it1 -> AlertDialog.Builder(it1) }
                    builder.setTitle("Exit/Logout")
                    builder.setMessage("Do you really want to exit \n You will be logged out")
                    builder.setPositiveButton("Yes") { _, _ ->
                        Firebase.auth.signOut()
                        val intent = Intent(
                            this,
                            LoginActivity::class.java
                        )   //Please add the login activity name
                        startActivity(intent)
                        this.finish()
                    }
                    builder.setNegativeButton("No") { _, _ ->
                        Toast.makeText(this, "Thank you for staying", Toast.LENGTH_SHORT).show()

                    }
                    builder.setNeutralButton("Cancel") { _, _ ->
                        Toast.makeText(this, "Thank you for staying", Toast.LENGTH_SHORT).show()
                    }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                }

            }
            true
        }
    }

    // for opening side_nav
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggleStar.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}