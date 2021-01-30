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
import com.fndt.unsplash.model.UnsplashRepository.DataProcess
import com.fndt.unsplash.viewmodels.IMAGES_RESET
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*

class ImageListFragment : Fragment() {
    var itemClickListener: ((UnsplashPhoto) -> Unit)? = null
    var onScrollListener: (() -> Unit)? = null
    var onRequestUpdateListener: ((Int) -> Unit)? = null
    var onPageSelectedListener: ((Int) -> Unit)? = null
    lateinit var adapter: PagerAdapter

    private lateinit var binding: RecyclerLayoutBinding
    private var pageJob: Job? = null

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
        binding.updateButton.setOnClickListener { onRequestUpdateListener?.invoke(IMAGES_RESET) }

        adapter = PagerAdapter()
        with(adapter) {
            onListItemClickListener = { itemClickListener?.invoke(it) }
            onListScrollListener = { onScrollListener?.invoke() }
            onUpdatePageListener = { onRequestUpdateListener?.invoke(it) }
        }
        binding.searchPager.adapter = adapter
        TabLayoutMediator(binding.tabs, binding.searchPager) { tab, index ->
            tab.text = (index + 1).toString()
        }.attach()
        binding.searchPager.registerOnPageChangeCallback(pagerCallback)
    }

    fun setData(status: DataProcess, currentPage: Int) {
        updateTextMessage(status)
        binding.searchPager.isVisible = status is DataProcess.Running && status.hasData()
        binding.tabs.isVisible = status is DataProcess.Running && status.hasData()
        binding.messageTextView.isVisible = status !is DataProcess.Running
        binding.placeholder.isVisible = status.isFirstPageLoading()
        binding.updateButton.isVisible = status is DataProcess.Failure
        if (status.hasData()) (binding.searchPager.adapter as PagerAdapter).setData(status as DataProcess.Running)
        if (binding.searchPager.currentItem != currentPage) updateSelectedPage(currentPage)
    }

    private fun updateSelectedPage(currentPage: Int) {
        pageJob = CoroutineScope(Dispatchers.Main).launch {
            delay(50)
            if (isActive) binding.searchPager.currentItem = currentPage
        }
    }

    override fun onDestroyView() {
        pageJob?.cancel()
        binding.searchPager.unregisterOnPageChangeCallback(pagerCallback)
        with(adapter) {
            onListItemClickListener = null
            onListScrollListener = null
            onUpdatePageListener = null
        }
        super.onDestroyView()
    }

    private fun updateTextMessage(status: DataProcess) {
        binding.messageTextView.text = when (status) {
            is DataProcess.Idle, is DataProcess.Running -> resources.getText(
                R.string.searched_pictures_will_be_shown_here
            )
            is DataProcess.NothingFound -> resources.getText(R.string.nothing_found)
            is DataProcess.Failure -> resources.getText(R.string.bad_network)
        }
    }
}