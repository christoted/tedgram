package com.example.tedgram.presentation.ui.register

import android.R.attr.password
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.tedgram.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = RegisterActivity::class.java.simpleName
    }

    private var mAuth: FirebaseAuth ?= null

    private var _binding: ActivityRegisterBinding ?= null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mAuth = FirebaseAuth.getInstance()

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
           createNewUser(email,password)
        }
    }

    private fun createNewUser(email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = mAuth!!.currentUser
                    Toast.makeText(
                        this, "Success Create User",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding?.progressBar?.visibility = View.INVISIBLE
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(
                        TAG,
                        "createUserWithEmail:failure",
                        task.exception
                    )
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding?.progressBar?.visibility = View.INVISIBLE
                }


            }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}