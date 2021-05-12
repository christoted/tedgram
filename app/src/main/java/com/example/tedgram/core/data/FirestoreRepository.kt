package com.example.tedgram.core.data

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.View
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.core.data.local.entity.User
import com.example.tedgram.databinding.ActivityRegisterBinding
import com.example.tedgram.databinding.FragmentPostBinding
import com.example.tedgram.presentation.ui.profile.edit.EditProfileActivity
import com.example.tedgram.util.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreRepository(
    private var mAuth: FirebaseAuth,
    private var db: FirebaseFirestore,
    private var storage: FirebaseStorage
) :
    IFirestoreRepository {

    companion object {
        @Volatile
        private var instance: FirestoreRepository? = null
        fun getInstance(mAuth: FirebaseAuth, db: FirebaseFirestore, storage: FirebaseStorage) =
            instance ?: synchronized(this) {
                instance ?: FirestoreRepository(mAuth, db, storage)
            }
    }


//    fun registerNewUser(email: String, password: String, username: String, binding: ActivityRegisterBinding){
//
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task->
//            if ( task.isSuccessful ) {
//                val user = mAuth.currentUser
//                val userModel =
//                    User(
//                        user.uid,
//                        "",
//                        username,
//                        email,
//                        password,
//                        "",
//                        "",
//                        false
//                    )
//                addToFirestore(userModel)
//                binding.progressBar.visibility = View.GONE
//            } else {
//                binding.progressBar.visibility = View.GONE
//            }
//        }
//    }


    override fun registerUser(
        email: String,
        password: String,
        username: String,
        binding: ActivityRegisterBinding
    ) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
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

    private fun addToFirestore(user: User) {
        val userMap: MutableMap<String, Any> = HashMap()
        userMap["user"] = user

        db.collection("users").document(user.userId).set(
            user
        )
    }


    override fun loginUser(email: String, password: String): Boolean {

        var isSuccess = false
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                isSuccess = true
            }
        }.addOnSuccessListener {
            isSuccess = true

        }

        return isSuccess
    }

    override fun fetchAllPost(): CollectionReference {

        return db.collection("allcontent")
    }

    override fun getPostUserId(postUserId: String): DocumentReference {

        return db.collection("users").document(postUserId)
    }

    override fun getAllUser(): CollectionReference {

        return db.collection("users")
    }

    override fun followUser(currentId: String): CollectionReference {
        return db.collection("follow").document(currentId).collection("following")
    }

    override fun unFollowUser(userIdToUnFollow: String): CollectionReference {
        return db.collection("follow").document(userIdToUnFollow).collection("follower")
    }

    override fun postContent(post: Post, imageUri: Uri, postId: Int, binding: FragmentPostBinding) {
        val userId = mAuth?.currentUser?.uid


        val storageRef: StorageReference = storage.reference!!.child(userId!! + Constant.POSTALL)

        storageRef.putFile(imageUri).addOnSuccessListener {

            val downloadURL = it.storage.downloadUrl

            downloadURL.addOnCompleteListener { task ->

                val imagePath = task.result.toString()

                val postMap: MutableMap<String, Any> = HashMap()
                postMap["postId"] = post.postId.toString()
                postMap["postURL"] = imagePath
                postMap["postCaption"] = post.postCaption.toString()
                postMap["username"] = post.username.toString()
                postMap["userImageURL"] = post.userImageURL.toString()
                postMap["userId"] = post.userId.toString()


                db?.collection("allcontent")?.document(postId.toString())?.set(
                    postMap
                )?.addOnCompleteListener {
                    // Toast.makeText(activity, "Posted!", Toast.LENGTH_SHORT).show()
                    binding?.progressBar?.visibility = View.GONE
                }?.addOnFailureListener {
                    //  Toast.makeText(activity, "Some error occurred!", Toast.LENGTH_SHORT).show()
                    binding?.progressBar?.visibility = View.GONE
                }?.addOnSuccessListener {
                    binding?.progressBar?.visibility = View.GONE
                    binding?.imageView?.setImageURI(null)
                    binding?.etCaption?.setText("")
                }


                db?.collection("content").document(userId)?.collection("post")
                    ?.document(postId.toString())?.set(
                        postMap
                    )?.addOnCompleteListener {
                        //   Toast.makeText(activity, "Posted!", Toast.LENGTH_SHORT).show()
                        binding?.progressBar?.visibility = View.GONE
                    }?.addOnFailureListener {
                        //   Toast.makeText(activity, "Some error occurred!", Toast.LENGTH_SHORT).show()
                        binding?.progressBar?.visibility = View.GONE
                    }?.addOnSuccessListener {
                        binding?.progressBar?.visibility = View.GONE
                        binding?.imageView?.setImageURI(null)
                        binding?.etCaption?.setText("")
                    }


            }.addOnFailureListener {
                Log.d("TAG", "addPost: FAILED")
                binding?.progressBar?.visibility = View.GONE
            }


        }.addOnCompleteListener {
            binding?.progressBar?.visibility = View.GONE

        }
    }

    //Notification
    override fun getAllUpdate(currentId: String): CollectionReference {
        return db.collection("follow").document(currentId).collection("follower")
    }

    override fun getProfileUser(currentId: String): CollectionReference {

        return db?.collection("users")
    }

    override fun getTotalFollower(currentId: String): CollectionReference {

        return db?.collection("follow")
    }

    override fun getTotalFollowing(currentId: String): CollectionReference {

        return db?.collection("follow")
    }

    override fun postUpdateEditProfile(
        bio: String?,
        fullName: String?,
        username: String?,
        uri: Uri?,
        sharedPreferences: SharedPreferences
    ) {
        val storageRef: StorageReference = storage?.reference!!.child(mAuth?.uid + Constant.PATH)
        var imagePath: String? = null

        if (uri != null) {
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    val downloadUrl = it.storage.downloadUrl
                    downloadUrl.addOnCompleteListener { task ->
                        imagePath = task.result.toString()

                        val userRef = db!!.collection("users").document(mAuth?.currentUser!!.uid)
                        userRef.update(
                            mapOf(
                                "bio" to bio,
                                "fullName" to fullName,
                                "username" to username,
                                "imageUrl" to imagePath

                            )
                        )
                            .addOnSuccessListener {
                                with(sharedPreferences.edit()) {
                                    this?.putString(
                                        com.example.tedgram.presentation.ui.home.HomeFragment.URI_KEY,
                                        imagePath
                                    )
                                    this?.putString(
                                        com.example.tedgram.presentation.ui.home.HomeFragment.CAPTION_KEY,
                                        username
                                    )
                                    this?.apply()

                                }
                                Log.d(EditProfileActivity.TAG, "updateUserProfile: Success")
                            }.addOnFailureListener {
                                Log.d(EditProfileActivity.TAG, "updateUserProfile: Failed")
                            }

                    }
                }
                .addOnFailureListener {

                }
        }
    }

    override fun showBookmark(): CollectionReference {
        return db.collection("bookmark").document(mAuth.currentUser!!.uid).collection("bookmarked")
    }

    override fun deleteBookmark(listDocumentReference: ArrayList<String>, position: Int) {
        db?.collection("bookmark")?.document(mAuth?.currentUser!!.uid)?.collection("bookmarked")
            ?.get()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val res = it.result

                    if (res != null) {
                        for (i in res) {
                            listDocumentReference.add(i.id)
                        }

                        db?.collection("bookmark")?.document(mAuth?.currentUser!!.uid)
                            ?.collection("bookmarked")?.document(listDocumentReference[position])
                            ?.delete()
                    }
                }
            }
    }

    override fun showPost(): CollectionReference {
        return db.collection("content").document(mAuth.currentUser!!.uid).collection("post")
    }

    override fun deletePost(listDocumentReference: ArrayList<String>, position: Int) {
        db?.collection("content")?.document(mAuth?.currentUser!!.uid)?.collection("post")?.get()
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val res = it.result

                    val idToDelete: String?

                    if (res != null) {
                        for (i in res) {
                            listDocumentReference.add(i.id)
                        }
                        idToDelete = listDocumentReference[position]

                        db?.collection("content")?.document(mAuth?.currentUser!!.uid)
                            ?.collection("post")?.document(listDocumentReference[position])
                            ?.delete()

                        db?.collection("allcontent")?.document(idToDelete)?.delete()
                    }

                }
            }
    }
}

