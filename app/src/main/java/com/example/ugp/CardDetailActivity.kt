package com.example.ugp

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ugp.databinding.ActivityCardDetailBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class CardDetailActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding : ActivityCardDetailBinding

    private var startDateClicked : Boolean = false
    private var endDateClicked : Boolean = false
    val db = Firebase.firestore
    private var board_name : String? = ""
    private var card_id : String? = ""
    private var list_name : String? = ""
    private var card_name : String? = ""
    private var list_text : String? = ""
    var memberList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.aboutCardToolbar)
        /*binding.aboutCardToolbar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.ic_round_arrow_back_24, resources.newTheme())
        binding.aboutCardToolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(
            ResourcesCompat
            .getColor(resources, R.color.black, resources.newTheme()), PorterDuff.Mode.SRC_ATOP)

        binding.aboutCardToolbar.setNavigationOnClickListener{
            val intent = Intent(this, BoardActivity::class.java)
            intent.putExtra("boardName", binding.tvCardName.text.toString())
            startActivity(intent)*/
        //}

         board_name = intent.extras?.getString("board_name")
         card_id = intent.extras?.getString("card_id")
         list_name = intent.extras?.getString("list_name")
         card_name = intent.extras?.getString("card_name")
         list_text = intent.extras?.getString("list_text")



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
                memberList = it.get("members") as ArrayList<String>
                Log.d("member list","firebase reached")


                if (memberList.isNullOrEmpty()){
                    binding.rvImage.isVisible = false
                    binding.hintText.isVisible = true
                    binding.hintText.text = "Members..."

                    Log.d("memberList","empty")

                }else{
                    binding.rvImage.isVisible = true
                    binding.hintText.isVisible = false
                    Log.d("memberList","not empty")

                    binding.rvImage.apply {
                        layoutManager = LinearLayoutManager(this@CardDetailActivity,LinearLayoutManager.HORIZONTAL,false)
                    }
                    binding.rvImage.adapter = MemberImageAdapter(memberList,this )
                    Log.d("memberList","adapter attached")

                }

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
            intent.putExtra("card_id",card_id)
            intent.putExtra("board_name",board_name)
            intent.putExtra("list_name",list_name)

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

    /*override fun onResume() {
        super.onResume()
        Log.d("onResume","reached")
    }

    override fun onPause() {
        super.onPause()
        Log.d("onPause","reached")

    }*/
    /* override fun onBackPressed() {
         super.onBackPressed()
         val intent = Intent(this, BoardActivity::class.java)
     //    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
         startActivity(intent)
         finish()
     }*/
}

class MemberImageAdapter(private val memberList: ArrayList<String>, private val context: Context) : RecyclerView.Adapter<MemberImageAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val image = v.findViewById<CircleImageView>(R.id.iv_circularimage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val v = inflater.inflate(R.layout.card_member_image,parent,false)

        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val db = Firebase.firestore

        db.collection("users")
            .document(memberList[position])
            .get()
            .addOnSuccessListener {

                if (it["photo_url"].toString().isEmpty()){

                    holder.image.setImageResource(R.drawable.ic_round_person_24)
                }
                else{
                    Glide.with(context)
                        .load(it["photo_url"].toString())
                        .into(holder.image)
                }
                }




    }

    override fun getItemCount(): Int {
        return memberList.size
    }


}