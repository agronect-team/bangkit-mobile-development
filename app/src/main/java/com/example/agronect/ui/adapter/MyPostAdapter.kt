package com.example.agronect.ui.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agronect.data.response.DataItem
import com.example.agronect.databinding.ItemStoriesmypostBinding

class MyPostAdapter : ListAdapter<DataItem, MyPostAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoriesmypostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.binding.root.setOnClickListener {
            onItemClickCallback?.onItemClicked(user)
        }
        holder.bind(user)
    }

    inner class MyViewHolder(val binding: ItemStoriesmypostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: DataItem) {

            Log.d(ContentValues.TAG, "bind: $story")
            binding.tvName.text = "${story.name}"
            binding.tvDetail.text = story.content

            Glide.with(binding.root.context)
                .load(story.imgUrl)
                .into(binding.ivImage)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: DataItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}