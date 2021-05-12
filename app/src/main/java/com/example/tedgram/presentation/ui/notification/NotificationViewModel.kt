package com.example.tedgram.presentation.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tedgram.core.data.FirestoreRepository
import com.firebase.ui.auth.data.model.User

class NotificationViewModel(private var firestoreRepository: FirestoreRepository) : ViewModel() {

    var followUpdateLiveData: MutableLiveData<List<String>> = MutableLiveData()

    fun getAllUpdate(currentId: String): LiveData<List<String>>  {
        firestoreRepository.getAllUpdate(currentId).addSnapshotListener { value, error ->

            val followUpdate: MutableList<String> = mutableListOf()

            if (error == null && value != null) {
                for ( res in value) {
                    val userIdThatFollowMe = res.id
                    followUpdate.add(userIdThatFollowMe)
                }

                followUpdateLiveData.value = followUpdate
            }
        }

        return followUpdateLiveData
    }
}