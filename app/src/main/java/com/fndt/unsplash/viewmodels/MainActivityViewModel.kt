package com.fndt.unsplash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.fndt.unsplash.model.UnsplashCollection
import com.fndt.unsplash.model.UnsplashPhoto

class MainActivityViewModel : ViewModel() {
    var currentController: LiveData<NavController>? = null
    val searchSelectedItem: LiveData<UnsplashPhoto?> get() = selectedItemData
    val selectedCollection: LiveData<UnsplashCollection?> get() = selectedCollectionData
    val collectionSelectedItem: LiveData<UnsplashPhoto?> get() = collectionSelectedItemData

    private val selectedItemData = MutableLiveData<UnsplashPhoto?>()
    private val selectedCollectionData = MutableLiveData<UnsplashCollection?>()
    private val collectionSelectedItemData = MutableLiveData<UnsplashPhoto?>()

    fun selectSearchItem(image: UnsplashPhoto?) {
        selectedItemData.value = image
    }

    fun selectCollection(collection: UnsplashCollection?) {
        selectedCollectionData.value = collection
    }

    fun selectCollectionItem(image: UnsplashPhoto?) {
        collectionSelectedItemData.value = image
    }
}