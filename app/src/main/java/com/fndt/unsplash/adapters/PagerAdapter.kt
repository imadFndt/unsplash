package com.fndt.unsplash.adapters

import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.util.putAll
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fndt.unsplash.R
import com.fndt.unsplash.databinding.ImagePageItemBinding
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.model.UnsplashSearchResult

class PagerAdapter : RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {
    var onListItemClickListener: ((UnsplashPhoto) -> Unit)? = null
    var onListScrollListener: (() -> Unit)? = null

    private var totalPages = 0
    private val data = SparseArray<UnsplashSearchResult>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = PagerViewHolder(ImagePageItemBinding.inflate(inflater, parent, false))
        val itemsAdapter = SearchListAdapter()
        itemsAdapter.itemClickListener = { onListItemClickListener?.invoke(it) }
        setupRecyclerView(holder.binding, itemsAdapter)
        return holder
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        data.get(position)?.let { (holder.binding.recyclerList.adapter as SearchListAdapter?)?.setItems(it) }
    }

    override fun getItemCount() = totalPages

    fun setData(newData: SparseArray<UnsplashSearchResult>) {
        totalPages = newData.valueAt(0).totalPages
        val diff = DiffUtil.calculateDiff(PagerDiffUtilCallback(data, newData))
        data.clear()
        data.putAll(newData)
        diff.dispatchUpdatesTo(this)
    }

    private fun setupRecyclerView(binding: ImagePageItemBinding, adapter: SearchListAdapter) {
        val context = binding.root.context
        binding.recyclerList.adapter = adapter
        binding.recyclerList.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
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
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        Log.d("SearchListScrollState", "The RecyclerView is not scrolling")
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        onListScrollListener?.invoke()
//                        binding.searchTextLayout.clearFocus()
//                        hideKeyboard()
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> Log.d("SearchListScrollState", "Scroll Settling")
                }
            }
        })
    }

    class PagerViewHolder(val binding: ImagePageItemBinding) : RecyclerView.ViewHolder(binding.root)

    class PagerDiffUtilCallback(
        private val list1: SparseArray<UnsplashSearchResult>,
        private val list2: SparseArray<UnsplashSearchResult>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = list1.size()
        override fun getNewListSize() = list2.size()
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            list1[oldItemPosition] == list2[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            list1.get(oldItemPosition)?.results == list2.get(newItemPosition)?.results


    }
}