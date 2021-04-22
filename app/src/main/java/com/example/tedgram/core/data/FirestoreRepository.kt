package com.example.tedgram.core.data

import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tedgram.core.data.local.entity.User
import com.example.tedgram.databinding.ActivityRegisterBinding
import com.example.tedgram.presentation.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository(private var mAuth: FirebaseAuth, private var db: FirebaseFirestore) {

    companion object {
        @Volatile
        private var instance: FirestoreRepository? = null
        fun getInstance(mAuth: FirebaseAuth, db: FirebaseFirestore) =
            instance ?: synchronized(this) {
                instance ?: FirestoreRepository(mAuth, db)
            }
    }

    fun registerNewUser(email: String, password: String, username: String, binding: ActivityRegisterBinding){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task->
            if ( task.isSuccessful ) {
                val user = mAuth.currentUser
                val userModel =
                    User(
                        user.uid,
                        "",
                        username,
                        email,
                        password,
                        "",
                        "",
                        false
                    )
                addToFirestore(userModel)
                binding.progressBar.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun addToFirestore(user: User){
        val userMap: MutableMap<String, Any> = HashMap()
        userMap["user"] = user

        db.collection("users").document(user.userId).set(
            user
        )
    }

}