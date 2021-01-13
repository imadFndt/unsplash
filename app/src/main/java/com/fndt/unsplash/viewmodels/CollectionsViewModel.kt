package com.fndt.unsplash.viewmodels

import androidx.lifecycle.*
import com.fndt.unsplash.model.UnsplashRepository
import kotlinx.coroutines.*

class CollectionsViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val collections = repository.collections.switchMap { pair ->
        currentPage = pair.first
        MutableLiveData(pair.second)
    }
    var currentPage = 0
    var isLoading: Boolean = false

    private var currentJob: Job? = null
    private var previousJob: Job? = null

    init {
        requestCollections(0)
    }

    fun loadIfAbsent(position: Int) {
        requestCollections(position)
    }

    private fun requestCollections(page: Int) {
        previousJob = currentJob
        currentJob = viewModelScope.launch {
            withContext(Dispatchers.Main) { isLoading = true }
            previousJob?.cancelAndJoin()
            repository.requestCollections(page, page == 0)
            withContext(Dispatchers.Main) { isLoading = false }
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