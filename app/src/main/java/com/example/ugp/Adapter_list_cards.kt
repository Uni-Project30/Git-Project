package com.example.ugp

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class Adapter_list_cards(val data:ArrayList<data_boards_list_card>):RecyclerView.Adapter<Adapter_list_cards.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Adapter_list_cards.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: Adapter_list_cards.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ViewHolder(v: View):RecyclerView.ViewHolder(v)
    {
        fun bind()
        {

        }
    }
}