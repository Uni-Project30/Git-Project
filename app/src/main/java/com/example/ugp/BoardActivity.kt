package com.example.ugp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class BoardActivity : AppCompatActivity() {

    // Variables for right drawer layout
    private lateinit var toggleBoard : EndDrawerToggle
    private lateinit var drawerBoard : DrawerLayout
    private lateinit var rightNavBoard : NavigationView
    private lateinit var toolbarBoard : Toolbar

    private var memberList : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        // Assigning variables of right nav
        drawerBoard = findViewById(R.id.board_drawer_layout)
        rightNavBoard = findViewById(R.id.board_right_nav)
        toolbarBoard = findViewById(R.id.toolbar_board)


        // Setting action bar for right nav
        setSupportActionBar(toolbarBoard)
        toggleBoard = EndDrawerToggle(drawerBoard, toolbarBoard, R.string.open, R.string.close)
        drawerBoard.addDrawerListener(toggleBoard)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        // Setting onClick for right nav
        rightNavBoard.setNavigationItemSelectedListener { menuItem ->
            drawerBoard.closeDrawer(GravityCompat.END)
            when(menuItem.itemId) {
                // For Later
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
        toolbarBoard.title=title



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
        toggleBoard.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggleBoard.onConfigurationChanged(newConfig)
    }

}