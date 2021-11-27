package com.example.ugp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.ugp.databinding.ActivityCardDetailBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class CardDetailActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding : ActivityCardDetailBinding

    private var startDateClicked : Boolean = false
    private var endDateClicked : Boolean = false
    private val db = Firebase.firestore
    private var board_name : String? = ""
    private var card_id : String? = ""
    private var list_name : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

         board_name = intent.extras?.getString("board_name")
         card_id = intent.extras?.getString("card_id")
         list_name = intent.extras?.getString("list_name")
        val card_name = intent.extras?.getString("card_name")
        val list_text = intent.extras?.getString("list_text")



        db.collection("boards")
            .document(board_name.toString())
            .collection("lists")
            .document(list_name.toString())
            .collection("cards")
            .document(card_id.toString())
            .get()
            .addOnSuccessListener {

                binding.tvCardDescription.text = it.getString("description")
                binding.tvCardStartDate.text = it.getString("start_date")
                binding.tvCardEndDate.text = it.getString("end_date")

            }


        binding.tvCardName.text = card_name.toString()
        binding.tvListName.append(list_text.toString())


        binding.tvCardStartDate.setOnClickListener {
            startDateClicked = true

            showDatePickerDialogStartDate()

        }

        binding.tvCardEndDate.setOnClickListener {
            endDateClicked = true
            showDatePickerDialogEndDate()

        }

        binding.tvCardDescription.setOnClickListener {

            binding.aboutCardToolbar.visibility = View.GONE
            binding.cardLinearLayout.visibility = View.GONE
            val fragment = CardDescription()
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
        }


        binding.llMembers.setOnClickListener {

            val intent = Intent(this,CardMemberActivity::class.java)

            startActivity(intent)

        }


    }


    private fun showDatePickerDialogStartDate() {

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

    private fun showDatePickerDialogEndDate() {
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
   //     datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = "$dayOfMonth/${month + 1}/$year"

        if (startDateClicked){
            binding.tvCardStartDate.text = date
            startDateClicked=false

            db.collection("boards")
                .document(board_name.toString())
                .collection("lists")
                .document(list_name.toString())
                .collection("cards")
                .document(card_id.toString())
                .update("start_date" , date)
        }
        if (endDateClicked){
            binding.tvCardEndDate.text = "       $date"
            endDateClicked = false


            db.collection("boards")
                .document(board_name.toString())
                .collection("lists")
                .document(list_name.toString())
                .collection("cards")
                .document(card_id.toString())
                .update("end_date", "       $date")
        }

    }
}