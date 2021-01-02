package com.fndt.unsplash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fndt.unsplash.model.UnsplashRepository
import com.fndt.unsplash.model.UnsplashSearchResult
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class SearchFragmentViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val photos: LiveData<UnsplashSearchResult> = repository.searchList

    var currentSearchText: String by Delegates.observable("") { _, _, new ->
        if (new.isNotEmpty()) requestSearch(new, 1, true)
    }

    private fun requestSearch(query: String, page: Int, resetList: Boolean) {
        viewModelScope.launch { repository.requestSearch(query, page) }
    }

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchFragmentViewModel(repository) as T
        }
    }
}