package com.fndt.unsplash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.fndt.unsplash.adapters.SearchListAdapter
import com.fndt.unsplash.databinding.ImageFragmentBinding
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.ImageDescriptionFragmentViewModel
import com.fndt.unsplash.viewmodels.RandomImageFragmentViewModel

class RandomImageFragment : Fragment() {
    private val viewModel: RandomImageFragmentViewModel by viewModels {
        (requireActivity().application as UnsplashApplication).component.getRandomImageViewModelFactory()
    }

    private val imageDescriptionViewModel: ImageDescriptionFragmentViewModel by activityViewModels()
    private lateinit var binding: ImageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (viewModel.randomImage.value == null) viewModel.requestUpdate()
        viewModel.randomImage.observe(viewLifecycleOwner) { image ->
            image?.let { imageDescriptionViewModel.setImage(image) }
        }
        binding.placeholder.setImageDrawable(SearchListAdapter.circularDrawable(requireContext()))
        viewModel.networkStatus.observe(viewLifecycleOwner) { status ->
            binding.placeholder.isVisible = status == NetworkStatus.PENDING
            binding.descriptionFragment.isVisible = status == NetworkStatus.SUCCESS
            binding.statusText.isVisible = status == NetworkStatus.FAILURE
        }
        imageDescriptionViewModel.imageNetworkStatus.observe(viewLifecycleOwner) { status ->
            viewModel.setImageStatus(status)
        }
    }
}