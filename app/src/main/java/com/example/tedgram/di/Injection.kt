package com.example.tedgram.di

import com.example.tedgram.core.data.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object Injection {

    fun provideRepository(): FirestoreRepository{
        var mAuth: FirebaseAuth ?= null
        var db: FirebaseFirestore ?= null

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        return FirestoreRepository.getInstance(mAuth, db)
    }
}