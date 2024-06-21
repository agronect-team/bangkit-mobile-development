package com.example.agronect.ui.adapter

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agronect.data.response.DataHistoryItem
import com.example.agronect.databinding.ItemStoriesmypostBinding

class ListHistoryAdapter : ListAdapter<DataHistoryItem, ListHistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
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

        fun bind(history: DataHistoryItem) {

            Log.d(ContentValues.TAG, "bind: $history")
            binding.tvName.text = "${history.prediction}"
            binding.tvDetail.text = history.description

            history.image?.data?.let {
                val byteArray = it.filterNotNull().map { byte -> byte.toByte() }.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                binding.ivImage.setImageBitmap(bitmap)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: DataHistoryItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataHistoryItem>() {
            override fun areItemsTheSame(
                oldItem: DataHistoryItem,
                newItem: DataHistoryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataHistoryItem,
                newItem: DataHistoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}