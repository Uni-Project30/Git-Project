package com.example.ugp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ugp.databinding.ActivityCardMemberBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CardMemberActivity : AppCompatActivity() {

    private var board_name: String? = ""
    private var card_id: String? = ""
    private var list_name: String? = ""
    private var card_name: String? = ""
    private var list_text: String? = ""


    private lateinit var binding: ActivityCardMemberBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var myAdapter: CardMemberAdapter
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("OnCreate", "reached")
        binding = ActivityCardMemberBinding.inflate(layoutInflater)

        setContentView(binding.root)

        board_name = intent.extras?.getString("board_name")
        card_id = intent.extras?.getString("card_id")
        list_name = intent.extras?.getString("list_name")
        card_name = intent.extras?.getString("card_name")
        list_text = intent.extras?.getString("list_text")

        val firebaseDetails = mapOf(
            "board_name" to board_name,
            "card_id" to card_id,
            "list_name" to list_name,
        )


        val memberList = arrayListOf<String>()
        val doc_idList = arrayListOf<String>()
        val photo_urlList = arrayListOf<String?>()


        linearLayoutManager = LinearLayoutManager(this)
        binding.rvCardMembers.layoutManager = linearLayoutManager

        db.collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->

                querySnapshot.forEach {

                    memberList.add(it["name"].toString())
                    doc_idList.add(it["doc_id"].toString())
                    photo_urlList.add(it["photo_url"].toString())
                }


                myAdapter =
                    CardMemberAdapter(memberList, doc_idList, photo_urlList, firebaseDetails, this)
                binding.rvCardMembers.adapter = myAdapter

            }

    }
}


