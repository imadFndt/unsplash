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
    val searchList: LiveData<UnsplashSearchResult> get() = searchListData
    val networkStatus: LiveData<NetworkStatus> get() = networkStatusData

    private val networkStatusData = MutableLiveData(NetworkStatus.SUCCESS)
    private val randomPhotoData = MutableLiveData<UnsplashPhoto>()
    private val searchListData = MutableLiveData<UnsplashSearchResult>()

    suspend fun requestRandomPhoto() = withContext(Dispatchers.IO) {
        networkStatusData.postValue(NetworkStatus.PENDING)
        try {
            randomPhotoData.postValue(unsplashService.getRandom())
            networkStatusData.postValue(NetworkStatus.SUCCESS)
        } catch (e: Exception) {
            networkStatusData.postValue(NetworkStatus.FAILURE)
        }
    }

    suspend fun requestSearch(query: String, page: Int) = withContext(Dispatchers.IO) {
        if (query.isEmpty()) {
            searchListData.postValue(null)
            return@withContext
        }
        try {
            networkStatusData.postValue(NetworkStatus.PENDING)
            val map = mutableMapOf<String, String>().apply {
                put("query", query)
                put("per_page", PER_PAGE.toString())
                put("page", page.toString())
            }
            val list = unsplashService.getList(map)
            //list.pagesLoaded += 1
            searchListData.postValue(list)
            networkStatusData.postValue(NetworkStatus.SUCCESS)
        } catch (e: Exception) {
            networkStatusData.postValue(NetworkStatus.FAILURE)
        }
    }
}

const val PER_PAGE = 21
