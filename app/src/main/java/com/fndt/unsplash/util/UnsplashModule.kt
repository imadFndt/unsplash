package com.fndt.unsplash.util

import android.content.Context
import com.fndt.unsplash.model.UnsplashRepository
import com.fndt.unsplash.remote.UnsplashServiceProvider
import com.fndt.unsplash.viewmodels.MainActivityViewModel
import com.fndt.unsplash.viewmodels.RandomImageFragmentViewModel
import com.fndt.unsplash.viewmodels.SearchFragmentViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UnsplashModule(private val context: Context) {
    @Provides
    fun context() = context

    @Provides
    @Singleton
    fun remote() = UnsplashServiceProvider.unsplashService

    @Provides
    fun viewModelFactory(repository: UnsplashRepository) = MainActivityViewModel.Factory(repository)

    @Provides
    fun searchViewModelFactory(repository: UnsplashRepository) = SearchFragmentViewModel.Factory(repository)

    @Provides
    fun randomImageViewModelFactory(repository: UnsplashRepository) =
        RandomImageFragmentViewModel.Factory(repository)
}
