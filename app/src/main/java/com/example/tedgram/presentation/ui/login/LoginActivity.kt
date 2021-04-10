package com.example.tedgram.presentation.ui.login

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.tedgram.databinding.ActivityLoginBinding
import com.example.tedgram.presentation.ui.MainActivity
import com.example.tedgram.presentation.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }

    private var _binding: ActivityLoginBinding ?= null
    private val binding get() = _binding

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mAuth = FirebaseAuth.getInstance()

        if ( mAuth?.currentUser != null) {
           startActivity(Intent(this, MainActivity::class.java))
        }

        binding?.btnLogin?.setOnClickListener{
            validateLogin()
        }

        binding?.tvRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }


    }

    fun validateLogin(){
        val email: String = binding?.etEmail?.text.toString()
        val password: String = binding?.etPassowrd?.text.toString()

        if ( email.isEmpty()) {
            binding?.etEmail?.error = "Email must be filled"
        } else if ( password.isEmpty()) {
            binding?.etPassowrd?.error = "Password must be filled"
        } else {
            binding?.progressBar?.visibility = View.VISIBLE
            checkExistingAccount(email,password)
        }
    }

    private fun checkExistingAccount(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    binding?.progressBar?.visibility = View.GONE
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding?.progressBar?.visibility = View.GONE
                }

                // ...
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}