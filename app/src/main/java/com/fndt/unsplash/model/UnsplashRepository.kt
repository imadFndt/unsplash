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
    val searchList: LiveData<MutableList<UnsplashSearchResult>> get() = searchListData
    val networkStatus: LiveData<NetworkStatus> get() = networkStatusData

    private val randomPhotoData = MutableLiveData<UnsplashPhoto>()
    private val searchListData = MutableLiveData<MutableList<UnsplashSearchResult>>()
    private val networkStatusData = MutableLiveData(NetworkStatus.SUCCESS)

    suspend fun requestRandomPhoto() = withContext(Dispatchers.IO) {
        networkStatusData.postValue(NetworkStatus.PENDING)
        try {
            randomPhotoData.postValue(unsplashService.getRandom())
            networkStatusData.postValue(NetworkStatus.SUCCESS)
        } catch (e: Exception) {
            networkStatusData.postValue(NetworkStatus.FAILURE)
        }
    }

    suspend fun requestSearch(query: String, page: Int, reset: Boolean) = withContext(Dispatchers.IO) {
        if (query.isEmpty()) {
            searchListData.postValue(null)
            return@withContext
        }
        try {
            networkStatusData.postValue(NetworkStatus.PENDING)
            val map = mutableMapOf<String, String>().apply {
                put("query", query)
                put("per_page", PER_PAGE.toString())
                put("page", (page + 1).toString())
            }
            val result = unsplashService.getList(map)
            result.page = page
            if (reset) {
                searchListData.postValue(mutableListOf(result))
            } else {
                val updated = searchListData.value
                updated?.add(result)
                searchListData.postValue(updated)
            }
            networkStatusData.postValue(NetworkStatus.SUCCESS)
        } catch (e: Exception) {
            networkStatusData.postValue(NetworkStatus.FAILURE)
        }
    }
}

const val PER_PAGE = 21

