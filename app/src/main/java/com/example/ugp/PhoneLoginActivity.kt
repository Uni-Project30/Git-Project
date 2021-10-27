package com.example.ugp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.ugp.databinding.ActivityPhoneLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class PhoneLoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityPhoneLoginBinding

    private var storedVerificationId : String? = null
    private var resendToken : String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.INVISIBLE

        mAuth = FirebaseAuth.getInstance()

        // on Click of Button Send OTP
        binding.btSendOtp.setOnClickListener {

            binding.countryCodePicker.registerCarrierNumberEditText(binding.etPhoneNumber)

            val checkNumber = binding.etPhoneNumber.text.toString()
            val phoneNumber = binding.countryCodePicker.fullNumberWithPlus

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

        }

        // on Click of Button ReSend OTP
        binding.tvResendOtp.setOnClickListener {

            binding.countryCodePicker.registerCarrierNumberEditText(binding.etPhoneNumber)

            val phoneNumber = binding.countryCodePicker.fullNumberWithPlus
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

            if (code.isEmpty()){
                binding.etEnterOtp.error = "Code Required"
                binding.etEnterOtp.requestFocus()
                return@setOnClickListener
            }

            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)

            signInWithPhoneAuthCredential(credential)
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
            Log.d("Verification", "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
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

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    binding.progressBar.visibility = View.INVISIBLE
                    Log.d("TAG", "signInWithCredential:success")
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()


                    val user = task.result?.user
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