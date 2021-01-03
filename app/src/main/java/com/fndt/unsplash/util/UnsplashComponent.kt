package com.fndt.unsplash.util

import com.fndt.unsplash.viewmodels.MainActivityViewModel
import com.fndt.unsplash.viewmodels.RandomImageFragmentViewModel
import com.fndt.unsplash.viewmodels.SearchFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [UnsplashModule::class])
interface UnsplashComponent {
    fun getActivityViewModelFactory(): MainActivityViewModel.Factory
    fun getSearchFragmentModelFactory(): SearchFragmentViewModel.Factory
    fun getRandomImageViewModelFactory(): RandomImageFragmentViewModel.Factory
}