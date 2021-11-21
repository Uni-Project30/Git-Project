package com.example.ugp

import android.content.ContentValues
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ugp.loginFeatures.LoginActivity
import com.example.ugp.toggle.DualDrawerToggle
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_board.*


class BoardActivity : AppCompatActivity() {

    private var c_list:ArrayList<data_boards_list_card> = ArrayList()
    private lateinit var dualDrawerToggle: DualDrawerToggle
    private lateinit var drawerBoard: DrawerLayout
    private lateinit var rightNavBoard: NavigationView
    private lateinit var toolbarBoard: Toolbar
    private lateinit var leftNavBoard: NavigationView


    private val mAuth = Firebase.auth
    private val db = Firebase.firestore

    private lateinit var menu : Menu
    private val b_list: ArrayList<data_board_lists> = ArrayList()
    private lateinit var rv:RecyclerView
    private lateinit var rv_l:RecyclerView

    private var favouriteBoard = intent?.extras?.getString("favourite")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)


        rv = findViewById(R.id.boards_list_rv)
        rv.apply {
            layoutManager = LinearLayoutManager(this@BoardActivity,LinearLayoutManager.HORIZONTAL,false)
        }


        // getting names of all the boards and saving in the arraylist
        db.collection("boards").document(intent.extras?.getString("boardName").toString()).collection("lists")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, error.message.toString())
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        b_list.add(dc.document.toObject(data_board_lists::class.java))
                    }
                    rv.adapter = Adapter_board_lists(b_list,this)
                    rv.adapter!!.notifyDataSetChanged()

                    val sf = getSharedPreferences("s1", MODE_PRIVATE)
                    val set = sf.edit()
                    val s = sf.getInt("list",0)
                    if(s==1)
                    {
                        rv.scrollToPosition(b_list.size-1)
                        set.apply {
                            putInt("list",0)
                        }.apply()
                    }
                }
            }


        // Assigning variables of right nav
        drawerBoard = findViewById(R.id.board_drawer_layout)
        rightNavBoard = findViewById(R.id.board_right_nav)
        toolbarBoard = findViewById(R.id.toolbar_board)

        // Assigning variables of left nav
        leftNavBoard = findViewById(R.id.board_left_side_nav)

        // Setting action bar for right nav
        setSupportActionBar(toolbarBoard)
        val drawable = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_baseline_more_vert_24,
            this.theme
        )
        dualDrawerToggle = drawable?.let {
            DualDrawerToggle(this, drawerBoard, toolbarBoard, it, R.string.open,
                R.string.close, R.string.open, R.string.close)
        }!!
        drawerBoard.addDrawerListener(dualDrawerToggle)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


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
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                R.id.starred_boards -> {
                    // this will take to star board activity
                    val intent = Intent(this, StarredBoardActivity::class.java)
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
                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                }
            }
            true
        }

        // Inflating the menu and setting the star board option
        menu = rightNavBoard.menu
        db.collection("boards")
            .document(intent.extras?.getString("boardName").toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot["favourite"] == "true") {
                    favouriteBoard = "true"
                    menu.findItem(R.id.star_board).isVisible = false
                    menu.findItem(R.id.remove_star_board).isVisible = true
                }
                else {
                    favouriteBoard = "false"
                    menu.findItem(R.id.remove_star_board).isVisible = false
                    menu.findItem(R.id.star_board).isVisible = true
                }
            }

        // Setting onClick for right nav
        rightNavBoard.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {

                R.id.about_board -> {
                    showAboutBoard()
                }

                R.id.members -> {
                    showMembers()
                }

                R.id.invite_members -> {
                    drawerBoard.closeDrawer(GravityCompat.END)
                }

                // Setting board as favourite
                R.id.star_board -> {
                    favouriteBoard = "true"
                    db.collection("boards")
                        .document(intent.extras?.getString("boardName").toString())
                        .update("favourite", favouriteBoard)

                    menu = rightNavBoard.menu
                    menu.findItem(R.id.star_board).isVisible = false
                    menu.findItem(R.id.remove_star_board).isVisible = true
                    drawerBoard.closeDrawer(GravityCompat.END)
                }

                R.id.remove_star_board -> {
                    favouriteBoard = "false"
                    db.collection("boards")
                        .document(intent.extras?.getString("boardName").toString())
                        .update("favourite", favouriteBoard)

                    menu = rightNavBoard.menu
                    menu.findItem(R.id.remove_star_board).isVisible = false
                    menu.findItem(R.id.star_board).isVisible = true
                    drawerBoard.closeDrawer(GravityCompat.END)
                }

            }
            true
        }

        //added board name to appbar title
        val title = intent.extras?.getString("boardName")
        toolbarBoard.title = title

        //
        btn_create_list.setOnClickListener{
            showCreateBoardDialog()
        }
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

    private fun showCreateBoardDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.create_new_board,null)
        val listName = dialogLayout.findViewById<EditText>(R.id.et_board_name)
        val boardName = intent.extras?.getString("boardName")

        with(builder){
            setTitle("Add List")
            setPositiveButton("Add List"){ _, _ ->

                db.collection("boards")
                    .document(boardName.toString())
                    .collection("lists")
                    .document()
                    .get()
                    .addOnSuccessListener {
                        val docName = it.id
                        val list = hashMapOf(
                            "name" to listName.text.toString(),
                            "doc_name" to docName,
                            "board_name" to boardName
//                            listName.text.toString() to docName,
                        )

                        db.collection("boards")
                            .document(boardName.toString())
                            .collection("lists")
                            .document(docName)
                            .set(list, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d("data in Firestore" , "true")
                            }
                            .addOnFailureListener {
                                Log.d("data in Firestore",it.message.toString() )
                            }
                        val sf = getSharedPreferences("s1", MODE_PRIVATE)
                        val set = sf.edit()
                        set.apply {
                            putInt("list",1)
                        }.apply()
                    }
                    .addOnFailureListener {

                    }
            }
            setNegativeButton("Cancel"){ _, _ ->

            }
            setView(dialogLayout)
            show()
        }
    }

    private fun showMembers() {

        menu = rightNavBoard.menu
        menu.clear()

        // Setting the header views
        var count = 0
        val rightMemberHeader = arrayListOf<View>()
        val rightHeader : View  = rightNavBoard.inflateHeaderView(R.layout.right_nav_header)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rightHeader.elevation = 100.0f
        }
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

        // Task to be performed when back button is clicked
        headerBack.setOnClickListener {
            rightNavBoard.removeHeaderView(rightHeader)
            for(i in 0 until count) {
                rightNavBoard.removeHeaderView(rightMemberHeader[i])
            }
            menu.clear()
            rightNavBoard.inflateMenu(R.menu.board_menu)
            if(favouriteBoard == "true") {
                menu.findItem(R.id.star_board).isVisible = false
                menu.findItem(R.id.remove_star_board).isVisible = true
            }
        }
    }

    private fun showAboutBoard() {

        menu = rightNavBoard.menu
        menu.clear()

        // Setting the header view
        val rightHeader : View  = rightNavBoard.inflateHeaderView(R.layout.right_nav_header)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rightHeader.elevation = 100.0f
        }
        val headerBack = rightHeader.findViewById<ImageButton>(R.id.imageButton_right_nav)
        val headerText = rightHeader.findViewById<TextView>(R.id.right_nav_text)

        headerText.setText(R.string.about_board)

        // Setting the about header
        val aboutHeader : View = rightNavBoard.inflateHeaderView(R.layout.about_board_layout)
        val aboutImageView : CircleImageView = aboutHeader.findViewById(R.id.about_member_image)
        val aboutNameText : TextView = aboutHeader.findViewById(R.id.about_member_name)
        val aboutEditDescription : EditText = aboutHeader.findViewById(R.id.about_edit_description)
        val aboutDescription : TextView = aboutHeader.findViewById(R.id.about_description)
        val saveDescription : Button = aboutHeader.findViewById(R.id.save_description)

        db.collection("boards")
            .document(intent.extras?.getString("boardName").toString()).get()
            .addOnSuccessListener { documentSnapshot ->

                if (mAuth.currentUser!!.photoUrl != null) {
                    val url = mAuth.currentUser!!.photoUrl
                    Glide.with(this)
                        .load(url)
                        .into(aboutImageView)
                }

                aboutNameText.text = documentSnapshot.getString("created by(name)").toString()

                // Toggling the visibilities and taking the board description
                if(documentSnapshot.getString("about") == "") {
                    aboutEditDescription.visibility = View.VISIBLE
                    saveDescription.visibility = View.VISIBLE

                    saveDescription.setOnClickListener {

                        if (aboutEditDescription.text.length <= 10) {
                            aboutEditDescription.error = "Minimum length 10"
                            aboutDescription.requestFocus()
                            return@setOnClickListener
                        }

                        aboutEditDescription.visibility = View.GONE
                        saveDescription.visibility = View.GONE
                        aboutDescription.visibility = View.VISIBLE

                        val description = aboutEditDescription.text.toString()
                        aboutDescription.text = description

                        db.collection("boards")
                            .document(intent.extras?.getString("boardName").toString())
                            .update("about", description)
                    }

                }
                else {
                    aboutDescription.visibility = View.VISIBLE
                    aboutDescription.text = documentSnapshot.getString("about")
                }

            }.addOnFailureListener { exception ->
                Log.e("About Board", exception.message.toString())
            }

        // Task to do when back button is clicked
        headerBack.setOnClickListener {
            rightNavBoard.removeHeaderView(rightHeader)
            rightNavBoard.removeHeaderView(aboutHeader)
            menu.clear()
            rightNavBoard.inflateMenu(R.menu.board_menu)
            if (favouriteBoard == "true") {
                menu.findItem(R.id.star_board).isVisible = false
                menu.findItem(R.id.remove_star_board).isVisible = true
            }
        }
    }
}
