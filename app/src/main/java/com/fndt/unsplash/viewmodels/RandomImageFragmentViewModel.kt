package com.fndt.unsplash.viewmodels

import androidx.lifecycle.*
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.model.UnsplashRepository
import com.fndt.unsplash.util.combineStatus
import kotlinx.coroutines.launch

class RandomImageFragmentViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val randomImage: LiveData<UnsplashPhoto?> = repository.randomPhoto.switchMap { unsplashPhoto ->
        MutableLiveData<UnsplashPhoto?>(unsplashPhoto)
    }
    val networkStatus: LiveData<NetworkStatus> get() = combinedNetworkStatus

    private val combinedNetworkStatus = MediatorLiveData<NetworkStatus>()
    private val loadNetworkStatus: LiveData<NetworkStatus> = repository.networkStatus
    private val imageNetworkStatus = MutableLiveData<NetworkStatus>()

    init {
        requestUpdate()
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

    fun requestUpdate() {
        viewModelScope.launch { repository.requestRandomPhoto() }
    }

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RandomImageFragmentViewModel(repository) as T
        }
    }
}