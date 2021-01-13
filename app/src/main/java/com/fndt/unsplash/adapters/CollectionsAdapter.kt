package com.fndt.unsplash.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fndt.unsplash.databinding.CollectionItemBinding
import com.fndt.unsplash.model.UnsplashCollection
import com.fndt.unsplash.model.UnsplashRepository
import com.fndt.unsplash.util.UnsplashDiffUtilCallback
import com.squareup.picasso.Picasso

class CollectionsAdapter : RecyclerView.Adapter<CollectionsAdapter.CollectionsViewHolder>() {
    var onListItemClickListener: ((UnsplashCollection) -> Unit)? = null

    private val items = mutableListOf<UnsplashCollection>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = CollectionsViewHolder(CollectionItemBinding.inflate(inflater, parent, false))
        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onListItemClickListener?.invoke(items[pos])
        }
        return holder
    }

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        Picasso.get()
            .load(items[position].coverPhoto.urls.thumb)
            .fit()
            .centerCrop()
            .placeholder(ImageListAdapter.circularDrawable(holder.binding.image.context))
            .into(holder.binding.image)
        holder.binding.imagesCount.text = items[position].totalPhotos.toString()
        holder.binding.collectionName.text = items[position].title
    }

    override fun getItemCount() = items.size

    fun setItems(newItems: List<UnsplashCollection>) {
        val diff = DiffUtil.calculateDiff(UnsplashDiffUtilCallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        diff.dispatchUpdatesTo(this)
    }

    class CollectionsViewHolder(val binding: CollectionItemBinding) : RecyclerView.ViewHolder(binding.root)
}