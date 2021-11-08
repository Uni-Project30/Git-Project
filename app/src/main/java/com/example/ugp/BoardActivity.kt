package com.example.ugp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.SubMenu
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class BoardActivity : AppCompatActivity() {

    private lateinit var dualDrawerToggle : DualDrawerToggle
    private lateinit var drawerBoard: DrawerLayout
    private lateinit var rightNavBoard: NavigationView
    private lateinit var toolbarBoard: Toolbar
    private lateinit var leftNavBoard: NavigationView

    private val mAuth = Firebase.auth
    private val db = Firebase.firestore

    private lateinit var menu : Menu
    private lateinit var subMenu : SubMenu

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
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_more_vert_24,
            this.theme)
        dualDrawerToggle = drawable?.let {
            DualDrawerToggle(this, drawerBoard, toolbarBoard, it, R.string.open,
                R.string.close, R.string.open, R.string.close)
        }!!
        drawerBoard.addDrawerListener(dualDrawerToggle)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Adding the member list
//        val menu : Menu = rightNavBoard.menu
//        val subMenu = menu.findItem(R.id.members).subMenu
//        Log.e("Member", subMenu.toString())
//        db.collection("users").get()
//            .addOnSuccessListener { querySnapshot ->
//                querySnapshot.documentChanges.forEach { documentChange ->
//                    subMenu.add(documentChange.document.get("name").toString())
//                }
//            }.addOnFailureListener { exception ->
//                Log.e("Member List", exception.message.toString())
//            }


        val header = leftNavBoard.getHeaderView(0)
        //variables for assigning image,name and emailId
        val image = header.findViewById<ImageView>(R.id.nav_image)
        val name = header.findViewById<TextView>(R.id.nav_name)
        val email = header.findViewById<TextView>(R.id.nav_email)

        // assigning values to information variables
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
                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                }
            }
            true
        }

        // Setting onClick for right nav
        rightNavBoard.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {

                R.id.members -> {
                    menu = rightNavBoard.menu
                    menu.clear()
                    rightNavBoard.inflateMenu(R.menu.member_menu)

                    var count = 0
                    val rightMemberHeader = arrayListOf<View>()
                    val rightHeader : View  = rightNavBoard.inflateHeaderView(R.layout.right_nav_header)
                    rightHeader.elevation = 20.0f
                    val headerBack = rightHeader.findViewById<ImageButton>(R.id.imageButton_right_nav)
                    val headerText = rightHeader.findViewById<TextView>(R.id.right_nav_text)

                    headerText.setText(R.string.board_members)

                    db.collection("users").get()
                        .addOnSuccessListener { querySnapshot ->
                            querySnapshot.documentChanges.forEach { documentChange ->
                                val rightMemberHeaderX = rightNavBoard.inflateHeaderView(R.layout.member_layout)
                                rightMemberHeader.add(rightMemberHeaderX)
                                val memberImageView : CircleImageView = rightMemberHeaderX.findViewById(R.id.member_image)
                                val memberNameText : TextView = rightMemberHeaderX.findViewById(R.id.member_name)

                                if (mAuth.currentUser!!.photoUrl != null) {
                                    val url = mAuth.currentUser!!.photoUrl
                                    Glide.with(this)
                                        .load(url)
                                        .into(memberImageView)
                                }

                                memberNameText.text = documentChange.document.get("name").toString()

                                count ++

                            }
                        }.addOnFailureListener { exception ->
                            Log.e("Member List", exception.message.toString())
                        }

                    headerBack.setOnClickListener {
                        rightNavBoard.removeHeaderView(rightHeader)
                        for(i in 0 until count) {
                            rightNavBoard.removeHeaderView(rightMemberHeader[i])
                        }
                        menu.clear()
                        rightNavBoard.inflateMenu(R.menu.board_menu)
                    }
                }

                //R.id.invite_members ->

                // Setting board as favourite
//                R.id.favourite_board -> {
//
//                }

                R.id.about_app -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
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
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        dualDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dualDrawerToggle.onConfigurationChanged(newConfig)
    }

}