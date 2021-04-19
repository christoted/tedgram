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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    companion object {
        val TAG = SearchFragment::class.java.simpleName
        val USERS = "users"
        val USERNAME = "username"
    }

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: SearchAdapter

    private lateinit var listUsers: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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

        searchAdapter = SearchAdapter()

        listUsers = ArrayList()
        binding.progressBar.visibility = View.VISIBLE
        
        getAllUser()
      //  searchUser("christoted")
    }

    fun getAllUser(): List<User> {
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

                        var userResult = User(user.id, fullName, username, email, password, bio, imageUrl)
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
        db!!.collection("users").whereEqualTo("username", query).get().addOnCompleteListener {


            if ( it.isSuccessful) {
                for ( document in it.result!!) {
                    Log.d("OYYY", "searchUser: result ${document.data}")
                    val email: String = document["email"].toString()
                    val bio: String = document?.get("bio").toString()
                    val fullName: String = document?.get("fullName").toString()
                    val imageUrl: String = document?.get("imageUrl").toString()
                    val password: String = document["password"].toString()
                    val username: String = document?.get("username").toString()

                    val userResult = User(document.id, fullName, username, email, password, bio, imageUrl)
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
                Toast.makeText(activity, "Submit Boy", Toast.LENGTH_SHORT).show()
                db!!.collection("users").whereArrayContains("username", "christoted").get().addOnCompleteListener {
                    Log.d("OYY", "searchUser: ON create options dalam DB")
                    for ( document in it.result!!) {
                        Log.d("OYY", "searchUser dalam result: DUAR")
                    }
                }
                searchUser(query ?: "teddy")
                Log.d("OYY", "searchUser: ON create options menu")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

    }


    override fun onDetach() {
        super.onDetach()
        _binding = null
    }


}