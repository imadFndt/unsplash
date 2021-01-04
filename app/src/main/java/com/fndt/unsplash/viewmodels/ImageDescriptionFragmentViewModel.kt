package com.fndt.unsplash.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.model.UnsplashPhoto

class ImageDescriptionFragmentViewModel : ViewModel() {
    val image: LiveData<UnsplashPhoto> get() = randomImageData
    val imageNetworkStatus: LiveData<NetworkStatus> get() = networkStatusData

    private val networkStatusData = MutableLiveData<NetworkStatus>()

    private val randomImageData = MutableLiveData<UnsplashPhoto>()

    fun setImage(image: UnsplashPhoto) {
        Log.d("ImageDescViewModel", "Loaded")
        randomImageData.value = image
    }

    fun setNetworkStatus(networkStatus: NetworkStatus) {
        if (networkStatus != networkStatusData.value) networkStatusData.value = networkStatus
    }
}