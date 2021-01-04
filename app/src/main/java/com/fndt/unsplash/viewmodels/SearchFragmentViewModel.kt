package com.fndt.unsplash.viewmodels

import androidx.lifecycle.*
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.model.UnsplashRepository
import com.fndt.unsplash.model.UnsplashSearchResult
import com.fndt.unsplash.util.combineStatus
import kotlinx.coroutines.launch

class SearchFragmentViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val photos: LiveData<UnsplashSearchResult> = repository.searchList
    val networkStatus: LiveData<NetworkStatus> = repository.networkStatus
    val currentSearchText: LiveData<String> get() = currentSearchTextData

    private val currentSearchTextData = MutableLiveData<String>()

    fun requestSearch(query: String, page: Int) {
        viewModelScope.launch { repository.requestSearch(query, page) }
    }

    fun setText(string: String) {
        currentSearchTextData.value = string
    }

    private val combinedNetworkStatus = MediatorLiveData<NetworkStatus>()
    private val loadNetworkStatus: LiveData<NetworkStatus> = repository.networkStatus
    private val imageNetworkStatus = MutableLiveData<NetworkStatus>()

    init {
        combinedNetworkStatus.addSource(loadNetworkStatus) { networkStatus ->
            combinedNetworkStatus.value = combineStatus(networkStatus, imageNetworkStatus.value)
        }
        combinedNetworkStatus.addSource(imageNetworkStatus) { imageNetworkStatus ->
            combinedNetworkStatus.value = combineStatus(loadNetworkStatus.value, imageNetworkStatus)
        }
    }

    fun setImageStatus(status: NetworkStatus) {
        imageNetworkStatus.value = status
    }

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchFragmentViewModel(repository) as T
        }
    }
}