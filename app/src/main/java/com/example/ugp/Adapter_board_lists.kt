package com.example.ugp


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.list_cardview1.view.*
import java.util.*
import kotlin.collections.ArrayList

class Adapter_board_lists(val data: ArrayList<data_board_lists>,val context: Context) :
    RecyclerView.Adapter<Adapter_board_lists.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    private var c_list: ArrayList<data_boards_list_card> = ArrayList()
    val db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.list_cardview1, parent, false)
        v.list_name.visibility = View.INVISIBLE
        v.tick_btn.visibility = View.INVISIBLE
        v.cancel_btn.visibility = View.INVISIBLE
        v.card_name1.visibility = View.INVISIBLE
        v.tick_btn1.visibility = View.INVISIBLE
        v.cancel_btn1.visibility = View.INVISIBLE
        v.card_name_view.visibility = View.INVISIBLE


        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val input = data[position]
        holder.bind(input)

        //setting up recyclerview for the cards list
        val layout  = LinearLayoutManager(holder.rv_l.context)
        layout.initialPrefetchItemCount = c_list.size

        holder.rv_l.apply {
            layoutManager = layout
            setRecycledViewPool(viewPool)
        }

        for(j in data)
        {
            db.collection("boards")
                .document(data[position].board_name)
                .collection("lists")
                .document(data[position].doc_name)
                .collection("cards")
                .get()
                .addOnSuccessListener {
                    c_list = ArrayList()
                    for(i in it) {
                        c_list.add(i.toObject(data_boards_list_card::class.java))
                        Log.i("error","Some error")
                    }
                    Log.i("Message","go the data")
                    holder.rv_l.adapter = Adapter_list_cards(c_list)
                    holder.rv_l.adapter!!.notifyDataSetChanged()
                }
        }

        // changing name of the list and storing in firebase
        holder.name.setOnClickListener {
            holder.tick.visibility = View.VISIBLE
            holder.cancel.visibility = View.VISIBLE
            holder.list_name.visibility = View.VISIBLE
            holder.list_name_t.visibility = View.INVISIBLE
        }

        //on clicking of tick for list name
        holder.tick.setOnClickListener {
            if (holder.list_name.text.toString().isNotEmpty()) {
                data[position].name = holder.list_name.text.toString()
                db.collection("boards")
                    .document(data[position].board_name)
                    .collection("lists")
                    .document(data[position].doc_name)
                    .update("name", data[position].name)
            }
            holder.tick.visibility = View.INVISIBLE
            holder.cancel.visibility = View.INVISIBLE
            holder.list_name.visibility = View.INVISIBLE
            holder.list_name_t.visibility = View.VISIBLE
        }

        //on clicking of cancel for list name
        holder.cancel.setOnClickListener {
            holder.tick.visibility = View.INVISIBLE
            holder.cancel.visibility = View.INVISIBLE
            holder.list_name.visibility = View.INVISIBLE
            holder.list_name_t.visibility = View.VISIBLE
        }

        //getting
        holder.add_card.setOnClickListener {
            holder.card_name.visibility = View.VISIBLE
            holder.tick1.visibility = View.VISIBLE
            holder.cancel1.visibility = View.VISIBLE
            holder.card_view.visibility = View.VISIBLE
            holder.add_card.visibility = View.INVISIBLE
        }

        //on clicking of tick for card name
        holder.tick1.setOnClickListener {

            holder.card_name.visibility = View.INVISIBLE
            holder.tick1.visibility = View.INVISIBLE
            holder.cancel1.visibility = View.INVISIBLE
            holder.card_view.visibility = View.INVISIBLE
            holder.add_card.visibility = View.VISIBLE

            if (holder.card_name.text.toString().isNotEmpty()) {

                val card_id = db.collection("boards")
                    .document(data[position].board_name)
                    .collection("lists")
                    .document(data[position].doc_name)
                    .collection("cards")
                    .document().id

                val d = hashMapOf(
                    "board_name" to data[position].board_name,
                    "card_id" to card_id,
                    "card_name" to holder.card_name.text.toString(),
                    "list_name" to data[position].doc_name
                )

                db.collection("boards")
                    .document(data[position].board_name)
                    .collection("lists")
                    .document(data[position].doc_name)
                    .collection("cards")
                    .document(card_id)
                    .set(d)
            }
        }

        //on clicking of cancel for card name
        holder.cancel1.setOnClickListener {
            holder.card_name.visibility = View.INVISIBLE
            holder.tick1.visibility = View.INVISIBLE
            holder.cancel1.visibility = View.INVISIBLE
            holder.card_view.visibility = View.INVISIBLE
            holder.add_card.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val rv_l = v.findViewById<RecyclerView>(R.id.rv_list)
        val card_view = v.findViewById<CardView>(R.id.card_name_view)
        val tick = v.findViewById<ImageView>(R.id.tick_btn)
        val tick1 = v.findViewById<ImageView>(R.id.tick_btn1)
        val cancel = v.findViewById<ImageView>(R.id.cancel_btn)
        val cancel1 = v.findViewById<ImageView>(R.id.cancel_btn1)
        val card_name = v.findViewById<EditText>(R.id.card_name1)
        val list_name = v.findViewById<EditText>(R.id.list_name)
        val list_name_t = v.findViewById<TextView>(R.id.list_name_t)
        val add_card = v.findViewById<Button>(R.id.add_card_btn)
        val name = v.findViewById<TextView>(R.id.list_name_t)
        fun bind(data: data_board_lists) {
            name.text = data.name
        }
    }
}