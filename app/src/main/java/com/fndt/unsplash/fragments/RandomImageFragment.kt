package com.fndt.unsplash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.fndt.unsplash.databinding.RandomImageFragmentBinding
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.ImageDescriptionFragmentViewModel
import com.fndt.unsplash.viewmodels.RandomImageFragmentViewModel

//TODO SWIPE REFRESH
class RandomImageFragment : Fragment() {
    private lateinit var binding: RandomImageFragmentBinding
    private val viewModel: RandomImageFragmentViewModel by viewModels {
        (requireActivity().application as UnsplashApplication).component.getRandomImageViewModelFactory()
    }
    private lateinit var imageDescriptionViewModel: ImageDescriptionFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RandomImageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageDescriptionViewModel =
            ViewModelProvider(requireActivity()).get(ImageDescriptionFragmentViewModel::class.java)
        viewModel.randomImage.observe(viewLifecycleOwner) { imageDescriptionViewModel.setImage(it) }
    }
}