package com.example.ugp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ugp.loginFeatures.LoginActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.list_cardview.view.*

class Adapter_board_lists(val data: ArrayList<data_board_lists>):RecyclerView.Adapter<Adapter_board_lists.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.list_cardview,parent,false)
        v.list_name.visibility = View.INVISIBLE
        v.tick_btn.visibility = View.INVISIBLE
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val input = data[position]
        holder.bind(input)

        // changing name of the list and storing in firebase
        holder.name.setOnClickListener {
            holder.tick.visibility = View.VISIBLE
            holder.list_name.visibility = View.VISIBLE
            holder.list_name_t.visibility = View.INVISIBLE
        }

        holder.tick.setOnClickListener {
            data[position].name = holder.list_name.toString()

            // ADD CODE HERE FOR CHANGING THE NAME OF LIST IN FIREBASE

            holder.bind(input)
            holder.tick.visibility = View.INVISIBLE
            holder.list_name.visibility = View.INVISIBLE
            holder.list_name_t.visibility = View.VISIBLE
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(v: View):RecyclerView.ViewHolder(v)
    {

        val tick = v.findViewById<ImageView>(R.id.tick_btn)
        val list_name = v.findViewById<EditText>(R.id.list_name)
        val list_name_t = v.findViewById<TextView>(R.id.list_name_t)
        val name = v.findViewById<TextView>(R.id.list_name_t)
        fun bind(data:data_board_lists) {
            name.text = data.name
        }
    }
}