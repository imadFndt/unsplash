package com.fndt.unsplash.viewmodels

import androidx.lifecycle.*
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.model.UnsplashRepository
import kotlinx.coroutines.launch

class RandomImageFragmentViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val randomImage: LiveData<UnsplashPhoto?> = repository.randomPhoto.switchMap { unsplashPhoto ->
        MutableLiveData<UnsplashPhoto?>(unsplashPhoto)
    }

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