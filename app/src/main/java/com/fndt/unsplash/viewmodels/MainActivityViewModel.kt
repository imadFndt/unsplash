package com.fndt.unsplash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.fndt.unsplash.model.UnsplashPhoto

class MainActivityViewModel : ViewModel() {
    var currentController: LiveData<NavController>? = null

    val searchSelectedItem: LiveData<UnsplashPhoto?> get() = selectedItemData
    private val selectedItemData = MutableLiveData<UnsplashPhoto?>()

    fun selectItem(image: UnsplashPhoto?) {
        selectedItemData.value = image
    }
}