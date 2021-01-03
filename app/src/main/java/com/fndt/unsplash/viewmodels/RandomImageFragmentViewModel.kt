package com.fndt.unsplash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.model.UnsplashRepository
import kotlinx.coroutines.launch

class RandomImageFragmentViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val randomImage: LiveData<UnsplashPhoto> = repository.randomPhoto

    init {
        requestUpdate()
    }

    private fun requestUpdate() {
        viewModelScope.launch { repository.requestRandomPhoto() }
    }

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RandomImageFragmentViewModel(repository) as T
        }
    }
}