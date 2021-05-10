package com.example.tedgram.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tedgram.core.data.FirestoreRepository
import com.example.tedgram.di.Injection
import com.example.tedgram.presentation.ui.home.HomeViewModel
import com.example.tedgram.presentation.ui.login.LoginViewModel
import com.example.tedgram.presentation.ui.notification.NotificationViewModel
import com.example.tedgram.presentation.ui.post.PostContentViewModel
import com.example.tedgram.presentation.ui.profile.below.post.PostViewModel
import com.example.tedgram.presentation.ui.profile.below.saved.SavedViewModel
import com.example.tedgram.presentation.ui.profile.edit.EditProfileViewModel
import com.example.tedgram.presentation.ui.register.RegisterViewModel
import com.example.tedgram.presentation.ui.search.SearchViewModel

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

           modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
               return HomeViewModel(firestoreRepository) as T
           }

           modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
               return LoginViewModel(firestoreRepository) as T
           }

           modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
               return NotificationViewModel(firestoreRepository) as T
           }

           modelClass.isAssignableFrom(PostContentViewModel::class.java) -> {
               return PostContentViewModel(firestoreRepository) as T
           }

            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                return PostViewModel(firestoreRepository) as T
            }

           modelClass.isAssignableFrom(SavedViewModel::class.java) -> {
               return SavedViewModel(firestoreRepository) as T
           }

           modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
               return EditProfileViewModel(firestoreRepository) as T
           }

           modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
               return RegisterViewModel(firestoreRepository) as T
           }

           modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
               return SearchViewModel(firestoreRepository) as T
           }

           else -> throw Throwable("Uknown View Model")
       }
       }
}