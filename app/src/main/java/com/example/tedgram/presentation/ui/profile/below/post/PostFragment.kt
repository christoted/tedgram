package com.example.tedgram.presentation.ui.profile.below.post

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.databinding.FragmentPost2Binding
import com.example.tedgram.databinding.ItemPostSendiriBinding
import com.example.tedgram.presentation.ui.profile.below.post.adapter.PostAdapter
import com.example.tedgram.presentation.ui.profile.below.post.adapter.PostRealTimeAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PostFragment : Fragment(), OnItemClicked {

    private var _binding: FragmentPost2Binding? = null
    val binding get() = _binding

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var postAdapter: PostAdapter? = null

    private lateinit var listPost: ArrayList<Post>

    private lateinit var firestoreRecyclerOptions: FirestoreRecyclerOptions<Post>
    private lateinit var postRealTimeAdapter: PostRealTimeAdapter

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

        postAdapter = PostAdapter(this)

        binding?.progressBar?.visibility = View.VISIBLE

        //fetchPost(mAuth?.currentUser!!.uid)

        getAllPostRealTime(mAuth?.currentUser!!.uid)
    }

    private fun getAllPostRealTime(currentId: String) {
        val query: Query = db!!.collection("content").document(currentId).collection("post")
        firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build()

        binding?.progressBar?.visibility = View.GONE

        postRealTimeAdapter = PostRealTimeAdapter(firestoreRecyclerOptions, db!!, mAuth!!, this)

        binding?.rvPost?.layoutManager = GridLayoutManager(context, 3)
        binding?.rvPost?.adapter = postRealTimeAdapter
    }


    override fun onDetach() {
        super.onDetach()
        _binding = null
        postRealTimeAdapter.stopListening()
    }

    override fun onItemClicked(position: Int, itemPostSendiriBinding: ItemPostSendiriBinding) {
        val popUpMenu = PopupMenu(activity, itemPostSendiriBinding.root)
        popUpMenu.inflate(R.menu.menu_delete)
        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener {
            it
            when (it.itemId) {
                R.id.delete -> {
                    deletePost(position)
                }
            }
            true
        }
    }

    private fun deletePost(position: Int) {

        val listDocumentIdPostUser: ArrayList<String> = ArrayList()

        db?.collection("content")?.document(mAuth?.currentUser!!.uid)?.collection("post")?.get()
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val res = it.result

                    val idToDelete: String?

                    if (res != null) {
                        for (i in res) {
                            listDocumentIdPostUser.add(i.id)
                        }
                        idToDelete = listDocumentIdPostUser[position]

                        db?.collection("content")?.document(mAuth?.currentUser!!.uid)
                            ?.collection("post")?.document(listDocumentIdPostUser[position])
                            ?.delete()

                        db?.collection("allcontent")?.document(idToDelete)?.delete()
                    }

                }
            }
    }

    override fun onStart() {
        super.onStart()

        postRealTimeAdapter.startListening()
    }

}