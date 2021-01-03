package com.fndt.unsplash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.model.UnsplashRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: UnsplashRepository) : ViewModel() {

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(repository) as T
        }
    }
}