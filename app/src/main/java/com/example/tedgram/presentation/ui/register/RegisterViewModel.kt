package com.example.tedgram.presentation.ui.register

import androidx.lifecycle.ViewModel
import com.example.tedgram.core.data.FirestoreRepository
import com.example.tedgram.databinding.ActivityRegisterBinding

class RegisterViewModel(private var firestoreRepository: FirestoreRepository): ViewModel(){

    fun registerUser(email: String, password: String, username: String, binding: ActivityRegisterBinding) {
        firestoreRepository.registerNewUser(email,password,username, binding)
    }

}