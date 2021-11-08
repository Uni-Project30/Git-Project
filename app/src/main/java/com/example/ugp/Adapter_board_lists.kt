package com.example.ugp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter_board_lists(val data:ArrayList<data_board_lists>):RecyclerView.Adapter<Adapter_board_lists.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.list_cardview,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val input = data[position]
        holder.bind(input)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(v: View):RecyclerView.ViewHolder(v)
    {
        val name = v.findViewById<EditText>(R.id.list_name)
        fun bind(data:data_board_lists) {
            name.setText(data.name)
        }
    }
}