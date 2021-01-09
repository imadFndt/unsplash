package com.fndt.unsplash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fndt.unsplash.adapters.CollectionsAdapter
import com.fndt.unsplash.databinding.CollectionsFragmentBinding
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.CollectionsViewModel


class CollectionsFragment : Fragment() {
    private val viewModel: CollectionsViewModel by viewModels {
        (requireActivity().application as UnsplashApplication).component.getCollectionsViewModelFactory()
    }

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
        val dividerItemDecoration =
            DividerItemDecoration(binding.collectionList.context, LinearLayout.VERTICAL)
        binding.collectionList.addItemDecoration(dividerItemDecoration)
        binding.collectionList.adapter = adapter
        binding.collectionList.layoutManager = LinearLayoutManager(context)
        viewModel.networkStatus.observe(viewLifecycleOwner) { status ->
            binding.messageTextView.isVisible =
                status == NetworkStatus.FAILURE && viewModel.collections.value == null
            binding.placeholder.isVisible = status == NetworkStatus.PENDING
        }
        viewModel.collections.observe(viewLifecycleOwner) { list ->
            binding.collectionList.isVisible = list.isNotEmpty()
            adapter.setItems(list)
        }
    }
}