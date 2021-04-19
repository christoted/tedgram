package com.example.tedgram.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.User
import com.example.tedgram.databinding.ItemSearchBinding

class SearchAdapter: RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    var listUser = ArrayList<User>()

    fun setData(users: List<User>) {
        listUser.clear()
        listUser.addAll(users)
        notifyDataSetChanged()
    }

    private fun getData(): List<User> {
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


    inner class SearchViewHolder(private val itemSearchBinding: ItemSearchBinding) :
        RecyclerView.ViewHolder(itemSearchBinding.root) {

        fun bind(user: User) {
            with(itemSearchBinding) {
                Glide.with(itemView.context)
                    .load(user.imageUrl)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(idImageView)
                

                tvName.text = user.username
            }
        }

    }

}