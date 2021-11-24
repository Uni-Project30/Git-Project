package com.example.ugp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.ugp.databinding.ActivityCardDetailBinding
import java.util.*

class CardDetailActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding : ActivityCardDetailBinding

    private var startDateClicked : Boolean = false
    private var endDateClicked : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvCardStartDate.setOnClickListener {
            startDateClicked = true
            showDatePickerStartDialog()
        }

        binding.tvCardEndDate.setOnClickListener {

//            if(!startDateClicked || binding.tvCardStartDate.text == null) {
//                binding.tvCardStartDate.error = "Enter Starting Date"
//                binding.tvCardStartDate.requestFocus()
//                return@setOnClickListener
//            }
            endDateClicked = true
            showDatePickerEndDialog()
        }

        binding.tvCardDescription.setOnClickListener {

            binding.aboutCardToolbar.visibility = View.GONE
            binding.cardLinearLayout.visibility = View.GONE
            val fragment = CardDescription()
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
        }


    }

    private fun showDatePickerStartDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()
    }

    private fun showDatePickerEndDialog() {

        val startDate = binding.tvCardStartDate.text.toString()

        Calendar.getInstance().set(Calendar.DAY_OF_MONTH, Integer.parseInt(startDate.split("/")[0]))
        Calendar.getInstance().set(Calendar.MONTH, Integer.parseInt(startDate.split("/")[1]) - 1)
        Calendar.getInstance().set(Calendar.YEAR, Integer.parseInt(startDate.split("/")[2]))

        val datePickerDialog = DatePickerDialog(
            this,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
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