package com.example.tedgram.presentation.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.User
import com.example.tedgram.databinding.ItemSearchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchAdapter(
   private var onButtonFollowClicked: OnButtonFollowClicked

): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    var listUser = ArrayList<User>()
    private var isFollowing: Boolean = false



    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun setData(users: List<User>) {
        listUser.clear()
        listUser.addAll(users)
        notifyDataSetChanged()
    }

    fun getData(): List<User> {
        return listUser
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemSearchBinding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(itemSearchBinding)
    }

    override fun getItemCount(): Int {

        return getData().size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = getData()[position]
        holder.bind(user)
    }

    private fun populateListFollowing(currentUserId: String): List<String> {

        val listFollowing = ArrayList<String>()

        db.collection("follow").document(currentUserId).collection("following").get().addOnCompleteListener { task ->
            if ( task.isSuccessful) {
                for ( document in task.result!!) {
                    Log.d("FOLLOWING", "populateListFollowing: ${document.data}")
                    val userId: String = document["following"].toString()

                    Log.d("USER ID", "populateListFollowing: $userId")

                    listFollowing.add(userId)

                    Log.d("FOLLOWING", "populateListFollowing: ukuran ${listFollowing.size} ")
                }
            }
        }

        Log.d("FOLLOWING", "populateListFollowing: ukuran ${listFollowing.size} ")

        return listFollowing
    }


    inner class SearchViewHolder(private val itemSearchBinding: ItemSearchBinding) :
        RecyclerView.ViewHolder(itemSearchBinding.root) {

        fun bind(user: User) {
            val listFollowing = ArrayList<String>()
            //Validate User Follow
            db.collection("follow").document(mAuth.currentUser.uid).collection("following").get().addOnCompleteListener { task ->
                if ( task.isSuccessful) {
                    for ( document in task.result!!) {
                        Log.d("FOLLOWING", "populateListFollowing: ${document.data}")
                        val userId: String = document["following"].toString()
                        listFollowing.add(userId)

                        listFollowing.forEach { id ->
                            if ( id == user.userId ) {
                                Log.d("BOOL", "setFollowState: ATAS 1 $isFollowing")
                                setFollowState(!isFollowing)
                                Log.d("BOOL", "setFollowState: ATAS 2 $isFollowing")
                            }
                        }

                        if ( user.userId == mAuth.currentUser?.uid) {
                            itemSearchBinding.buttonFollow.visibility = View.GONE
                        } else {
                            itemSearchBinding.buttonFollow.visibility = View.VISIBLE
                            itemSearchBinding.buttonFollow.cornerRadius = 8


                            itemSearchBinding.buttonFollow.setOnClickListener {
                                isFollowing = !isFollowing
                                setFollowState(isFollowing)
                                onButtonFollowClicked.onButtonFollowClicked(adapterPosition, isFollowing)
                            }

                        }

                    }
                }
            }

            with(itemSearchBinding) {
                Glide.with(itemView.context)
                    .load(user.imageUrl)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(idImageView)


                tvName.text = user.username

                if ( user.userId == mAuth.currentUser?.uid) {
                    buttonFollow.visibility = View.GONE
                } else {
                    buttonFollow.visibility = View.VISIBLE
                    buttonFollow.cornerRadius = 8


                    buttonFollow.setOnClickListener {
                        isFollowing = !isFollowing
                        setFollowState(isFollowing)
                        onButtonFollowClicked.onButtonFollowClicked(adapterPosition, isFollowing)
                    }

                }
            }
        }

        private fun setFollowState(isFollowing: Boolean) {
            Log.d("BOOL", "setFollowState: LUAR $isFollowing")
            if (isFollowing) {
                itemSearchBinding.buttonFollow.text = "UnFollow"
                Log.d("BOOL", "setFollowState: ATAS$isFollowing")
            } else {
                itemSearchBinding.buttonFollow.text = "Follow"
                Log.d("BOOL", "setFollowState: BAWAG $isFollowing")
            }
        }

    }



}