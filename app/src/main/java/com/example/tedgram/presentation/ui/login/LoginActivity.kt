package com.example.tedgram.presentation.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tedgram.R
import com.example.tedgram.databinding.ActivityLoginBinding
import com.example.tedgram.presentation.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding ?= null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.tvRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}