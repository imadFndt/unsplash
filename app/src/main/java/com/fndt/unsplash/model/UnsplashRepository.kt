package com.fndt.unsplash.model

import android.util.SparseArray
import androidx.core.util.isNotEmpty
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fndt.unsplash.remote.UnsplashService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(private val unsplashService: UnsplashService) {
    val randomPhoto: LiveData<UnsplashPhoto> get() = randomPhotoData
    val collections: LiveData<MutableList<UnsplashCollection>> get() = collectionsListData
    val networkStatus: LiveData<NetworkStatus> get() = randomPhotoNetworkStatusData
    val search: LiveData<SearchProcess> get() = searchData

    private val randomPhotoData = MutableLiveData<UnsplashPhoto>()
    private val randomPhotoNetworkStatusData = MutableLiveData(NetworkStatus.SUCCESS)

    private val collectionsListData = MutableLiveData<MutableList<UnsplashCollection>>()
    private val searchData = MutableLiveData<SearchProcess>()

    suspend fun requestRandomPhoto() = withContext(Dispatchers.IO) {
        randomPhotoNetworkStatusData.postValue(NetworkStatus.PENDING)
        try {
            randomPhotoData.postValue(unsplashService.getRandom())
            randomPhotoNetworkStatusData.postValue(NetworkStatus.SUCCESS)
        } catch (e: Exception) {
            randomPhotoNetworkStatusData.postValue(NetworkStatus.FAILURE)
        }
    }

    suspend fun requestSearch(query: String, page: Int, reset: Boolean) = withContext(Dispatchers.IO) {
        if (query.isEmpty()) {
            searchData.postValue(SearchProcess.Idle)
            return@withContext
        }
        val pageList: SparseArray<ListPage?>
        var totalPages: Int?
        when {
            searchData.value !is SearchProcess.Running || reset -> {
                pageList = SparseArray<ListPage?>()
                totalPages = null
            }
            else -> {
                pageList = (searchData.value as SearchProcess.Running).pages
                totalPages = (searchData.value as SearchProcess.Running).totalPages
            }
        }
        try {
            pageList.put(page, (ListPage(NetworkStatus.PENDING, null)))
            searchData.postValue(SearchProcess.Running(pageList, totalPages))

            val result = searchList(query, page)

            if (result.results.isNotEmpty()) {
                totalPages = result.totalPages
                pageList.put(page, ListPage(NetworkStatus.SUCCESS, result.results))
                searchData.postValue(SearchProcess.Running(pageList, totalPages))
            } else {
                searchData.postValue(SearchProcess.NothingFound)
            }
        } catch (e: CancellationException) {
            pageList.put(page, ListPage(NetworkStatus.SUCCESS, null))
            searchData.postValue(SearchProcess.Running(pageList, totalPages))
        } catch (e: Exception) {
            if (pageList.isNotEmpty() && pageList.valueAt(0)?.networkStatus == NetworkStatus.PENDING) {
                searchData.postValue(SearchProcess.Failure)
            } else {
                pageList.put(page, ListPage(NetworkStatus.FAILURE, null))
                searchData.postValue(SearchProcess.Running(pageList, totalPages))
            }
        }
    }

    private suspend fun searchList(query: String, page: Int): UnsplashSearchResult {
        val map = mutableMapOf<String, String>().apply {
            put("query", query)
            put("per_page", PER_PAGE_IMAGES.toString())
            put("page", (page + 1).toString())
        }
        return unsplashService.getSearchList(map)
    }

    suspend fun requestCollections(page: Int, reset: Boolean) = withContext(Dispatchers.IO) {
        try {
            randomPhotoNetworkStatusData.postValue(NetworkStatus.PENDING)
            val map = mutableMapOf<String, String>().apply {
                put("page", (page + 1).toString())
            }
            val result = unsplashService.getCollectionsList(map)

            if (reset) {
                collectionsListData.postValue(result.toMutableList())
            } else {
                val updated = collectionsListData.value
                updated?.addAll(result)
                collectionsListData.postValue(updated)
            }
            randomPhotoNetworkStatusData.postValue(NetworkStatus.SUCCESS)
        } catch (e: CancellationException) {
            randomPhotoNetworkStatusData.postValue(NetworkStatus.SUCCESS)
        } catch (e: Exception) {
            randomPhotoNetworkStatusData.postValue(NetworkStatus.FAILURE)
        }
    }

    sealed class SearchProcess {
        data class Running(val pages: SparseArray<ListPage?>, val totalPages: Int?) : SearchProcess()
        object NothingFound : SearchProcess()
        object Failure : SearchProcess()
        object Idle : SearchProcess()

        fun isFirstPageLoading() = this is Running
                && pages.size() == 1 && pages.valueAt(0)?.networkStatus == NetworkStatus.PENDING

        fun hasData() =
            this is Running && pages.size() >= 1 && pages.valueAt(0)?.networkStatus == NetworkStatus.SUCCESS && totalPages != null

        fun hasDataAtPage(page: Int) =
            this is Running && pages.size() >= 1 && pages.get(page)?.items?.isNotEmpty() == true
    }
}

const val PER_PAGE_IMAGES = 21
