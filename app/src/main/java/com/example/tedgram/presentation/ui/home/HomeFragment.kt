package com.example.tedgram.presentation.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot


class HomeFragment : Fragment(), OnBookmarkClicked {

    private var _binding: FragmentHomeBinding ?= null
    private val binding get() = _binding

    private var mAuth: FirebaseAuth ?= null
    private var db: FirebaseFirestore ?= null

    private lateinit var listPostBookmarked: ArrayList<Post>

    companion object {
        val URI_KEY = "uri"
        val CAPTION_KEY = "caption"
    }

    private lateinit var firestoreRecyclerOptions: FirestoreRecyclerOptions<Post>

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listPostBookmarked = ArrayList()

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
        firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build()

        postAdapter = PostAdapter(firestoreRecyclerOptions, mAuth, db, this)

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

    override fun onBookmarkedClicked(Position: Int, bookmarkedState: Boolean) {

        val postBookmarked: MutableMap<String, Any> = HashMap()

        //get all post reference
        val listDocumentReference = ArrayList<String>()
        db?.collection("allcontent")!!.get().addOnCompleteListener {
            if ( it.isSuccessful) {

                val res = it.result

                if (res != null) {
                    for ( i in res) {
                        listDocumentReference.add(i.id)
                    }

                    val result = firestoreRecyclerOptions.snapshots[Position]
                    val post = Post(result.postId, result.postURL, result.postCaption, result.userImageURL, result.username, result.userId)

                    postBookmarked["bookmarked"] = post

                    Log.d("TEST", "onBookmarkedClicked: ${postBookmarked["bookmarked"]}")

                    if(bookmarkedState) {
                        db?.collection("bookmark")?.document(mAuth?.currentUser!!.uid)?.collection("bookmarked")?.document(listDocumentReference[Position])?.set(
                            post
                        )?.addOnCompleteListener {
                            Toast.makeText(activity, "Saved!", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        removePost(mAuth?.currentUser!!.uid, listDocumentReference[Position])
                    }

                }
            }
        }
    }

    private fun removePost(currentUserId: String, documentId: String) {
        db?.collection("bookmark")?.document(currentUserId)?.collection("bookmarked")?.document(documentId)?.delete()
    }

}