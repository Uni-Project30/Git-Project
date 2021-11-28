package com.example.ugp

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ugp.databinding.ActivityCardMemberBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CardMemberActivity : AppCompatActivity() {

    private lateinit var binding :ActivityCardMemberBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var myAdapter: CardMemberAdapter
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardMemberBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val memberList = ArrayList<String>()

        linearLayoutManager = LinearLayoutManager(this)
        binding.rvCardMembers.layoutManager = linearLayoutManager

        db.collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->

                querySnapshot.forEach {

                    memberList.add(it["name"].toString())
                }
                myAdapter = CardMemberAdapter(memberList, this)
                binding.rvCardMembers.adapter = myAdapter

            }

    }
}

