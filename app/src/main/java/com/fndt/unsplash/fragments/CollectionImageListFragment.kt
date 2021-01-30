package com.fndt.unsplash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.fndt.unsplash.R
import com.fndt.unsplash.databinding.CollectionImageListFragmentBinding
import com.fndt.unsplash.fragments.util.ImageListFragment
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.CollectionImageListViewModel
import com.fndt.unsplash.viewmodels.MainActivityViewModel

class CollectionImageListFragment : Fragment() {
    private val viewModel: CollectionImageListViewModel by viewModels {
        (requireActivity().application as UnsplashApplication).component.getCollectionImageListViewModelFactory()
    }
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var binding: CollectionImageListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CollectionImageListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listFragment =
            (childFragmentManager.findFragmentByTag(getString(R.string.collections_image_list_tag)) as ImageListFragment).apply {
                onPageSelectedListener = { viewModel.currentSelectedPage = it }
                onRequestUpdateListener = { viewModel.loadIfAbsent(it) }
                itemClickListener = { activityViewModel.selectCollectionItem(it) }
            }
        viewModel.collection.observe(viewLifecycleOwner) {
            listFragment.setData(it, viewModel.currentSelectedPage)
        }
        activityViewModel.selectedCollection.observe(viewLifecycleOwner) { collection ->
            viewModel.setCollection(collection)
            collection?.let {
                binding.collectionsTitle.text = getString(R.string.collections_title, collection.title)
            }
        }
    }
}