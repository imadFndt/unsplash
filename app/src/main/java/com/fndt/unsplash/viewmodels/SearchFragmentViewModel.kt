package com.fndt.unsplash.viewmodels

import androidx.lifecycle.*
import com.fndt.unsplash.model.UnsplashRepository
import com.fndt.unsplash.model.UnsplashRepository.DataProcess
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

const val NO_PAGE = -1

class SearchFragmentViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val currentSearchText: LiveData<String> get() = currentSearchTextData
    val search: LiveData<DataProcess> = repository.search.switchMap {
        needUpdate = false
        MutableLiveData(it)
    }

    var currentSearchPage: Int = NO_PAGE

    private var needUpdate: Boolean = false
    private val currentSearchTextData = MutableLiveData<String>()

    private var currentJob: Job? = null
    private var previousJob: Job? = null

    fun setText(string: String) {
        if (currentSearchTextData.value == string) return
        needUpdate = true
        currentSearchTextData.value = string
        requestSearch(string, 0)
    }

    fun loadIfAbsent(position: Int) {
        if (search.value?.hasDataAtPage(position) == true && !needUpdate && position != IMAGES_RESET) return
        currentSearchText.value?.let {
            requestSearch(it, if (needUpdate || position == IMAGES_RESET) 0 else position)
        }
    }

    override fun onCleared() {
        previousJob?.cancel()
        currentJob?.cancel()
    }

    private fun requestSearch(query: String, page: Int) {
        previousJob = currentJob
        currentJob = viewModelScope.launch {
            previousJob?.cancelAndJoin()
            repository.requestSearch(query, page, page == 0 || needUpdate)
            currentJob = null
            previousJob = null
        }
    }

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchFragmentViewModel(repository) as T
        }
    }
}