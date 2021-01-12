package com.fndt.unsplash.fragments.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.fndt.unsplash.R
import com.fndt.unsplash.adapters.ImageListAdapter
import com.fndt.unsplash.adapters.PagerAdapter
import com.fndt.unsplash.databinding.RecyclerLayoutBinding
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.model.UnsplashRepository.SearchProcess
import com.google.android.material.tabs.TabLayoutMediator

class ImageListFragment : Fragment() {
    var itemClickListener: ((UnsplashPhoto) -> Unit)? = null
    var onScrollListener: (() -> Unit)? = null
    var onRequestUpdateListener: ((Int) -> Unit)? = null
    var onPageSelectedListener: ((Int) -> Unit)? = null

    private lateinit var binding: RecyclerLayoutBinding

    private val pagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            onPageSelectedListener?.invoke(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecyclerLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeholder.setImageDrawable(ImageListAdapter.circularDrawable(requireContext()))

        val adapter = PagerAdapter()
        adapter.onListItemClickListener = { itemClickListener?.invoke(it) }
        adapter.onListScrollListener = { onScrollListener?.invoke() }
        adapter.onUpdatePageListener = { onRequestUpdateListener?.invoke(it) }
        binding.searchPager.adapter = adapter
        TabLayoutMediator(binding.tabs, binding.searchPager) { tab, index ->
            tab.text = (index + 1).toString()
        }.attach()
        binding.searchPager.registerOnPageChangeCallback(pagerCallback)
    }

    fun setData(status: SearchProcess, currentPage: Int) {
        updateTextMessage(status)
        binding.searchPager.isVisible = status is SearchProcess.Running && status.hasData()
        binding.tabs.isVisible = status is SearchProcess.Running && status.hasData()
        binding.messageTextView.isVisible = status !is SearchProcess.Running
        binding.placeholder.isVisible = status.isFirstPageLoading()
        if (status.hasData()) (binding.searchPager.adapter as PagerAdapter).setData(status as SearchProcess.Running)
        if (binding.searchPager.currentItem != currentPage) binding.searchPager.currentItem = currentPage
    }

    override fun onDestroyView() {
        binding.searchPager.unregisterOnPageChangeCallback(pagerCallback)
        super.onDestroyView()
    }

    private fun updateTextMessage(status: SearchProcess) {
        binding.messageTextView.text = when (status) {
            is SearchProcess.Idle, is SearchProcess.Running -> resources.getText(
                R.string.searched_pictures_will_be_shown_here
            )
            is SearchProcess.NothingFound -> resources.getText(R.string.nothing_found)
            is SearchProcess.Failure -> resources.getText(R.string.bad_network)
        }
    }
}