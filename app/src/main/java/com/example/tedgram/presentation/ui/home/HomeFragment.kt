package com.example.tedgram.presentation.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.databinding.FragmentHomeBinding
import com.example.tedgram.presentation.ui.home.adapter.PostAdapter
import com.example.tedgram.presentation.ui.post.PostFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding ?= null
    private val binding get() = _binding

    private var mAuth: FirebaseAuth ?= null
    private var db: FirebaseFirestore ?= null

    companion object {
        val URI_KEY = "uri"
        val CAPTION_KEY = "caption"
    }

    private lateinit var postAdapter: PostAdapter



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding?.progressBar?.visibility = View.VISIBLE

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        fetchUser(mAuth?.currentUser!!.uid)

        fetchAllPost()

    }

    private fun fetchUser(currentUserId: String) {
        db?.collection("users")?.document(currentUserId)?.get()?.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val result = task.result
                val imageURL: String = result?.get("imageUrl").toString()
                val username: String = result?.get("username").toString()

                Log.d("TAG", "fetchUser: $imageURL && $username")

                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                with(sharedPref?.edit()) {
                    this?.putString(URI_KEY, imageURL)
                    this?.putString(CAPTION_KEY, username)
                    this?.apply()
                }

            }

        }?.addOnFailureListener {
            Log.d("FAILED", "onFailed $it ")
        }
    }


    private fun fetchAllPost () {
        val query: Query = db!!.collection("allcontent")
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Post> = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build()

        postAdapter = PostAdapter(firestoreRecyclerOptions)

        binding?.recyclerViewHome?.layoutManager = LinearLayoutManager(activity)
        binding?.recyclerViewHome?.adapter = postAdapter

        binding?.progressBar?.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
        postAdapter.stopListening()
    }

    override fun onStart() {
        super.onStart()
        postAdapter.startListening()
    }


}