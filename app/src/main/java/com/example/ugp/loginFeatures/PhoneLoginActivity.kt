package com.example.ugp.loginFeatures

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.ugp.MainActivity
import com.example.ugp.R
import com.example.ugp.databinding.ActivityPhoneLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class PhoneLoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityPhoneLoginBinding

    private var storedVerificationId : String? = null
    private var resendToken : String? = null
    private val db = Firebase.firestore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set status bar blue
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue2)

        binding.progressBar.visibility = View.INVISIBLE

        mAuth = FirebaseAuth.getInstance()

        // on Click of Button Send OTP
        binding.btSendOtp.setOnClickListener {

            binding.countryCodePicker.registerCarrierNumberEditText(binding.etPhoneNumber)

            val checkNumber = binding.etPhoneNumber.text.toString()
            val phoneNumber = binding.countryCodePicker.fullNumberWithPlus
            val name = binding.etNamePhoneLogin.text.toString()

            // checking if number is valid
            if (!binding.countryCodePicker.isValidFullNumber){
                binding.etPhoneNumber.error = "Enter a Valid Phone Number"
                binding.etPhoneNumber.requestFocus()
                return@setOnClickListener
            }

            // sending otp
            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(phoneAuthCallBack)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

            // hiding the after receive OTP widgets
            binding.etEnterOtp.visibility = View.VISIBLE
            binding.tvResendOtp.visibility = View.VISIBLE
            binding.btVerifyOtp.visibility = View.VISIBLE

        }

        // on Click of Button ReSend OTP
        binding.tvResendOtp.setOnClickListener {

            binding.countryCodePicker.registerCarrierNumberEditText(binding.etPhoneNumber)

            val phoneNumber = binding.countryCodePicker.fullNumberWithPlus
            val name = binding.etNamePhoneLogin.text.toString()

            // sending otp
            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(phoneAuthCallBack)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)


        }


        binding.btVerifyOtp.setOnClickListener {

            val code = binding.etEnterOtp.text.toString()
            binding.countryCodePicker.registerCarrierNumberEditText(binding.etPhoneNumber)

            val phoneNumber = binding.countryCodePicker.fullNumberWithPlus
            val name = binding.etNamePhoneLogin.text.toString()

            if (code.isEmpty()){
                binding.etEnterOtp.error = "Code Required"
                binding.etEnterOtp.requestFocus()
                return@setOnClickListener
            }

            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)

            signInWithPhoneAuthCredential(credential, phoneNumber , name)
            binding.progressBar.visibility = View.VISIBLE
        }
        
    }
    private val phoneAuthCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            binding.countryCodePicker.registerCarrierNumberEditText(binding.etPhoneNumber)

            val phoneNumber = binding.countryCodePicker.fullNumberWithPlus
            val name = binding.etNamePhoneLogin.text.toString()

            Log.d("Verification", "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential, phoneNumber, name)
            binding.progressBar.visibility = View.VISIBLE
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("Verification" ,"onVerificationFailed", e)
            Toast.makeText(this@PhoneLoginActivity, e.message, Toast.LENGTH_SHORT).show()
           
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("Code", "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
           // resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        phoneNumber: String,
        name: String
    ) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                      //  task.result.user.

                    val user = hashMapOf(

                        "name" to name ,
                        "email" to "",
                        "phone" to phoneNumber,
                        "doc_id" to task.result.user!!.uid,
                        "photo_url" to task.result.user!!.photoUrl.toString()
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


                        binding.progressBar.visibility = View.INVISIBLE
                    Log.d("TAG", "signInWithCredential:success")
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()


                } else {
                    // Sign in failed, display a message and update the UI
                    binding.progressBar.visibility = View.INVISIBLE
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, "Invalid Code", Toast.LENGTH_SHORT).show()
                    }
                    // Update UI
                }
            }
    }


}