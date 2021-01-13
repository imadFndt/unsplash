package com.fndt.unsplash.util

import androidx.recyclerview.widget.DiffUtil
import com.fndt.unsplash.model.UnsplashItems
import com.fndt.unsplash.model.UnsplashPhoto

class UnsplashDiffUtilCallback(
    private val oldList: List<UnsplashItems>,
    private val newList: List<UnsplashItems>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

}