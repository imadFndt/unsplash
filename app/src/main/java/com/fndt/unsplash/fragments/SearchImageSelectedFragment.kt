package com.fndt.unsplash.fragments

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.viewmodels.MainActivityViewModel

class SearchImageSelectedFragment : DescriptionFragment() {
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    override fun getSelectedItemLiveData(): LiveData<UnsplashPhoto?> = activityViewModel.searchSelectedItem
}