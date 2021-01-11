package com.fndt.unsplash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fndt.unsplash.model.UnsplashCollection
import com.fndt.unsplash.model.UnsplashRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class CollectionImageListViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val collection: LiveData<UnsplashRepository.SearchProcess> = repository.selectedCollectionImages
    var currentSelectedPage: Int = NO_PAGE
    var selectedCollection: UnsplashCollection? = null

    private var currentJob: Job? = null
    private var previousJob: Job? = null

    fun loadIfAbsent(position: Int) {
        if (collection.value?.hasDataAtPage(position) == true) return
        requestLoad(position)
    }

    override fun onCleared() {
        previousJob?.cancel()
        currentJob?.cancel()
    }

    private fun requestLoad(page: Int) {
        selectedCollection?.let {
            previousJob = currentJob
            currentJob = viewModelScope.launch {
                previousJob?.cancelAndJoin()
                repository.requestCollectionImages(it, page, page == 0)
                currentJob = null
                previousJob = null
            }
        }
    }

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CollectionImageListViewModel(repository) as T
        }
    }
}
