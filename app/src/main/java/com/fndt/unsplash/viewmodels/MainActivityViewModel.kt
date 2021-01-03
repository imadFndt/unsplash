package com.fndt.unsplash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

class MainActivityViewModel : ViewModel() {
    var currentController: LiveData<NavController>? = null
}