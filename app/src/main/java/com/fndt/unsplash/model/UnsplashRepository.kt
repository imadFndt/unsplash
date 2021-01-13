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
import kotlin.math.ceil

const val PER_PAGE_IMAGES = 21

@Singleton
class UnsplashRepository @Inject constructor(private val unsplashService: UnsplashService) {
    val randomPhoto: LiveData<UnsplashPhoto> get() = randomPhotoData
    val collections: LiveData<Pair<Int, ListPage<UnsplashCollection>>> get() = collectionsListData
    val networkStatus: LiveData<NetworkStatus> get() = networkStatusData
    val search: LiveData<DataProcess> get() = searchData
    val selectedCollectionImages: LiveData<DataProcess> get() = selectedCollectionImagesData


    private val randomPhotoData = MutableLiveData<UnsplashPhoto>()
    private val networkStatusData = MutableLiveData(NetworkStatus.SUCCESS)
    private val collectionsListData = MutableLiveData<Pair<Int, ListPage<UnsplashCollection>>>()
    private val selectedCollectionImagesData = MutableLiveData<DataProcess>()
    private val searchData = MutableLiveData<DataProcess>()

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
            searchData.postValue(DataProcess.Idle)
            return@withContext
        }
        requestImages(page, reset, searchData) { liveData, pageList ->
            val result = searchList(query, page)
            if (result.results.isNotEmpty()) {
                pageList.put(page, ListPage(NetworkStatus.SUCCESS, result.results))
                liveData.postValue(DataProcess.Running(pageList, result.totalPages))
            } else {
                liveData.postValue(DataProcess.NothingFound)
            }
        }
    }

    suspend fun requestCollections(page: Int, reset: Boolean) = withContext(Dispatchers.IO) {
        val currentList: List<UnsplashCollection>? = collectionsListData.value?.second?.items
        val listPage: MutableList<UnsplashCollection> =
            currentList?.let { if (reset) mutableListOf() else it.toMutableList() }
                ?: run { mutableListOf() }
        try {
            collectionsListData.postValue(
                Pair(page, ListPage(NetworkStatus.PENDING, if (reset) null else listPage))
            )
            listPage.addAll(getCollections(page))
            collectionsListData.postValue(Pair(page, ListPage(NetworkStatus.SUCCESS, listPage)))
        } catch (e: CancellationException) {
            collectionsListData.postValue(Pair(page, ListPage(NetworkStatus.SUCCESS, null)))
        } catch (e: Exception) {
            collectionsListData.postValue(
                Pair(page, ListPage(NetworkStatus.FAILURE, if (listPage.isEmpty()) null else listPage))
            )
        }
    }

    suspend fun requestCollectionImages(collection: UnsplashCollection, page: Int, reset: Boolean) =
        withContext(Dispatchers.IO) {
            requestImages(page, reset, selectedCollectionImagesData) { liveData, pageList ->
                val result = getCollectionImages(collection.id, page)
                if (result.isNotEmpty()) {
                    val totalPages = ceil(collection.totalPhotos.toDouble() / PER_PAGE_IMAGES).toInt()
                    pageList.put(page, ListPage(NetworkStatus.SUCCESS, result))
                    liveData.postValue(DataProcess.Running(pageList, totalPages))
                } else {
                    liveData.postValue(DataProcess.NothingFound)
                }
            }
            return@withContext
        }

    private suspend fun requestImages(
        page: Int,
        reset: Boolean,
        liveData: MutableLiveData<DataProcess>,
        block: suspend (MutableLiveData<DataProcess>, SparseArray<ListPage<UnsplashPhoto>?>) -> Unit
    ) =
        withContext(Dispatchers.IO) {
            val pageList: SparseArray<ListPage<UnsplashPhoto>?>
            val totalPages: Int?
            when {
                liveData.value !is DataProcess.Running || reset -> {
                    pageList = SparseArray()
                    totalPages = null
                }
                else -> {
                    pageList = (liveData.value as DataProcess.Running).pages
                    totalPages = (liveData.value as DataProcess.Running).totalPages
                }
            }
            try {
                pageList.put(page, (ListPage(NetworkStatus.PENDING, null)))
                liveData.postValue(DataProcess.Running(pageList, totalPages))
                block(liveData, pageList)
            } catch (e: CancellationException) {
                pageList.put(page, ListPage(NetworkStatus.SUCCESS, null))
                liveData.postValue(DataProcess.Running(pageList, totalPages))
            } catch (e: Exception) {
                if (pageList.isNotEmpty() && pageList.valueAt(0)?.networkStatus == NetworkStatus.PENDING) {
                    liveData.postValue(DataProcess.Failure)
                } else {
                    pageList.put(page, ListPage(NetworkStatus.FAILURE, null))
                    liveData.postValue(DataProcess.Running(pageList, totalPages))
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

    private suspend fun getCollections(page: Int): List<UnsplashCollection> {
        val map = mutableMapOf<String, String>().apply { put("page", (page + 1).toString()) }
        return unsplashService.getCollectionsList(map)
    }

    private suspend fun getCollectionImages(collectionId: String, page: Int): List<UnsplashPhoto> {
        val map = mutableMapOf<String, String>().apply {
            put("page", (page + 1).toString())
            put("per_page", PER_PAGE_IMAGES.toString())
        }
        return unsplashService.getCollection(collectionId, map)
    }

    sealed class DataProcess {
        data class Running(val pages: SparseArray<ListPage<UnsplashPhoto>?>, val totalPages: Int?) :
            DataProcess()

        object NothingFound : DataProcess()
        object Failure : DataProcess()
        object Idle : DataProcess()

        fun isFirstPageLoading() = this is Running
                && pages.size() == 1 && pages.valueAt(0)?.networkStatus == NetworkStatus.PENDING

        fun hasData() =
            this is Running && pages.size() >= 1 && pages.valueAt(0)?.networkStatus == NetworkStatus.SUCCESS && totalPages != null

        fun hasDataAtPage(page: Int) =
            this is Running && pages.size() >= 1 && pages.get(page)?.items?.isNotEmpty() == true
    }
}