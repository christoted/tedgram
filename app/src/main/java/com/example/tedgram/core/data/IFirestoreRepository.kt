package com.example.tedgram.core.data

import android.content.SharedPreferences
import android.net.Uri
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.core.data.local.entity.User
import com.example.tedgram.databinding.ActivityRegisterBinding
import com.example.tedgram.databinding.FragmentPostBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

interface IFirestoreRepository {

    /* Authentication */
    fun registerUser(email: String, password: String, username: String, binding: ActivityRegisterBinding)

    fun loginUser(email: String, password: String): Boolean

    /* Home */
    fun fetchAllPost(): CollectionReference

    fun getPostUserId(postUserId: String): DocumentReference

    /* Search */
    fun getAllUser(): CollectionReference

    fun followUser(currentId: String): CollectionReference

    fun unFollowUser(userIdToUnFollow: String): CollectionReference

    /* Post */
    fun postContent(post: Post, imageUri: Uri, postId: Int, binding: FragmentPostBinding)

    /* Notification */

    /* Profile */
    fun getProfileUser(currentId: String) : CollectionReference

    fun getTotalFollower(currentId: String) : CollectionReference

    fun getTotalFollowing(currentId: String) : CollectionReference

    fun postUpdateEditProfile(bio: String?, fullName: String?, username: String?, uri: Uri?, sharedPreferences: SharedPreferences)

    fun showBookmark() : CollectionReference

    fun deleteBookmark(listDocumentReference: ArrayList<String>, position: Int)

    fun showPost() : CollectionReference

    fun deletePost(listDocumentReference: ArrayList<String> ,position: Int)




}