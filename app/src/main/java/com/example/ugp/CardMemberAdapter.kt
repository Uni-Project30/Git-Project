package com.example.ugp

import android.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.FileNotFoundException
import java.io.InputStream


class CardMemberAdapter(val memberList: ArrayList<String>, val context : Context) :RecyclerView.Adapter<CardMemberAdapter.ViewHolder>() {

    val mAuth = Firebase.auth

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardMemberAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val v = inflater.inflate(R.layout.,parent,false)
        Log.d("card","reached")


        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardMemberAdapter.ViewHolder, position: Int) {

        holder.name.text = memberList[position]
        val pic = mAuth.currentUser?.photoUrl
        var yourDrawable : Drawable?

        try {

            val inputStream: InputStream? = context.contentResolver.openInputStream(pic!!)!!
            yourDrawable = Drawable.createFromStream(inputStream, pic.toString())
        } catch (e: FileNotFoundException) {
            yourDrawable = ContextCompat.getDrawable( context, R.drawable.sym_call_incoming);
        }


      //  holder.name.setCompoundDrawablesWithIntrinsicBounds()
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {


        val name = v.findViewById<TextView>(R.id.)

    }
}
