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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fndt.unsplash.adapters.CollectionsAdapter
import com.fndt.unsplash.adapters.ImageListAdapter
import com.fndt.unsplash.databinding.CollectionsFragmentBinding
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.CollectionsViewModel
import com.fndt.unsplash.viewmodels.MainActivityViewModel


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
        adapter.onListItemClickListener = { activityViewModel.selectCollection(it) }
        val dividerItemDecoration =
            DividerItemDecoration(binding.collectionList.context, LinearLayout.VERTICAL)
        binding.collectionList.addItemDecoration(dividerItemDecoration)
        binding.collectionList.adapter = adapter
        binding.collectionList.layoutManager = LinearLayoutManager(context)

        binding.placeholder.setImageDrawable(ImageListAdapter.circularDrawable(requireContext()))
        binding.updateButton.setOnClickListener { viewModel.loadIfAbsent(0) }

        viewModel.collections.observe(viewLifecycleOwner) { current ->
            val status = current.networkStatus
            val list = current.items
            binding.collectionList.isVisible = list?.isNotEmpty() == true
            binding.messageTextView.isVisible = status == NetworkStatus.FAILURE && list == null
            binding.placeholder.isVisible =
                status == NetworkStatus.PENDING && (list == null || list.isEmpty())
            list?.let { adapter.setItems(it) }
        }
    }
}