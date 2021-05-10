package com.example.tedgram.di

import com.example.tedgram.core.data.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object Injection {

    fun provideRepository(): FirestoreRepository{
        var mAuth: FirebaseAuth ?= null
        var db: FirebaseFirestore ?= null
        var storage: FirebaseStorage ?= null

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        return FirestoreRepository.getInstance(mAuth, db, storage)
    }
}