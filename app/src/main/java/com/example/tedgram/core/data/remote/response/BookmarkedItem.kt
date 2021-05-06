package com.example.tedgram.core.data.remote.response

data class BookmarkedItem(
    var postCaption: String ?= "",
    var postId: String? = "",
    var postURL: String? = "",
    var userId: String? = "",
    var userImageURL: String? = "",
    var username: String? = ""

)