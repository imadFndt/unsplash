package com.fndt.unsplash.util

import com.fndt.unsplash.MainActivityViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [UnsplashModule::class])
interface UnsplashComponent {
    fun getActivityViewModelFactory(): MainActivityViewModel.Factory
}