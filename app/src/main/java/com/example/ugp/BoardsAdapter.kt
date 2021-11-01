package com.example.ugp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.board_item.view.*

class BoardsAdapter(
    private val context: Context,
    private val listOfMap : ArrayList<String>):
RecyclerView.Adapter<BoardsAdapter.BoardsViewHolder>(){

    inner class BoardsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position!=RecyclerView.NO_POSITION){
                val intent = Intent(v?.context,BoardActivity::class.java)
                intent.putExtra("boardName",itemView.tv_board_name.text.toString())
                itemView.context.startActivity(intent)
                (context as MainActivity).finish()
            }
        }

        fun bindBoard(position: Int){
            itemView.tv_board_name.text = listOfMap[position]
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardsViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.board_item,parent,false)
        return BoardsViewHolder(itemView)
    }

    override fun onBindViewHolder(holderFavourites: BoardsViewHolder, position: Int) {
        holderFavourites.bindBoard(position)
    }
    override fun getItemCount(): Int {
        return listOfMap.size
    }
}