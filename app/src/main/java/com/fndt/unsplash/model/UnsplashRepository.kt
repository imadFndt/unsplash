package com.fndt.unsplash.model

import android.util.Log
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

    private val randomPhotoData = MutableLiveData<UnsplashPhoto>()
    private val searchListData = MutableLiveData<UnsplashSearchResult>()

    suspend fun requestRandomPhoto() = withContext(Dispatchers.IO) {
        try {
            randomPhotoData.postValue(unsplashService.getRandom())
        } catch (e: Exception) {
            //TODO
        }
    }

    suspend fun requestSearch(query: String, page: Int) = withContext(Dispatchers.IO) {
        try {
            val map = mutableMapOf<String, String>().apply {
                put("query", query)
                put("per_page", PER_PAGE.toString())
                put("page", page.toString())
            }
            val list = unsplashService.getList(map)
            list.pagesLoaded += 1
            searchListData.postValue(list)
        } catch (e: Exception) {
            Log.e("TODO", "TODO")
            //TODO
        }
    }

    suspend fun requestAdd(searchQuery: String) = withContext(Dispatchers.IO) {
        try {
            val currentValue = searchList.value
            val map = mutableMapOf<String, String>().apply {
                put("query", searchQuery)
                put("per_page", PER_PAGE.toString())
                currentValue?.let { put("page", (it.pagesLoaded + 1).toString()) }
            }
            currentValue?.let { value ->
                val list = unsplashService.getList(map).results
                value.results.addAll(list)
                value.pagesLoaded += 1
                searchListData.postValue(value)
            }
        } catch (e: Exception) {
            Log.e("TODO", "TODO")
            //TODO
        }
    }
}

const val PER_PAGE = 21
