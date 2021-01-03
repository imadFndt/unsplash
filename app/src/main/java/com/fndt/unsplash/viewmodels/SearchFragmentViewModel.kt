package com.fndt.unsplash.viewmodels

import androidx.lifecycle.*
import com.fndt.unsplash.model.UnsplashRepository
import com.fndt.unsplash.model.UnsplashSearchResult
import kotlinx.coroutines.launch

class SearchFragmentViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val photos: LiveData<UnsplashSearchResult> = repository.searchList

    val currentSearchText: LiveData<String> get() = currentSearchTextData

    private val currentSearchTextData = MutableLiveData<String>()

    fun requestSearch(query: String, page: Int) {
        viewModelScope.launch { repository.requestSearch(query, page) }
    }

    fun setText(string: String) {
        currentSearchTextData.value = string
    }

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchFragmentViewModel(repository) as T
        }
    }
}