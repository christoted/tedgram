package com.example.tedgram.presentation.ui.profile.below.post

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.databinding.FragmentPost2Binding
import com.example.tedgram.presentation.ui.profile.below.post.adapter.PostAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostFragment : Fragment() {

    private var _binding: FragmentPost2Binding ?= null
    val binding get() = _binding

    private var mAuth: FirebaseAuth ?= null
    private var db: FirebaseFirestore ?= null
    private var postAdapter: PostAdapter ?= null

    private lateinit var listPost: ArrayList<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPost2Binding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        listPost = ArrayList()

        postAdapter = PostAdapter()

        binding?.progressBar?.visibility = View.VISIBLE

        fetchPost(mAuth?.currentUser!!.uid)
    }

    private fun fetchPost(currentId: String){
        db?.collection("content")?.document(currentId)?.collection("post")?.addSnapshotListener { value, error ->
            if (value != null ) {
                val results = value.documents

                for ( result in results) {
                    val res = result.data
                    val postCaption = res?.get("postCaption").toString()
                    val postId = res?.get("postId").toString()
                    val postURL = res?.get("postURL").toString()
                    val userId = res?.get("userId").toString()
                    val userImageURL = res?.get("userImageURL").toString()
                    val username = res?.get("username").toString()

                    val post = Post(postId, postURL, postCaption, userImageURL, username, userId)
                    listPost.add(post)


                    Log.d("SIZEE", "fetchPost: ${listPost.size}")

                    postAdapter?.setData(listPost)

                    binding?.rvPost?.layoutManager = GridLayoutManager(context, 3)
                    binding?.rvPost?.adapter = postAdapter
                }

                binding?.progressBar?.visibility = View.GONE
            }

        }
//
//        {
//            if ( it.isSuccessful) {
//                val results = it.result
//                if (results != null) {
//                    for ( i in results) {
//
//                        val res = i.data
//                        val postCaption = res["postCaption"].toString()
//                        val postId = res["postId"].toString()
//                        val postURL = res["postURL"].toString()
//                        val userId = res["userId"].toString()
//                        val userImageURL = res["userImageURL"].toString()
//                        val username = res["username"].toString()
//
//                        val post = Post(postId, postURL, postCaption, userImageURL, username, userId)
//                        listPost.add(post)
//
//
//                        Log.d("SIZEE", "fetchPost: ${listPost.size}")
//
//                        postAdapter?.setData(listPost)
//
//                        binding?.rvPost?.layoutManager = GridLayoutManager(context, 3)
//                        binding?.rvPost?.adapter = postAdapter
//                    }
//                }
//
//                binding?.progressBar?.visibility = View.GONE
//            } //      }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}