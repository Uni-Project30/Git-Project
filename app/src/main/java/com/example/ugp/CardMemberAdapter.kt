package com.example.ugp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView

import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class CardMemberAdapter(val memberList: ArrayList<String>) :RecyclerView.Adapter<CardMemberAdapter.ViewHolder>() {

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

        holder.name.text = memberList[position]
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val image = v.findViewById<CircleImageView>(R.id.iv_member_image)
        val name = v.findViewById<CheckedTextView>(R.id.checked_tv)


    }
}
