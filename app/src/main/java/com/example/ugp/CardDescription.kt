package com.example.ugp

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment


class CardDescription : Fragment() {
    private  lateinit var cardDescriptionToolbar : androidx.appcompat.widget.Toolbar
    private lateinit var cardDescription : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardDescriptionToolbar = view.findViewById(R.id.cardDescriptionToolbar)
        cardDescription = view.findViewById(R.id.textView)

        cardDescriptionToolbar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.ic_round_arrow_back_24, resources.newTheme())
        cardDescriptionToolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(ResourcesCompat
            .getColor(resources, R.color.black, resources.newTheme()), PorterDuff.Mode.SRC_ATOP)
        cardDescriptionToolbar.setNavigationOnClickListener {
            val intent = Intent(view.context, CardDetailActivity::class.java)
            startActivity(intent)
        }

    }

}