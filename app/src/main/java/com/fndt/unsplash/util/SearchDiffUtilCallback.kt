package com.fndt.unsplash.util

import androidx.recyclerview.widget.DiffUtil
import com.fndt.unsplash.model.UnsplashPhoto

class SearchDiffUtilCallback(
    private val oldList: List<UnsplashPhoto>,
    private val newList: List<UnsplashPhoto>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

}