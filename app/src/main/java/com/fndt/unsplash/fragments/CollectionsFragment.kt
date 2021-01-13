package com.fndt.unsplash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fndt.unsplash.adapters.CollectionsAdapter
import com.fndt.unsplash.adapters.ImageListAdapter
import com.fndt.unsplash.adapters.InfiniteScrollAdapter
import com.fndt.unsplash.databinding.CollectionsFragmentBinding
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.CollectionsViewModel
import com.fndt.unsplash.viewmodels.MainActivityViewModel

const val VISIBLE_THRESHOLD = 3

class CollectionsFragment : Fragment() {
    private val viewModel: CollectionsViewModel by viewModels {
        (requireActivity().application as UnsplashApplication).component.getCollectionsViewModelFactory()
    }
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: CollectionsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CollectionsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = CollectionsAdapter()
        val loadingAdapter = InfiniteScrollAdapter()
        adapter.onListItemClickListener = { activityViewModel.selectCollection(it) }
        val dividerItemDecoration =
            DividerItemDecoration(binding.collectionList.context, LinearLayout.VERTICAL)
        binding.collectionList.addItemDecoration(dividerItemDecoration)
        binding.collectionList.adapter = ConcatAdapter(adapter, loadingAdapter)
        binding.collectionList.layoutManager = LinearLayoutManager(context)
        binding.collectionList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.collectionList.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                if (totalItemCount - VISIBLE_THRESHOLD == lastVisibleItem) {
                    if (!viewModel.isLoading) {
                        viewModel.loadIfAbsent(viewModel.currentPage + 1)
                        loadingAdapter.setState(true)
                    }
                }
            }
        })

        binding.placeholder.setImageDrawable(ImageListAdapter.circularDrawable(binding.placeholder.context))
        binding.updateButton.setOnClickListener { viewModel.loadIfAbsent(0) }

        viewModel.collections.observe(viewLifecycleOwner) { current ->
            val status = current.networkStatus
            val list = current.items
            loadingAdapter.setState(status == NetworkStatus.PENDING)
            binding.collectionList.isVisible = list?.isNotEmpty() == true
            binding.messageTextView.isVisible = status == NetworkStatus.FAILURE && list == null
            binding.placeholder.isVisible =
                status == NetworkStatus.PENDING && (list == null || list.isEmpty())
            binding.updateButton.isVisible = status == NetworkStatus.FAILURE && list == null
            list?.let { adapter.setItems(it) }
        }
    }
}