package com.fndt.unsplash.viewmodels

import androidx.lifecycle.*
import com.fndt.unsplash.model.UnsplashCollection
import com.fndt.unsplash.model.UnsplashRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

const val IMAGES_RESET = -1

class CollectionImageListViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val collection: LiveData<UnsplashRepository.DataProcess> = repository.selectedCollectionImages.switchMap {
        needUpdate = false
        MutableLiveData(it)
    }
    var currentSelectedPage: Int = NO_PAGE

    private var currentJob: Job? = null
    private var previousJob: Job? = null
    private var needUpdate = false
    private var selectedCollection: UnsplashCollection? = null

    fun loadIfAbsent(position: Int) {
        if (collection.value?.hasDataAtPage(position) == true && !needUpdate && position != IMAGES_RESET) return
        requestLoad(if (position == IMAGES_RESET) 0 else position)
    }

    fun setCollection(unsplashCollection: UnsplashCollection?) {
        if (selectedCollection == unsplashCollection) return
        selectedCollection = unsplashCollection
        needUpdate = true
        loadIfAbsent(0)
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