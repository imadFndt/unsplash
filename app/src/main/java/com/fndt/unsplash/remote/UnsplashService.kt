package com.fndt.unsplash.remote

import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.model.UnsplashSearchResult
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface UnsplashService {
    @GET("/photos/random")
    suspend fun getRandom(): UnsplashPhoto

    @GET("/search/photos")
    suspend fun getList(@QueryMap map: Map<String, String>): UnsplashSearchResult
}