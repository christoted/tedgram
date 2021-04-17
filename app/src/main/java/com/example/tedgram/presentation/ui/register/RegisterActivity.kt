package com.example.tedgram.presentation.ui.register

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tedgram.core.data.local.entity.User
import com.example.tedgram.databinding.ActivityRegisterBinding
import com.example.tedgram.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = RegisterActivity::class.java.simpleName
    }

    private var mAuth: FirebaseAuth ?= null

    private var db: FirebaseFirestore ?= null

    private var _binding: ActivityRegisterBinding ?= null
    private val binding get() = _binding

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val viewModelFactory = ViewModelFactory.getInstance(this)

        registerViewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding?.btnRegister?.setOnClickListener {
            registerUser()
        }


    }

    private fun registerUser() {
        val email: String = binding?.etEmail?.text.toString()
        val password: String = binding?.etPassowrd?.text.toString()
        val username: String = binding?.etUsername?.text.toString()

        if ( email.isEmpty()) {
            binding?.etEmail?.error = "Please fill this email"
        } else if ( password.isEmpty()) {
            binding?.etPassowrd?.error = "Please fill this password"
        } else if  ( username.isEmpty()) {
            binding?.etUsername?.error = "Please fill this username"
        } else {
            binding?.progressBar?.visibility = View.VISIBLE
           createNewUser(email,password, username)

        }
    }

    private fun createNewUser(email: String, password: String, username: String) {
        binding?.let { registerViewModel.registerUser(email, password, username, it) }
        finish()
//            .addOnCompleteListener(
//                this
//            ) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "createUserWithEmail:success")
//                    val user = mAuth!!.currentUser
//                    val userModel =
//                        User(
//                            user.uid,
//                            "",
//                            username,
//                            email,
//                            password,
//                            "",
//                            ""
//                        )
//                    addToFirestore(userModel)
//                    Toast.makeText(
//                        this, "Success Create User",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    binding?.progressBar?.visibility = View.INVISIBLE
////                    mAuth?.signOut()
//                    finish()
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(
//                        TAG,
//                        "createUserWithEmail:failure",
//                        task.exception
//                    )
//                    Toast.makeText(
//                        this, "Authentication failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    binding?.progressBar?.visibility = View.INVISIBLE
//                }
 //           }
    }

    private fun addToFirestore(user: User) {
        val userMap: MutableMap<String, Any> = HashMap()
        userMap["user"] = user

// Add a new document with a generated ID
//        db!!.collection("users")
//            .add(user)
//            .addOnSuccessListener { user.userId
//            }
//            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }

        db!!.collection("users").document(user.userId).set(
            user
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}