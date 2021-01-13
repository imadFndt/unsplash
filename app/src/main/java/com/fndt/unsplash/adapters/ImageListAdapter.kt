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
import com.fndt.unsplash.model.ListPage
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.util.UnsplashDiffUtilCallback
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ImageListAdapter : RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder>() {
    private val items = mutableListOf<UnsplashPhoto>()
    var itemClickListener: ((UnsplashPhoto) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = ImageListViewHolder(ImageItemBinding.inflate(inflater, parent, false)).apply {
            binding.itemImage.layoutParams.height = parent.measuredWidth / 3
        }
        holder.binding.itemImage.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) itemClickListener?.invoke(items[pos])
        }
        holder.binding.badNetworkText.isVisible = false
        return holder
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
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

    fun setItems(newItems: ListPage<UnsplashPhoto>) {
        newItems.items?.let {
            val diff = DiffUtil.calculateDiff(UnsplashDiffUtilCallback(items, it))
            items.clear()
            items.addAll(it)
            diff.dispatchUpdatesTo(this)
        }
    }

    class ImageListViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        fun circularDrawable(context: Context) = CircularProgressDrawable(context).apply {
            setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.circularTrackColor)
            centerRadius = 100f
            strokeWidth = 3f
            start()
        }
    }
}
