package com.example.tedgram.presentation.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tedgram.R
import com.example.tedgram.databinding.FragmentNotificationBinding
import com.example.tedgram.presentation.ui.notification.adapter.NotificationAdapter
import com.example.tedgram.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class NotificationFragment : Fragment(), OnButtonClicked {

    private var binding: FragmentNotificationBinding? = null
    val _binding get() = binding

    private lateinit var notificationViewModel: NotificationViewModel

    private lateinit var mAuth: FirebaseAuth

    private lateinit var db: FirebaseFirestore

    private lateinit var listUserId: ArrayList<String>

    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity == null) {
            return
        }

        listUserId = ArrayList()

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val viewModelFactory = ViewModelFactory.getInstance(activity!!)
        notificationViewModel =
            ViewModelProvider(this, viewModelFactory)[NotificationViewModel::class.java]

        notificationViewModel.followUpdateLiveData

        notificationAdapter = NotificationAdapter(mAuth, db, this)

        setupRV()

        notificationViewModel.getAllUpdate(mAuth.currentUser.uid)
            .observe(viewLifecycleOwner, Observer {
                listUserId.addAll(it)
                notificationAdapter.setListId(it)

                binding?.rvNotification?.adapter = notificationAdapter
            })
    }

    private fun setupRV() {
        binding?.rvNotification?.layoutManager = LinearLayoutManager(activity)
    }

    override fun onDetach() {
        super.onDetach()
        binding = null
    }

    override fun onButtonFollowClicked(position: Int, isFollow: Boolean) {
        if (isFollow) {
            //Following -> current User Follow user B
            val following: MutableMap<String, Any> = HashMap()
            following["following"] = notificationAdapter.ids[position]

            val userIdToFollow = notificationAdapter.ids[position]

            db.collection("follow").document(mAuth.currentUser!!.uid).collection("following")
                .document(userIdToFollow).set(
                    following
                )

            //Follower -> user B followed by current User
            val follower: MutableMap<String, Any> = HashMap()
            follower["follower"] = mAuth.currentUser!!.uid

            val currentUserThatFollow = mAuth.currentUser!!.uid
            db.collection("follow").document(notificationAdapter.ids[position])
                .collection("follower").document(currentUserThatFollow).set(
                    follower
                )
        } else {
            removeFollowing(mAuth.currentUser!!.uid, position)
            removeFollower(mAuth.currentUser!!.uid, position)
        }
    }

    private fun removeFollowing(currentUserId: String, position: Int) {
        val userIdToFollow = notificationAdapter.ids[position]
        db.collection("follow").document(currentUserId).collection("following")
            .document(userIdToFollow).delete()
    }

    private fun removeFollower(currentUserId: String, position: Int) {
        db.collection("follow").document(notificationAdapter.ids[position]).collection("follower")
            .document(currentUserId).delete()
    }

}