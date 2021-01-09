package com.fndt.unsplash.viewmodels

import android.util.SparseArray
import androidx.lifecycle.*
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.model.UnsplashCollection
import com.fndt.unsplash.model.UnsplashRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class CollectionsViewModel(private val repository: UnsplashRepository) : ViewModel() {
    val currentStatus: LiveData<RepositoryData> get() = repositoryData

    private val repositoryData = MediatorLiveData<RepositoryData>()

    private var currentJob: Job? = null
    private var previousJob: Job? = null

    init {
        val collections: LiveData<SparseArray<UnsplashCollection>?> =
            repository.collections.switchMap { list ->
                val sparseArray = SparseArray<UnsplashCollection>()
                list.forEachIndexed { i, collection -> sparseArray.append(i, collection) }
                MutableLiveData(sparseArray)
            }
        repositoryData.addSource(collections) { map ->
            repositoryData.value = RepositoryData(map, repository.networkStatus.value)
        }
        repositoryData.addSource(repository.networkStatus) { status ->
            repositoryData.value = RepositoryData(collections.value, status)
        }
    }

    fun loadIfAbsent(position: Int) {
        repositoryData.value?.pages?.get(position)?.let { return }
        requestCollections(position)
    }

    override fun onCleared() {
        previousJob?.cancel()
        currentJob?.cancel()
    }

    private fun requestCollections(page: Int) {
        previousJob = currentJob
        currentJob = viewModelScope.launch {
            previousJob?.cancelAndJoin()
            repository.requestCollections(page, page == 0)
        }
    }

    data class RepositoryData(
        val pages: SparseArray<UnsplashCollection>?,
        val networkStatus: NetworkStatus?
    )

    class Factory(private val repository: UnsplashRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CollectionsViewModel(repository) as T
        }
    }
}