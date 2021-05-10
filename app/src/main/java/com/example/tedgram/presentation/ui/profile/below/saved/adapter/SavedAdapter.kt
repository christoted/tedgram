package com.example.tedgram.presentation.ui.profile.below.saved.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.databinding.ItemPostSendiriBinding
import com.example.tedgram.presentation.ui.profile.below.saved.OnItemClicked

class SavedAdapter(val onItemClicked: OnItemClicked) : RecyclerView.Adapter<SavedAdapter.SavedViewHolder>() {

    val listPost = ArrayList<Post>()

    fun setPost(posts: List<Post>){
        listPost.clear()
        listPost.addAll(posts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val itemSavedBinding = ItemPostSendiriBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SavedViewHolder(itemSavedBinding)
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
       holder.bind(listPost[position])
       holder.onItemBookmarkedClicked()
    }

    override fun getItemCount(): Int = listPost.size

    inner class SavedViewHolder(private val itemPostSendiriBinding: ItemPostSendiriBinding): RecyclerView.ViewHolder(itemPostSendiriBinding.root) {

        fun onItemBookmarkedClicked(){

            itemPostSendiriBinding.root.setOnLongClickListener {
                onItemClicked.onItemClicked(adapterPosition, itemPostSendiriBinding)
                true
            }
        }

        fun bind(post: Post) {
            with(itemPostSendiriBinding) {
                Glide.with(itemView.context)
                    .load(post.postURL)
                    .placeholder(R.color.chalman_pink)
                    .into(imageViewPost)

            }
        }

    }
}