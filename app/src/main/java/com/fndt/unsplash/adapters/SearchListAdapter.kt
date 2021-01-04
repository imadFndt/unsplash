package com.fndt.unsplash.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.fndt.unsplash.R
import com.fndt.unsplash.databinding.ImageItemBinding
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.model.UnsplashSearchResult
import com.fndt.unsplash.util.SearchDiffUtilCallback
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class SearchListAdapter : RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>() {
    private val items = mutableListOf<UnsplashPhoto>()
    var itemClickListener: ((UnsplashPhoto) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = SearchListViewHolder(ImageItemBinding.inflate(inflater, parent, false)).apply {
            binding.itemImage.layoutParams.height = parent.measuredWidth / 3
        }
        holder.binding.itemImage.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) itemClickListener?.invoke(items[pos])
        }
        holder.binding.badNetworkText.isVisible = false
        return holder
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        Picasso.get().load(items[position].urls.small)
            .placeholder(circularDrawable(holder.binding.itemImage.context))
            .fit()
            .centerCrop()
            .into(holder.binding.itemImage, object : Callback {
                override fun onSuccess() {
                    holder.binding.badNetworkText.isVisible = false
                }

                override fun onError(e: Exception?) {
                    holder.binding.badNetworkText.isVisible = true
                }
            })
    }

    override fun getItemCount() = items.size

    fun setItems(newItems: UnsplashSearchResult) {
        val diff =
            DiffUtil.calculateDiff(SearchDiffUtilCallback(items, newItems.results))
        items.clear()
        items.addAll(newItems.results)
        diff.dispatchUpdatesTo(this)
    }

    class SearchListViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        fun circularDrawable(context: Context) = CircularProgressDrawable(context).apply {
            setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.circularTrackColor)
            centerRadius = 100f
            strokeWidth = 3f
            start()
        }
    }
}
