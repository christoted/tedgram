package com.example.tedgram.core.data.local.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var userId: String,
    var fullName: String,
    var username: String,
    var email: String,
    var password: String,
    var bio: String,
    var imageUrl: String
) : Parcelable