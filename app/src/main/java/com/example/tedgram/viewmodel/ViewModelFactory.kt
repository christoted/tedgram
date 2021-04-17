package com.example.tedgram.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tedgram.core.data.FirestoreRepository
import com.example.tedgram.di.Injection
import com.example.tedgram.presentation.ui.register.RegisterViewModel

class ViewModelFactory private constructor(private val firestoreRepository: FirestoreRepository): ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var instance: ViewModelFactory ?= null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository())
            }
    }


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       when{
           modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
               return RegisterViewModel(firestoreRepository) as T
           }
           else -> throw Throwable("Uknown View Model")
       }
       }
}