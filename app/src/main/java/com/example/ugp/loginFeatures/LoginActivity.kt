package com.example.ugp.loginFeatures

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.ugp.MainActivity
import com.example.ugp.R
import com.example.ugp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var googleSignInClient  : GoogleSignInClient
    private val RC_SIGN_IN = 100
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //initialising firebase auth
        mAuth = FirebaseAuth.getInstance()

        // Click of Login Button
        binding.btLoginLogin.setOnClickListener{

            val email = binding.etEmailLogin.text.toString().trim()
            val password = binding.etPasswordLogin.text.toString().trim()

            if (password.isEmpty() || password.length < 6){
                binding.etPasswordLogin.error = "Minimum 6 character password required"
                binding.etPasswordLogin.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()){
                binding.etEmailLogin.error = "Email Required"
                binding.etEmailLogin.requestFocus()
                return@setOnClickListener
            }
            // validating email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.etEmailLogin.error = "Valid Email Required"
                binding.etEmailLogin.requestFocus()
                return@setOnClickListener
            }

            loginUser(email,password)
        }

        binding.tvRegisterNowLogin.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.tvPhoneNumberLogin.setOnClickListener {

            val intent = Intent(this, PhoneLoginActivity::class.java)
            startActivity(intent)
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.tvGoogleLogin.setOnClickListener {

            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent,RC_SIGN_IN)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.w("LoginActivity", "Google sign in successful")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginActivity", e.toString())
            }
        }


    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginActivity", "signInWithCredential:success")
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    Log.d("LoginActivity",mAuth.currentUser?.displayName.toString())

                    val user = hashMapOf(

                        "name" to task.result.user?.displayName,
                        "email" to task.result.user?.email,
                        "phone" to task.result.user?.phoneNumber,
                        "doc_id" to task.result.user!!.uid,
                        "photo_url" to task.result.user!!.photoUrl.toString()
                    )

                    db.collection("users")
                        .document(task.result.user!!.uid)
                        .set(user)
                        .addOnSuccessListener {
                            Log.d("data in Firestore" , "true")
                        }
                        .addOnFailureListener {
                            Log.d("data in Firestore",it.message.toString() )
                        }

                    Log.d("name final", mAuth.currentUser!!.displayName.toString())
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)

                }
            }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null)
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        //redirect to main Activity if user is not null
        if (currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // login via email and password
    private fun loginUser(email: String, password: String) {

        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {

                if (it.isSuccessful){
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }else{
                    Toast.makeText(this,"No such user found", Toast.LENGTH_SHORT).show()
                    Log.d("login",it.exception?.message.toString())
                }

            }
    }
}