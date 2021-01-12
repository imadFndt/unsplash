package com.fndt.unsplash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fndt.unsplash.R
import com.fndt.unsplash.model.*

class MainActivityViewModel : ViewModel() {
    val navState: LiveData<NavState?> get() = navStateData
    val searchSelectedItem: LiveData<UnsplashPhoto?> get() = selectedItemData
    val selectedCollection: LiveData<UnsplashCollection?> get() = selectedCollectionData
    val collectionSelectedItem: LiveData<UnsplashPhoto?> get() = collectionSelectedItemData

    private val navStateData = MutableLiveData<NavState?>()
    private val selectedItemData = MutableLiveData<UnsplashPhoto?>()
    private val selectedCollectionData = MutableLiveData<UnsplashCollection?>()
    private val collectionSelectedItemData = MutableLiveData<UnsplashPhoto?>()

    private val collectionNavState = CollectionNavState()
    private val searchNavState = SearchNavState()

    fun selectSearchItem(image: UnsplashPhoto?) {
        selectedItemData.value = image
        searchNavState.setSelectedImage(image)
        navStateData.value = searchNavState
    }

    fun selectCollection(collection: UnsplashCollection?) {
        selectedCollectionData.value = collection
        collectionNavState.setCollection(collection)
        navStateData.value = collectionNavState
    }

    fun selectCollectionItem(image: UnsplashPhoto?) {
        collectionSelectedItemData.value = image
        collectionNavState.setImage(image)
        navStateData.value = collectionNavState
    }

    fun getCurrentGraph() =
        navState.value?.graphId ?: run { R.id.random_image_nav_graph }

    fun setNavState(id: Int) {
        navStateData.value = when (id) {
            R.id.collections_fragment_nav_graph -> collectionNavState
            R.id.search_fragment_nav_graph -> searchNavState
            else -> null
        }
    }
}