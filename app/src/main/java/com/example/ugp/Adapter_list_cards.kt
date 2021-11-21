package com.example.ugp

import android.content.Intent
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class Adapter_list_cards(val data:ArrayList<data_boards_list_card>):RecyclerView.Adapter<Adapter_list_cards.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val input = data[position]
        holder.bind(input)
        holder.itemView.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val intent = Intent(activity,CardDetailActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
       return data.size
    }

    class ViewHolder(v: View):RecyclerView.ViewHolder(v)
    {
        val card_name = v.findViewById<TextView>(R.id.card_name)
        fun bind(data:data_boards_list_card)
        {
            card_name.setText(data.card_name)
        }
    }
}