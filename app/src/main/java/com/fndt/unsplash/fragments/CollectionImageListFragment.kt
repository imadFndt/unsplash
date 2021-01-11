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
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.CollectionImageListViewModel
import com.fndt.unsplash.viewmodels.MainActivityViewModel

class CollectionImageListFragment : Fragment() {
    private val viewModel: CollectionImageListViewModel by viewModels {
        (requireActivity().application as UnsplashApplication).component.getCollectionImageListViewModelFactory()
    }
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CollectionImageListFragmentBinding.inflate(inflater, container, false)
//        childFragmentManager.findFragmentByTag(resources.getString(R.string.collections_image_list_tag))
//            ?.let {
//                childFragmentManager.beginTransaction()
//                    .add(R.id.list_fragment, ImageListFragment())
//                    .commit()
//            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frag =
            childFragmentManager.findFragmentByTag(resources.getString(R.string.collections_image_list_tag))
        val listFragment =
            (childFragmentManager.findFragmentByTag(resources.getString(R.string.collections_image_list_tag)) as ImageListFragment).apply {
                this.onPageSelectedListener = { viewModel.currentSelectedPage = it }
                this.onRequestUpdateListener = { viewModel.loadIfAbsent(it) }
                this.itemClickListener = { activityViewModel.selectCollectionItem(it) }
            }
        viewModel.collection.observe(viewLifecycleOwner) {
            listFragment.setData(it, viewModel.currentSelectedPage)
        }
        activityViewModel.selectedCollection.observe(viewLifecycleOwner) {
            viewModel.selectedCollection = it
            viewModel.loadIfAbsent(0)
        }
    }
}