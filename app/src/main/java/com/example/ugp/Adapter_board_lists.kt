package com.example.ugp

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ugp.loginFeatures.LoginActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.list_cardview.view.*

class Adapter_board_lists(val data: ArrayList<data_board_lists>):RecyclerView.Adapter<Adapter_board_lists.ViewHolder>() {

    private lateinit var c_list:ArrayList<data_boards_list_card>
    val db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        c_list = ArrayList()
        val v = inflater.inflate(R.layout.list_cardview,parent,false)
        v.list_name.visibility = View.INVISIBLE
        v.tick_btn.visibility = View.INVISIBLE

        //setting uo recyclerview for the cards list
        v.rv_list.apply {
            val activity = v.context as AppCompatActivity
            layoutManager = LinearLayoutManager(activity)
        }


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
            data[position].name = holder.list_name.text.toString()
            db.collection("boards")
                .document(data[position].board_name)
                .collection("lists")
                .document(data[position].doc_name)
                .update("name", data[position].name)

            holder.tick.visibility = View.INVISIBLE
            holder.list_name.visibility = View.INVISIBLE
            holder.list_name_t.visibility = View.VISIBLE
        }


        //getting
        holder.add_list.setOnClickListener {
            db.collection("boards")
                .document(data[position].board_name)
                .collection("lists")
                .document(data[position].doc_name)
                .collection("cards")
//                .addSnapshotListener { value, error ->
//                    if (error != null) {
//                        Log.w(ContentValues.TAG, error.message.toString())
//                        return@addSnapshotListener
//                    }
//                    for (dc: DocumentChange in value?.documentChanges!!) {
//                        if (dc.type == DocumentChange.Type.ADDED) {
//                            c_list.add(dc.document.toObject(data_boards_list_card::class.java))
//                        }
//                    }
//                    holder.rv_l.adapter = Adapter_list_cards(c_list)
//                    holder.rv_l.adapter!!.notifyDataSetChanged()
//                }

                .get()
                .addOnSuccessListener { dc ->
                    for (i in dc) {
                        c_list.add(i.toObject(data_boards_list_card::class.java))
                    }
                    holder.rv_l.adapter = Adapter_list_cards(c_list)
                    holder.rv_l.adapter!!.notifyDataSetChanged()
                }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(v: View):RecyclerView.ViewHolder(v)
    {
        val rv_l = v.findViewById<RecyclerView>(R.id.rv_list)
        val tick = v.findViewById<ImageView>(R.id.tick_btn)
        val list_name = v.findViewById<EditText>(R.id.list_name)
        val list_name_t = v.findViewById<TextView>(R.id.list_name_t)
        val add_list = v.findViewById<Button>(R.id.add_card_btn)
        val name = v.findViewById<TextView>(R.id.list_name_t)
        fun bind(data:data_board_lists) {
            name.text = data.name
        }
    }
}