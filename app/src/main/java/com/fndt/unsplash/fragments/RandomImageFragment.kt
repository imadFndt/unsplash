package com.fndt.unsplash.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.RandomImageFragmentViewModel

class RandomImageFragment : DescriptionFragment() {
    private val viewModel: RandomImageFragmentViewModel by viewModels {
        (requireActivity().application as UnsplashApplication).component.getRandomImageViewModelFactory()
    }

    override fun getSelectedItemLiveData(): LiveData<UnsplashPhoto?> = viewModel.randomImage
}