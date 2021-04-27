package com.example.tedgram.presentation.ui.home.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.databinding.ItemHomeBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class PostAdapter(
    options: FirestoreRecyclerOptions<Post>,
    private val mAuth: FirebaseAuth?,
    private val db: FirebaseFirestore?
) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
    options
) {

    val listPost = ArrayList<Post>()


    fun setPost(posts: ArrayList<Post>) {
        listPost.clear()
        listPost.addAll(posts)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemHomeBinding =
            ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PostViewHolder(itemHomeBinding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.bind(model)
    }


    inner class PostViewHolder(private val itemHomeBinding: ItemHomeBinding) :
        RecyclerView.ViewHolder(itemHomeBinding.root) {

        private fun fetchUser(currentId: String) {
            db?.collection("users")?.document(currentId)?.addSnapshotListener { value, error ->

                if ( value != null && value.exists()) {
                    val result = value.data

                    val imageUrl = result?.get("imageUrl").toString()
                    val username = result?.get("username").toString()

                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .error(R.color.chalman_pink)
                        .into(itemHomeBinding.profileImage)

                    itemHomeBinding.username.text = username
                }

            }


        }

        fun bind(post: Post) {

            fetchUser(post.userId!!)

            with(itemHomeBinding) {

                Glide.with(itemView.context)
                    .load(post.postURL)
                    .error(R.color.chalman_pink)
                    .listener(object: RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE

                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE

                            return false
                        }

                    })
                    .into(imageView)

                tvCaption.text = post.postCaption

                btnLike.setOnClickListener {
                    Toast.makeText(itemView.context, "Like", Toast.LENGTH_SHORT).show()
                }

                btnCommand.setOnClickListener {
                    Toast.makeText(itemView.context, "Command", Toast.LENGTH_SHORT).show()
                }

                btnBookmarked.setOnClickListener {
                    Toast.makeText(itemView.context, "Bookmarked", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}