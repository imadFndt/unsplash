package com.fndt.unsplash.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fndt.unsplash.remote.UnsplashService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(private val unsplashService: UnsplashService) {
    val randomPhoto: LiveData<UnsplashPhoto> get() = randomPhotoData
    private val randomPhotoData = MutableLiveData<UnsplashPhoto>()

    suspend fun requestRandomPhoto() = withContext(Dispatchers.IO) {
        try {
            randomPhotoData.postValue(unsplashService.getRandom())
        } catch (e: Exception) {
            //TODO
        }
    }
}
