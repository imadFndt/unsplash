package com.fndt.unsplash.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fndt.unsplash.model.UnsplashPhoto

class ImageDescriptionFragmentViewModel : ViewModel() {
    val image: LiveData<UnsplashPhoto> get() = randomImageData

    private val randomImageData = MutableLiveData<UnsplashPhoto>()

    fun setImage(image: UnsplashPhoto) {
        Log.d("ImageDescViewModel","Loaded")
        randomImageData.value = image
    }
}