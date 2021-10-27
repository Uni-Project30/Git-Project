package com.example.ugp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class BoardActivity : AppCompatActivity() {

    // Variables for right drawer layout
    private lateinit var toggleBoard : ActionBarDrawerToggle
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
        toggleBoard = ActionBarDrawerToggle(this, drawerBoard, R.string.open, R.string.close)
        drawerBoard.addDrawerListener(toggleBoard)
        toggleBoard.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        // Setting onClick for right nav
        rightNavBoard.setNavigationItemSelectedListener { menuItem ->
            drawerBoard.closeDrawer(GravityCompat.START)
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
                    finish()
                }

            }
            true
        }

    }

    // Adding members currently in the board
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        val members = menu?.addSubMenu(0, 0, 0, "Members")
        for(member in memberList) {
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