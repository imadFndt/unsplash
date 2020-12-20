package com.fndt.unsplash.util

import android.app.Application

class UnsplashApplication : Application() {
    val component: UnsplashComponent =
        DaggerUnsplashComponent.builder().unsplashModule(UnsplashModule(this)).build()
}

