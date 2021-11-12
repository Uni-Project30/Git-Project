package com.example.ugp

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.example.ugp.databinding.ActivityCardDetailBinding
import java.util.*

private lateinit var binding : ActivityCardDetailBinding

private var startDateClicked : Boolean = false
private var endDateClicked : Boolean = false

class CardDetailActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvCardStartDate.setOnClickListener {
            startDateClicked = true
            showDatePickerDialog()

        }

        binding.tvCardEndDate.setOnClickListener {
            endDateClicked = true
            showDatePickerDialog()

        }

        binding.tvCardDescription.setOnClickListener {

            val fragment = CardDescription()
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content ,fragment )
                .commit()
        }


    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = "$dayOfMonth/${month + 1}/$year"
        if (startDateClicked){
            binding.tvCardStartDate.text = date
            startDateClicked=false
        }
        if (endDateClicked){
            binding.tvCardEndDate.text = date
            endDateClicked = false
        }

    }
}