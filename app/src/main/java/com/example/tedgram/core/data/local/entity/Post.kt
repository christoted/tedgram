package com.example.tedgram.core.data.local.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.lang.reflect.Constructor

@Parcelize
data class Post(
    var postId: String ?= " ",
    var postURL: String ?= " ",
    var postCaption: String ?= " ",
    var userImageURL: String ?= " ",
    var username: String ?= " ",
    var userId: String ?= " "

): Parcelable