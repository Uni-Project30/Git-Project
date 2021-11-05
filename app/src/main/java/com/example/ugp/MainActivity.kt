package com.example.ugp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.create_new_board.*
class MainActivity : AppCompatActivity() {

    // variables for side navigation
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawer: DrawerLayout
    private lateinit var side_nav: NavigationView
    lateinit var toolbar: Toolbar
    val db = Firebase.firestore

    //variables for sign Out
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val mAuth = FirebaseAuth.getInstance()

    //variables for adapter
    lateinit var myAdapter: BoardsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var listOfBoards : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get boards List in Recycler View
        linearLayoutManager = LinearLayoutManager(applicationContext)
        rv_boards.layoutManager = linearLayoutManager

        listOfBoards = arrayListOf()

        db.collection("boards")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    listOfBoards.add(document.getString("board name")!!)
                }
                myAdapter = BoardsAdapter(this, listOfBoards)
                rv_boards.adapter = myAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"Failed to retrieve boards data",Toast.LENGTH_SHORT).show()
            }

        //assigning variables of side nav
        side_nav = findViewById(R.id.side_nav1)
        drawer = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.app_bar)


        val header = side_nav.getHeaderView(0)
        //variables for assigning image,name and emailid
        var image = header.findViewById<ImageView>(R.id.nav_image)
        var name = header.findViewById<TextView>(R.id.nav_name)
        var email = header.findViewById<TextView>(R.id.nav_email)


        //setting action bar for side navigation
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        // assigning values to information variables

//        val n= mAuth.currentUser!!.displayName
        if(mAuth.currentUser!!.displayName.isNullOrEmpty())
        {
            db.collection("users").document(mAuth.currentUser!!.uid)
                .get()
                .addOnSuccessListener {
                    name.text = it.getString("name")
                }
        }
        else
        {
            name.text = mAuth.currentUser!!.displayName
        }
        email.text = mAuth.currentUser!!.email.toString()
        if (mAuth.currentUser!!.photoUrl != null) {
            val url = mAuth.currentUser!!.photoUrl
            Glide.with(this)
                .load(url)
                .into(image)
        }

        //setting onClick for side nav options

        side_nav.setNavigationItemSelectedListener {
            drawer.closeDrawer(GravityCompat.START)
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
                        signOut()
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
                    builder.setNeutralButton("Cancel") { dialog, which ->
                        Toast.makeText(this, "Thank you for staying", Toast.LENGTH_SHORT).show()
                    }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                }
            }
            true
        }

        //Board Creating Button
        btn_create_board.setOnClickListener{
            showCreateBoardDialog()
        }
    }

    // for opening side_nav
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    // function for signing out
    private fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        Toast.makeText(this, "You have been successfully signed out", Toast.LENGTH_SHORT).show()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient.signOut()

        Firebase.auth.signOut()
    }

    private fun showCreateBoardDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.create_new_board,null)
        val txt = dialogLayout.findViewById<EditText>(R.id.et_board_name)
        val currentUser = mAuth.currentUser


        with(builder){
            setTitle("Create Board")
            setPositiveButton("Create"){dialog, which ->

                val board = hashMapOf(
                    "board name" to txt.text.toString(),
                    "created by" to currentUser?.uid
                )

                db.collection("boards")
                    .document(txt.text.toString())
                    .set(board, SetOptions.merge())
                    .addOnSuccessListener {
                        val intent = Intent(this@MainActivity, BoardActivity::class.java)
                        intent.putExtra("boardName",txt.text.toString())
                        startActivity(intent)
                        finish()
                        Log.d("data in firestore" , "true")
                    }
                    .addOnFailureListener {
                        Log.d("data in firestore",it.message.toString() )
                    }
            }
            setNegativeButton("Cancel"){dialog, which ->

            }
            setView(dialogLayout)
            show()
        }
    }
}