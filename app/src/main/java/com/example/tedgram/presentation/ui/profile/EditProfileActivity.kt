package com.example.tedgram.presentation.ui.profile

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.tedgram.R
import com.example.tedgram.databinding.ActivityEditProfileBinding
import com.example.tedgram.presentation.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class EditProfileActivity : AppCompatActivity() {

    private var _binding: ActivityEditProfileBinding ?= null
    private val binding get()= _binding

    private var mAuth: FirebaseAuth ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mAuth = FirebaseAuth.getInstance()

        binding?.toolbar?.setNavigationOnClickListener {
            showAlertDialog()
        }

        binding?.btnLogout?.setOnClickListener {
            mAuth?.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding?.toolbar?.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.done -> {
                    Snackbar.make(binding!!.root, "Update Success", Snackbar.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    },2000)

                }
            }
            true
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure to discard the edit profile? You're edit progress won't save")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes"
        ) { _, _ -> finish() }

        builder.setNegativeButton("No", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.cancel()
            }
        })

        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}