package com.fndt.unsplash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fndt.unsplash.model.UnsplashRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val randomImage = repository.randomPhoto

    init {
        requestUpdate()
    }

    fun requestUpdate() {
        viewModelScope.launch { repository.requestRandomPhoto() }
    }

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(repository) as T
        }
    }
}