package com.fndt.unsplash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.model.UnsplashPhoto

class ImageDescriptionFragmentViewModel : ViewModel() {
    val image: LiveData<UnsplashPhoto> get() = imageData
    val imageNetworkStatus: LiveData<NetworkStatus> get() = networkStatusData

    private val networkStatusData = MutableLiveData<NetworkStatus>()

    private val imageData = MutableLiveData<UnsplashPhoto>()

    fun setImage(image: UnsplashPhoto) {
        imageData.value = image
    }

    fun setNetworkStatus(networkStatus: NetworkStatus) {
        if (networkStatus != networkStatusData.value) networkStatusData.value = networkStatus
    }
}