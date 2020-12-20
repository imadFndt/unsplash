package com.fndt.unsplash.remote

import com.fndt.unsplash.model.UnsplashPhoto
import retrofit2.http.GET

interface UnsplashService {
    @GET("/photos/random")
    suspend fun getRandom(): UnsplashPhoto
}