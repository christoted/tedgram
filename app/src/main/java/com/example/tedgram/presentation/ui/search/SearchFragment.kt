package com.example.tedgram.presentation.ui.search

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tedgram.R
import com.example.tedgram.databinding.FragmentSearchBinding
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tedgram.core.data.local.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log


class SearchFragment : Fragment(), OnButtonFollowClicked {

    companion object {
        val TAG = SearchFragment::class.java.simpleName
        val USERS = "users"
        val USERNAME = "username"
    }

    private var isFollow = false
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: SearchAdapter

    private lateinit var listUsers: ArrayList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity == null) {
            return
        }
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        searchAdapter = SearchAdapter(this)

        listUsers = ArrayList()
        binding.progressBar.visibility = View.VISIBLE
        
        getAllUser()
      //  searchUser("christoted")
    }

    fun getAllUser(): List<User> {
        //Nanti waktu getAllUser bisa di validasi dlu sii, dari collection following, if ternyata sama yauda dibuat is Follwoing nya sama dengan unfollow
        binding.progressBar.visibility = View.VISIBLE
        val listUser = ArrayList<User>()
        db?.collection(USERS)
            ?.get()
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    for (user in it.result!!) {

                        val result = user.data
                        val email: String = result["email"].toString()
                        val bio: String = result?.get("bio").toString()
                        val fullName: String = result?.get("fullName").toString()
                        val imageUrl: String = result?.get("imageUrl").toString()
                        val password: String = result["password"].toString()
                        val username: String = result?.get("username").toString()
                        val isFollowing: Boolean = result["isFollowing"] as Boolean

                        var userResult = User(user.id, fullName, username, email, password, bio, imageUrl, isFollowing)
                        listUser.add(userResult)

                        searchAdapter.setData(listUser)

                        binding.recyclerView.layoutManager = LinearLayoutManager(context)
                        binding.recyclerView.adapter = searchAdapter

                    }
                    Log.d("LIST USER", "getAllUser: ${listUser.size}")
                    binding.progressBar.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }

        Log.d("LIST USER BAWAH", "getAllUser: ${listUser.size}")

        return listUser
    }

    fun searchUser(query: String){
        binding.progressBar.visibility = View.VISIBLE
        Log.d("OYY", "searchUser: result ATAS}")
        val listUser = ArrayList<User>()
        db!!.collection("users").whereGreaterThanOrEqualTo("username", query).get().addOnCompleteListener {


            if ( it.isSuccessful) {
                for ( document in it.result!!) {
                    Log.d("OYYY", "searchUser: result ${document.data}")
                    val email: String = document["email"].toString()
                    val bio: String = document?.get("bio").toString()
                    val fullName: String = document?.get("fullName").toString()
                    val imageUrl: String = document?.get("imageUrl").toString()
                    val password: String = document["password"].toString()
                    val username: String = document?.get("username").toString()
                    val isFollowing: Boolean = document["isFollowing"] as Boolean

                    val userResult = User(document.id, fullName, username, email, password, bio, imageUrl, isFollowing)
                    Log.d("OYYY", "searchUser dalam object: ${userResult}")
                    listUser.add(userResult)

                    searchAdapter.setData(listUser)
                    binding.recyclerView.layoutManager = LinearLayoutManager(context)
                    binding.recyclerView.adapter = searchAdapter

                }
                binding.progressBar.visibility = View.GONE
            } else if ( it.isCanceled) {
                Log.d("OYY", "searchUser: result cancel")
                binding.progressBar.visibility = View.GONE
            } else {
                Log.d("OYY", "searchUser: result gagal")
                binding.progressBar.visibility = View.GONE
            }
        }.addOnFailureListener {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val mSearch = menu.findItem(R.id.searchUp)
        val searchView = mSearch.actionView as SearchView
        searchView.queryHint = "Search User"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                Toast.makeText(activity, "Submit Boy", Toast.LENGTH_SHORT).show()
//                if (query?.isEmpty() == true) {
//                   getAllUser()
//                } else {
//                    searchUser(query ?: "teddy")
//                }
//
//
//                Log.d("OYY", "searchUser: ON create options menu")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText?.isEmpty() == true) {
                    getAllUser()
                } else {
                    searchUser(newText ?: "teddy")
                }

                return true

            }

        })

    }


    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

    override fun onButtonFollowClicked(position: Int, isFollowing: Boolean) {
        Log.d(TAG, "onButtonFollowClicked: at Search Fragment $position isFollowing $isFollowing")
        //Follow

        if (isFollowing) {
            //Following -> current User Follow user B
            val following: MutableMap<String, Any> = HashMap()
            following["following"] = searchAdapter.getData()[position].userId

            val userIdToFollow = searchAdapter.getData()[position].userId

            db!!.collection("follow").document(mAuth?.currentUser!!.uid).collection("following").document(userIdToFollow).set(
                following
            )

            //Follower -> user B followed by current User
            val follower: MutableMap<String, Any> = HashMap()
            follower["follower"] = mAuth?.currentUser!!.uid

            val currentUserThatFollow = mAuth?.currentUser!!.uid
            db!!.collection("follow").document(searchAdapter.getData()[position].userId).collection("follower").document(currentUserThatFollow).set(
                follower
            )

        } else {
            //remove Following
            removeFollowing(mAuth?.currentUser!!.uid, position)

            //remove Follower
            removeFollower(mAuth?.currentUser!!.uid, position)
        }


    }

    private fun removeFollowing(currentUserId: String, position: Int) {
        val userIdToFollow = searchAdapter.getData()[position].userId
        db!!.collection("follow").document(currentUserId).collection("following").document(userIdToFollow).delete()
    }

    private fun removeFollower(currentUserId: String, position: Int) {
        db!!.collection("follow").document(searchAdapter.getData()[position].userId).collection("follower").document(currentUserId).delete()
    }


}