package com.example.tedgram.core.data.local.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    var postId: String?,
    var postimageUrl: String?,
    var postCaption: String?
): Parcelable