package com.example.tedgram.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.tedgram.R
import com.example.tedgram.databinding.FragmentProfileNewBinding
import com.example.tedgram.presentation.ui.profile.edit.EditProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileNewBinding? = null
    private val binding get() = _binding!!

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileNewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity == null) {
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.scrollView.isFillViewport = true

        val viewPagerAdapter = ViewPagerAdapter(this, childFragmentManager)

        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.btnProfile.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
        }

        fetchImage(mAuth?.currentUser!!.uid)
        fetchFollowing(mAuth?.currentUser!!.uid)
        fetchFollower(mAuth?.currentUser!!.uid)

    }

    private fun fetchImage(currentUserId: String) {
        db?.collection("users")?.document(currentUserId)?.get()?.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val result = task.result
                val imageURL: String = result?.get("imageUrl").toString()

                Glide.with(this)
                    .load(imageURL)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(binding.imageViewProfile)

                binding.progressBar.visibility = View.GONE
            }

        }?.addOnFailureListener {
            Log.d("FAILED", "onFailed $it ")
        }
    }

    private fun fetchFollowing(currentUserId: String) {
        db?.collection("follow")?.document(currentUserId)?.collection("following")?.get()
            ?.addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val listFollowing = ArrayList<String>()
                    for (document in task.result!!) {
                        val userId: String = document["following"].toString()

                        Log.d("USER ID", "fetchFollowing: $userId")
                        listFollowing.add(userId)

                    }

                    binding.following.text = "${listFollowing.size}"

                    binding.progressBar.visibility = View.GONE
                }


            }?.addOnFailureListener {
                Log.d("FAILED", "onFailed: $it")
            }
    }

    private fun fetchFollower(currentUserId: String) {
        db?.collection("follow")?.document(currentUserId)?.collection("follower")?.get()
            ?.addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val listFollower = ArrayList<String>()

                    for (document in task.result!!) {
                        val userId: String = document["follower"].toString()

                        Log.d("USER ID", "fetch Follower: $userId")

                        listFollower.add(userId)
                    }

                    binding.follower.text = "${listFollower.size}"
                    binding.progressBar.visibility = View.GONE
                }


            }?.addOnFailureListener {
                Log.d("FAILED", "onFailed: $it")
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}