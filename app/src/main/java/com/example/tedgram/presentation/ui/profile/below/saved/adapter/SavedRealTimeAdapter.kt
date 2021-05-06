package com.example.tedgram.presentation.ui.profile.below.saved.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.core.data.remote.response.BookmarkedResponse
import com.example.tedgram.databinding.ItemHomeBinding
import com.example.tedgram.databinding.ItemPostSendiriBinding
import com.example.tedgram.presentation.ui.profile.below.saved.OnItemClicked
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SavedRealTimeAdapter(
    options: FirestoreRecyclerOptions<Post>,
    val onItemClicked: OnItemClicked,
    val db: FirebaseFirestore,
    val mAuth: FirebaseAuth
) : FirestoreRecyclerAdapter<Post, SavedRealTimeAdapter.SavedRealTimeViewHolder>(
    options
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRealTimeViewHolder {
        val itemPostSendiriBinding =
            ItemPostSendiriBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SavedRealTimeViewHolder(itemPostSendiriBinding)
    }

    override fun onBindViewHolder(
        holder: SavedRealTimeViewHolder,
        position: Int,
        model: Post
    ) {
        holder.bind(model, position)
        holder.onItemClicked()
    }


    inner class SavedRealTimeViewHolder(private val itemPostSendiriBinding: ItemPostSendiriBinding) :
        RecyclerView.ViewHolder(itemPostSendiriBinding.root) {

        fun onItemClicked(){
            itemPostSendiriBinding.root.setOnLongClickListener {
                onItemClicked.onItemClicked(adapterPosition, itemPostSendiriBinding)
                true
            }
        }


        fun bind(model: Post, position: Int) {
            db.collection("bookmark").document(mAuth.currentUser!!.uid).collection("bookmarked")
                .addSnapshotListener { value, error ->

                    with(itemPostSendiriBinding) {
                        Glide.with(itemView.context)
                            .load(model.postURL)
                            .placeholder(R.color.chalman_pink)
                            .into(imageViewPost)
                    }
                }


        }

    }
}