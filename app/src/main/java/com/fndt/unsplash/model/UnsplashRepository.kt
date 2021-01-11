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
    val collections: LiveData<ListPage<UnsplashCollection>> get() = collectionsListData
    val networkStatus: LiveData<NetworkStatus> get() = networkStatusData
    val search: LiveData<SearchProcess> get() = searchData
    val selectedCollectionImages: LiveData<SearchProcess> get() = selectedCollectionImagesData


    private val randomPhotoData = MutableLiveData<UnsplashPhoto>()
    private val networkStatusData = MutableLiveData(NetworkStatus.SUCCESS)
    private val collectionsListData = MutableLiveData<ListPage<UnsplashCollection>>()
    private val selectedCollectionImagesData = MutableLiveData<SearchProcess>()
    private val searchData = MutableLiveData<SearchProcess>()

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
            searchData.postValue(SearchProcess.Idle)
            return@withContext
        }
        val pageList: SparseArray<ListPage<UnsplashPhoto>?>
        var totalPages: Int?
        when {
            searchData.value !is SearchProcess.Running || reset -> {
                pageList = SparseArray()
                totalPages = null
            }
            else -> {
                pageList = (searchData.value as SearchProcess.Running).pages
                totalPages =
                    (searchData.value as SearchProcess.Running).totalPages
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

    suspend fun requestCollections(page: Int, reset: Boolean) = withContext(Dispatchers.IO) {
        val currentList: List<UnsplashCollection>? = collectionsListData.value?.items
        val listPage: MutableList<UnsplashCollection> =
            currentList?.let { if (reset) mutableListOf() else it.toMutableList() }
                ?: run { mutableListOf() }
        try {
            collectionsListData.postValue(ListPage(NetworkStatus.PENDING, null))
            listPage.addAll(getCollections(page))
            collectionsListData.postValue(ListPage(NetworkStatus.SUCCESS, listPage))
        } catch (e: CancellationException) {
            collectionsListData.postValue(ListPage(NetworkStatus.SUCCESS, null))
        } catch (e: Exception) {
            collectionsListData.postValue(ListPage(NetworkStatus.FAILURE, null))
        }
    }

    suspend fun requestCollectionImages(collection: UnsplashCollection, page: Int, reset: Boolean) =
        withContext(Dispatchers.IO) {
            val pageList: SparseArray<ListPage<UnsplashPhoto>?>
            var totalPages: Int?
            when {
                selectedCollectionImagesData.value !is SearchProcess.Running || reset -> {
                    pageList = SparseArray()
                    totalPages = null
                }
                else -> {
                    pageList = (selectedCollectionImagesData.value as SearchProcess.Running).pages
                    totalPages =
                        (selectedCollectionImagesData.value as SearchProcess.Running).totalPages
                }
            }
            try {
                pageList.put(page, (ListPage(NetworkStatus.PENDING, null)))
                selectedCollectionImagesData.postValue(SearchProcess.Running(pageList, totalPages))

                val result = getCollectionImages(collection.id, page)

                if (result.isNotEmpty()) {
                    totalPages = collection.totalPhotos / PER_PAGE_IMAGES
                    pageList.put(page, ListPage(NetworkStatus.SUCCESS, result))
                    selectedCollectionImagesData.postValue(SearchProcess.Running(pageList, totalPages))
                } else {
                    selectedCollectionImagesData.postValue(SearchProcess.NothingFound)
                }
            } catch (e: CancellationException) {
                pageList.put(page, ListPage(NetworkStatus.SUCCESS, null))
                selectedCollectionImagesData.postValue(SearchProcess.Running(pageList, totalPages))
            } catch (e: Exception) {
                if (pageList.isNotEmpty() && pageList.valueAt(0)?.networkStatus == NetworkStatus.PENDING) {
                    selectedCollectionImagesData.postValue(SearchProcess.Failure)
                } else {
                    pageList.put(page, ListPage(NetworkStatus.FAILURE, null))
                    selectedCollectionImagesData.postValue(SearchProcess.Running(pageList, totalPages))
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

    sealed class SearchProcess {
        data class Running(val pages: SparseArray<ListPage<UnsplashPhoto>?>, val totalPages: Int?) :
            SearchProcess()

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