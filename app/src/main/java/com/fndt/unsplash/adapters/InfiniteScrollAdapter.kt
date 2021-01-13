package com.fndt.unsplash.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.fndt.unsplash.databinding.LoadingItemBinding

class InfiniteScrollAdapter : RecyclerView.Adapter<InfiniteScrollAdapter.InfiniteScrollViewHolder>() {
    private var itemsCount: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfiniteScrollViewHolder {
        return InfiniteScrollViewHolder(
            LoadingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: InfiniteScrollViewHolder, position: Int) {
        val context = holder.binding.progressCircular.context
        holder.binding.progressCircular.setImageDrawable(ImageListAdapter.circularDrawable(context))
        holder.itemView.isVisible = itemsCount == 1
    }

    override fun getItemCount() = itemsCount

    fun setState(isLoading: Boolean) {
        if (isLoading && itemsCount != 1) {
            itemsCount = 1
            notifyItemInserted(0)
        } else if (!isLoading && itemsCount != 0) {
            itemsCount = 0
            notifyItemRemoved(0)
        }
    }

    class InfiniteScrollViewHolder(val binding: LoadingItemBinding) : RecyclerView.ViewHolder(binding.root)
}