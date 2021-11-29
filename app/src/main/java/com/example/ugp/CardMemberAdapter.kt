package com.example.ugp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CardMemberAdapter(
    private val memberNameList: ArrayList<String>,
    private val doc_idList: ArrayList<String>,
    private val photo_urlList: ArrayList<String?>,
    private val firebaseDetails: Map<String, String?>,
    val context: Context
) :RecyclerView.Adapter<CardMemberAdapter.ViewHolder>() {



    private val mAuth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardMemberAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)


        val v = inflater.inflate(R.layout.card_card_member,parent,false)
        Log.d("card","reached")


        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardMemberAdapter.ViewHolder, position: Int) {

        holder.name.text = memberNameList[position]
        val user_id = doc_idList[position]

        val board_name = firebaseDetails["board_name"]
        val card_id  = firebaseDetails["card_id"]
        var list_name = firebaseDetails["list_name"]
        var member_idList = arrayListOf<String>()


        val pic_url = photo_urlList[position].toString()

        if (pic_url.isEmpty()){

            Log.d("member pic",pic_url)

            holder.memberImage.setImageResource(R.drawable.icon_profile)

        }else{
            Glide.with(context)
                .load(pic_url)
                .into(holder.memberImage)
            Log.d("member pic not null",pic_url)
        }


        db.collection("boards")
            .document(board_name.toString())
            .collection("lists")
            .document(list_name.toString())
            .collection("cards")
            .document(card_id.toString())
            .get()
            .addOnSuccessListener{

            //    member_idList.addAll((it["members"]) as ArrayList<String>)

                member_idList = it["members"] as ArrayList<String>
                val a = it["members"]
                Log.d("firebase",a.toString())
                Log.d("members",member_idList.toString())

                if (member_idList.contains(user_id)){
                    holder.checkBox.isVisible = true
                }
            }




        holder.itemView.setOnClickListener {

            Log.d("member",user_id)

            if (holder.checkBox.isVisible){

                holder.checkBox.isVisible = false

                member_idList.remove(user_id)

                db.collection("boards")
                    .document(board_name.toString())
                    .collection("lists")
                    .document(list_name.toString())
                    .collection("cards")
                    .document(card_id.toString())
                    .update("members",member_idList)
                    .addOnSuccessListener {
                        Log.d("member ","removed")
                    }.addOnFailureListener {
                        Log.d("member ",it.message.toString())
                    }

            }
            else{

                holder.checkBox.isVisible = true
                member_idList.add(user_id)

                db.collection("boards")
                    .document(board_name.toString())
                    .collection("lists")
                    .document(list_name.toString())
                    .collection("cards")
                    .document(card_id.toString())
                    .update("members",member_idList)
                    .addOnSuccessListener {
                    Log.d("member ","added")

                }.addOnFailureListener {
                    Log.d("member ",it.message.toString())
                }

            }

        }

    }

    override fun getItemCount(): Int {
        return memberNameList.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {


        val name = v.findViewById<TextView>(R.id.tv_memberName)
        val memberImage = v.findViewById<ImageView>(com.example.ugp.R.id.iv_member_photo)
        val checkBox = v.findViewById<ImageView>(R.id.iv_checkbox)

    }
}
