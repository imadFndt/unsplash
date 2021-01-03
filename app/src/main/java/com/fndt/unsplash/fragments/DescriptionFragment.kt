package com.fndt.unsplash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.fndt.unsplash.databinding.ImageFragmentBinding
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.viewmodels.ImageDescriptionFragmentViewModel

abstract class DescriptionFragment : Fragment() {
    private lateinit var binding: ImageFragmentBinding

    private val imageDescriptionViewModel: ImageDescriptionFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getSelectedItemLiveData().observe(viewLifecycleOwner) { image ->
            image?.let { imageDescriptionViewModel.setImage(image) }
        }
    }

    abstract fun getSelectedItemLiveData(): LiveData<UnsplashPhoto?>
}