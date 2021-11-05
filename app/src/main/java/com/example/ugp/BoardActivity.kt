package com.example.ugp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BoardActivity : AppCompatActivity() {

    // Variables for right drawer layout
    private lateinit var toggleBoard: ActionBarDrawerToggle
    private lateinit var drawerBoard: DrawerLayout
    private lateinit var rightNavBoard: NavigationView
    private lateinit var toolbarBoard: Toolbar
    private lateinit var leftNavBoard: NavigationView
    private val mAuth = Firebase.auth
    private val db = Firebase.firestore

    private var memberList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        // Assigning variables of right nav
        drawerBoard = findViewById(R.id.board_drawer_layout)
        rightNavBoard = findViewById(R.id.board_right_nav)
        toolbarBoard = findViewById(R.id.toolbar_board)

        // Assigning variables of left nav
        leftNavBoard = findViewById(R.id.board_left_side_nav)

        // Setting action bar for right nav
        setSupportActionBar(toolbarBoard)
        toggleBoard = ActionBarDrawerToggle(this, drawerBoard, R.string.open, R.string.close)
        drawerBoard.addDrawerListener(toggleBoard)
        toggleBoard.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val header = leftNavBoard.getHeaderView(0)
        //variables for assigning image,name and emailid
        var image = header.findViewById<ImageView>(R.id.nav_image)
        var name = header.findViewById<TextView>(R.id.nav_name)
        var email = header.findViewById<TextView>(R.id.nav_email)

        // assigning values to information variables

//        val n= mAuth.currentUser!!.displayName
        if (mAuth.currentUser!!.displayName.isNullOrEmpty()) {
            db.collection("users").document(mAuth.currentUser!!.uid)
                .get()
                .addOnSuccessListener {
                    name.text = it.getString("name")
                }
        } else {
            name.text = mAuth.currentUser!!.displayName
        }
        email.text = mAuth.currentUser!!.email.toString()
        if (mAuth.currentUser!!.photoUrl != null) {
            val url = mAuth.currentUser!!.photoUrl
            Glide.with(this)
                .load(url)
                .into(image)
        }


        // setting onclick listeners for left navigation
        leftNavBoard.setNavigationItemSelectedListener {
            drawerBoard.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.home -> {
                    // this will take to main activity
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
                R.id.profile -> {
                    // this will take to profile activity
                    val i = Intent(this, ProfileActivity::class.java)
                    startActivity(i)
                }
                R.id.logout -> {
                    // This will show a dialog box foe logging out
                    val builder = this.let { it1 -> AlertDialog.Builder(it1) }
                    builder.setTitle("Exit/Logout")
                    builder.setMessage("Do you really want to exit \n You will be logged out")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        Firebase.auth.signOut()
                        val intent = Intent(
                            this,
                            LoginActivity::class.java
                        )   //Please add the login activity name
                        startActivity(intent)
                        this.finish()
                    }
                    builder.setNegativeButton("No") { dialog, which ->
                        Toast.makeText(this, "Thank you for staying", Toast.LENGTH_SHORT).show()

                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                }
            }
            true
        }

        // Setting onClick for right nav
        rightNavBoard.setNavigationItemSelectedListener { menuItem ->
            drawerBoard.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                // For Later
                //R.id.invite_members ->

                // Setting board as favourite
//                R.id.favourite_board -> {
//
//                }

                R.id.about_app -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            true
        }

        //added board name to appbar title
        val title = intent.extras?.getString("boardName")
        toolbarBoard.title = title


    }

    //Start Main Activity on going Back
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // Adding members currently in the board
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        val members = menu?.addSubMenu(0, 0, 0, "Members")
        for (member in memberList) {
            members?.add(member)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    // For opening right nav
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggleBoard.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}