package com.fndt.unsplash.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fndt.unsplash.model.UnsplashRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class CollectionsViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val collections = repository.collections
    var currentPage = 0

    private var currentJob: Job? = null
    private var previousJob: Job? = null

    init {
        requestCollections(0)
    }

    private fun requestCollections(page: Int) {
        previousJob = currentJob
        currentJob = viewModelScope.launch {
            previousJob?.cancelAndJoin()
            repository.requestCollections(page, page == 0)
            currentPage = page
        }
    }

    override fun onCleared() {
        previousJob?.cancel()
        currentJob?.cancel()
    }

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CollectionsViewModel(repository) as T
        }
    }
}