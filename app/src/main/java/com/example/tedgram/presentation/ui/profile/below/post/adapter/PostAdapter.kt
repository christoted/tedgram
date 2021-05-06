package com.example.tedgram.presentation.ui.profile.below.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.databinding.ItemPostSendiriBinding

class PostAdapter(

) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var listPost = ArrayList<Post>()

    fun setData(posts: ArrayList<Post>) {
        listPost.clear()
        listPost.addAll(posts)
        notifyDataSetChanged()
    }

    fun getData(): List<Post> {

        return listPost
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemPostSendiriBinding =
            ItemPostSendiriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(itemPostSendiriBinding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = listPost[position]
        return holder.bind(post)
    }

    override fun getItemCount(): Int {

        return getData().size
    }

    inner class PostViewHolder(private val itemPostSendiriBinding: ItemPostSendiriBinding) :
        RecyclerView.ViewHolder(itemPostSendiriBinding.root) {
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