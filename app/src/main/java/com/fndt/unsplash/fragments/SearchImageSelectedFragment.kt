package com.fndt.unsplash.fragments

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.fndt.unsplash.fragments.util.ImageSelectedFragment
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.viewmodels.MainActivityViewModel

class SearchImageSelectedFragment : ImageSelectedFragment() {
    override val selectedItem: LiveData<UnsplashPhoto?> get() = activityViewModel.searchSelectedItem
    private val activityViewModel: MainActivityViewModel by activityViewModels()
}