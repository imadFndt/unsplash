package com.fndt.unsplash.adapters

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.util.putAll
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fndt.unsplash.R
import com.fndt.unsplash.databinding.ImagePageItemBinding
import com.fndt.unsplash.model.ListPage
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.model.UnsplashRepository

class PagerAdapter : RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {
    var onListItemClickListener: ((UnsplashPhoto) -> Unit)? = null
    var onListScrollListener: (() -> Unit)? = null
    var onUpdatePageListener: ((page: Int) -> Unit)? = null

    private var totalPages = 0
    private val data = SparseArray<ListPage?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = PagerViewHolder(ImagePageItemBinding.inflate(inflater, parent, false))
        val itemsAdapter = ImageListAdapter()
        itemsAdapter.itemClickListener = { onListItemClickListener?.invoke(it) }
        holder.binding.updateButton.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) onUpdatePageListener?.invoke(pos)
        }
        setupRecyclerView(holder.binding, itemsAdapter)
        return holder
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val haveData = data.get(position) != null
        val isFailure = data.get(position)?.networkStatus == NetworkStatus.FAILURE
        holder.binding.updateButton.isVisible = !haveData || isFailure
        holder.binding.badNetworkText.isVisible = isFailure
        if (haveData) (holder.binding.recyclerList.adapter as ImageListAdapter?)?.setItems(data.get(position)!!)
        holder.binding.recyclerList.isVisible = haveData && !isFailure
    }

    override fun getItemCount() = totalPages

    fun setData(newData: UnsplashRepository.SearchProcess.Running) {
        totalPages = newData.totalPages!!
        data.clear()
        data.putAll(newData.pages)
        notifyDataSetChanged()
    }

    private fun setupRecyclerView(binding: ImagePageItemBinding, adapter: ImageListAdapter) {
        val context = binding.root.context
        binding.recyclerList.adapter = adapter
        binding.recyclerList.layoutManager =
            GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        with(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)) {
            setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
            binding.recyclerList.addItemDecoration(this)
        }
        with(DividerItemDecoration(context, DividerItemDecoration.VERTICAL)) {
            setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
            binding.recyclerList.addItemDecoration(this)
        }
        binding.recyclerList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) onListScrollListener?.invoke()
            }
        })
    }

    class PagerViewHolder(val binding: ImagePageItemBinding) : RecyclerView.ViewHolder(binding.root)
}