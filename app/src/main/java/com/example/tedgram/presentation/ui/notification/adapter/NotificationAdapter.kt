package com.example.tedgram.presentation.ui.notification.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tedgram.core.data.local.entity.User
import com.example.tedgram.databinding.ItemNotificationBinding
import com.example.tedgram.presentation.ui.notification.OnButtonClicked
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class NotificationAdapter(private val db: FirebaseFirestore, private val onButtonClicked: OnButtonClicked): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>()  {

    var ids: ArrayList<String> = ArrayList()

    var isFollowing: Boolean = false

    fun setListId(listId: List<String>) {
        ids.clear()
        ids.addAll(listId)
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemNotificationBinding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return NotificationViewHolder(itemNotificationBinding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val data = ids[position]
        holder.retrieveByUserId(data)
    }

    override fun getItemCount(): Int = ids.size


    inner class NotificationViewHolder(private val itemNotificationBinding: ItemNotificationBinding): RecyclerView.ViewHolder(itemNotificationBinding.root) {

        fun retrieveByUserId(userId: String){
            db.collection("users").document(userId).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result
                    val imageUrl = data?.get("imageUrl")
                    val usernameFirestore: String = data?.get("username") as String

                    with(itemNotificationBinding) {
                        Glide.with(itemView.context)
                            .load(imageUrl)
                            .into(imageView)

                        username.text = usernameFirestore + "started following you, Do you know?"

                        buttonFollow.setOnClickListener {
                            isFollowing = !isFollowing
                            setFollowState(isFollowing)
                            onButtonClicked.onButtonFollowClicked(adapterPosition, isFollowing)
                        }
                    }
                }
            }
        }

        private fun setFollowState(isFollowing: Boolean) {
            if (isFollowing) {
                itemNotificationBinding.buttonFollow.text = "UnFollow"
            } else {
                itemNotificationBinding.buttonFollow.text = "Follow"
            }
        }


    }



}