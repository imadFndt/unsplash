package com.fndt.unsplash.viewmodels

import android.util.SparseArray
import androidx.lifecycle.*
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.model.UnsplashRepository
import com.fndt.unsplash.model.UnsplashSearchResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class SearchFragmentViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val currentStatus: LiveData<RepositoryData> get() = repositoryData
    val currentSearchText: LiveData<String> get() = currentSearchTextData

    var currentSearchPage: Int = NO_PAGE

    private var needUpdate: Boolean = false
    private val repositoryData = MediatorLiveData<RepositoryData>()
    private val currentSearchTextData = MutableLiveData<String>()

    private var currentJob: Job? = null
    private var previousJob: Job? = null

    init {
        val photos: LiveData<SparseArray<UnsplashSearchResult>?> = repository.searchList.switchMap { list ->
            list ?: return@switchMap MutableLiveData(null)
            val sparseArray = SparseArray<UnsplashSearchResult>()
            list.forEach { sparseArray.append(it.page, it) }
            MutableLiveData(sparseArray)
        }
        repositoryData.addSource(photos) { map ->
            needUpdate = false
            repositoryData.value = RepositoryData(map, repository.networkStatus.value)
        }
        repositoryData.addSource(repository.networkStatus) { status ->
            repositoryData.value = RepositoryData(photos.value, status)
        }
    }

    fun setText(string: String) {
        needUpdate = true
        currentSearchTextData.value = string
        requestSearch(string, 0)
    }

    fun loadIfAbsent(position: Int) {
        if (repositoryData.value?.pages?.get(position) != null && !needUpdate) return
        currentSearchText.value?.let { requestSearch(it, if (needUpdate) 0 else position) }
    }

    private fun requestSearch(query: String, page: Int) {
        previousJob = currentJob
        currentJob = viewModelScope.launch {
            previousJob?.cancelAndJoin()
            repository.requestSearch(query, page, page == 0 || needUpdate)
        }
    }

    data class RepositoryData(
        val pages: SparseArray<UnsplashSearchResult>?,
        val networkStatus: NetworkStatus?
    )

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchFragmentViewModel(repository) as T
        }
    }
}

const val NO_PAGE = -1