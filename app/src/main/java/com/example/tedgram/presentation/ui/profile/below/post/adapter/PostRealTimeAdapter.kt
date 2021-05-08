package com.example.tedgram.presentation.ui.profile.below.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.databinding.ItemPostSendiriBinding
import com.example.tedgram.presentation.ui.profile.below.post.OnItemClicked
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostRealTimeAdapter(
    options: FirestoreRecyclerOptions<Post>,
    val db: FirebaseFirestore,
    val mAuth: FirebaseAuth,
    val onItemClicked: OnItemClicked
) : FirestoreRecyclerAdapter<Post, PostRealTimeAdapter.PostRealTimeViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostRealTimeViewHolder {
        val itemPostSendiriBinding =
            ItemPostSendiriBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PostRealTimeViewHolder(itemPostSendiriBinding)
    }

    override fun onBindViewHolder(holder: PostRealTimeViewHolder, position: Int, model: Post) {
        holder.bind(model)
        holder.onItemClicked()
    }


    inner class PostRealTimeViewHolder(val itemPostSendiriBinding: ItemPostSendiriBinding) :
        RecyclerView.ViewHolder(itemPostSendiriBinding.root) {

        fun onItemClicked(){
            itemPostSendiriBinding.root.setOnLongClickListener {
                onItemClicked.onItemClicked(adapterPosition, itemPostSendiriBinding)
                true
            }
        }

        fun bind(post: Post) {
            db.collection("content").document(mAuth.currentUser!!.uid).collection("post").addSnapshotListener { value, error ->


                    with(itemPostSendiriBinding) {
                        Glide.with(itemView.context)
                            .load(post.postURL)
                            .placeholder(R.color.chalman_pink)
                            .into(imageViewPost)


                }
            }
        }
    }
}