package com.fndt.unsplash.fragments.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.fndt.unsplash.databinding.ImageFragmentBinding
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.viewmodels.ImageDescriptionFragmentViewModel

abstract class ImageSelectedFragment : Fragment() {
    abstract val selectedItem: LiveData<UnsplashPhoto?>
    private val imageDescriptionViewModel: ImageDescriptionFragmentViewModel by activityViewModels()
    private lateinit var binding: ImageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ImageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectedItem.observe(viewLifecycleOwner) { it?.let { imageDescriptionViewModel.setImage(it) } }
        binding.updateButton.setOnClickListener {
            selectedItem.value?.let { imageDescriptionViewModel.setImage(it) }
        }
        imageDescriptionViewModel.imageNetworkStatus.observe(viewLifecycleOwner) { status ->
            with(binding) {
                placeholder.isVisible = status == NetworkStatus.PENDING
                descriptionFragment.isVisible = status == NetworkStatus.SUCCESS
                statusText.isVisible = status == NetworkStatus.FAILURE
                updateButton.isVisible = status == NetworkStatus.FAILURE
            }
        }
    }
}