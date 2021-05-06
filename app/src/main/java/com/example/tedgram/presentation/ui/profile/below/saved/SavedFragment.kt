package com.example.tedgram.presentation.ui.profile.below.saved

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.core.data.remote.response.BookmarkedResponse
import com.example.tedgram.databinding.FragmentSavedBinding
import com.example.tedgram.databinding.ItemPostSendiriBinding
import com.example.tedgram.presentation.ui.home.adapter.PostAdapter
import com.example.tedgram.presentation.ui.profile.below.saved.adapter.SavedAdapter
import com.example.tedgram.presentation.ui.profile.below.saved.adapter.SavedRealTimeAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.json.JSONObject


class SavedFragment : Fragment(), OnItemClicked {

    private var _binding: FragmentSavedBinding ?= null
    private val binding get() = _binding!!

    private var db:FirebaseFirestore ?= null
    private var mAuth: FirebaseAuth ?= null

    private lateinit var adapter: SavedAdapter

    private lateinit var listPost: ArrayList<Post>

    private lateinit var listDocumentReference: ArrayList<String>

    private lateinit var firestoreRecyclerOptions: FirestoreRecyclerOptions<Post>

    private lateinit var savedRealTimeAdapter: SavedRealTimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSavedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        adapter = SavedAdapter(this)

        listPost = ArrayList()
        setupRV()
       // getAllBookmarked()

        getAllBookmarkedRealTime()

        listDocumentReference = ArrayList()

      //  binding.progressBar.visibility = View.VISIBLE


    }

    private fun setupRV(){
        binding.rvPost.layoutManager = GridLayoutManager(context, 3)
    }

    private fun getAllBookmarked() {
        db?.collection("bookmark")?.document(mAuth?.currentUser!!.uid)?.collection("bookmarked")?.get()?.addOnCompleteListener {
            if ( it.isSuccessful) {
                val res = it.result
                Log.d("TEST", "getAllBookmarked: $res")
                if (res != null) {
                    for ( i in res) {

                        val data = i.data
                        val postCaption = data["postCaption"] as String
                        val postId = data["postId"] as String
                        val postURL = data["postURL"] as String
                        val userId = data["userId"] as String
                        val userImageURL = data["userImageURL"] as String
                        val username = data["username"] as String

                        val post = Post(postId, postURL, postCaption, userImageURL, username, userId)

                        listPost.add(post)
                        adapter.setPost(listPost)
                        binding.rvPost.adapter = adapter
                    }
                }

                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun getAllBookmarkedRealTime(){
      //  val query: Query = db!!.collection("allcontent")
        val query: Query = db!!.collection("bookmark").document(mAuth?.currentUser!!.uid).collection("bookmarked")
        firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build()

        binding.progressBar.visibility = View.GONE

        savedRealTimeAdapter = SavedRealTimeAdapter(firestoreRecyclerOptions, this, db!!, mAuth!!)

        binding.rvPost.adapter = savedRealTimeAdapter


    }

    override fun onDetach() {
        super.onDetach()
        _binding = null

        savedRealTimeAdapter.stopListening()
    }

    override fun onStart() {
        super.onStart()

        savedRealTimeAdapter.startListening()
    }

    override fun onItemClicked(position: Int, itemPostSendiriBinding: ItemPostSendiriBinding) {
        Log.d("TESt", "onItemClicked: POSISI KE $position")
       // val post = adapter.listPost[position]
        val popupMenu = PopupMenu(activity, itemPostSendiriBinding.root)
        popupMenu.inflate(R.menu.menu_delete)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {  menuItem ->
            when(menuItem.itemId) {
                R.id.delete -> {
                    Log.d("TEST", "onItemClicked: CLICKED")
                    deleteBookmarked(position)
                    adapter.notifyItemRemoved(position)
                   // adapter.notifyDataSetChanged()
                }
            }
            true
        }

    }

    private fun deleteBookmarked(position: Int){
        //kasi logic buat delete nya ez
        db?.collection("bookmark")?.document(mAuth?.currentUser!!.uid)?.collection("bookmarked")?.get()?.addOnCompleteListener {
            if ( it.isSuccessful) {
                val res = it.result

                if (res != null) {
                    for ( i in res) {
                        listDocumentReference.add(i.id)
                    }

                    db?.collection("bookmark")?.document(mAuth?.currentUser!!.uid)?.collection("bookmarked")?.document(listDocumentReference[position])?.delete()
                }
            }
        }

    }

}