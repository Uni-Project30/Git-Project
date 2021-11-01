package com.example.ugp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import boards.Boards
import com.example.ugp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth : FirebaseAuth
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        // initialising firebase auth
        mAuth = FirebaseAuth.getInstance()

        binding.btRegisterRegister.setOnClickListener {

            val email = binding.etEmailRegister.text.toString().trim()
            val password = binding.etPasswordRegister.text.toString().trim()
            val name = binding.etNameRegister.text.toString().trim()

            if (name.isEmpty()){
                binding.etNameRegister.error = "Name Required"
                binding.etNameRegister.requestFocus()
                return@setOnClickListener

            }
            if (password.isEmpty() || password.length < 6){
                binding.etPasswordRegister.error = "Minimum 6 character password required"
                binding.etPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()){
                binding.etEmailRegister.error = "Email Required"
                binding.etEmailRegister.requestFocus()
                return@setOnClickListener
            }
            // validating email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.etEmailRegister.error = "Valid Email Required"
                binding.etEmailRegister.requestFocus()
                return@setOnClickListener
            }

            registerUser(email,password,name)

        }



    }

    private fun registerUser(email: String, password: String, name: String) {

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful){

                   val user = hashMapOf(

                       "name" to name ,
                       "email" to email,
                       "phone" to String(),
                       "boards" to ArrayList<Boards>()
                   )

                    db.collection("users")
                        .document(task.result.user!!.uid)
                        .set(user)
                        .addOnSuccessListener {
                            Log.d("data in firestore" , "true")
                        }
                        .addOnFailureListener {
                            Log.d("data in firestore",it.message.toString() )
                        }

                    Log.d("name final", mAuth.currentUser!!.displayName.toString())
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }

            }


    }
}