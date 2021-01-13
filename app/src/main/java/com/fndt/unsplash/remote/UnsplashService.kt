package com.fndt.unsplash.remote

import com.fndt.unsplash.model.UnsplashCollection
import com.fndt.unsplash.model.UnsplashPhoto
import com.fndt.unsplash.model.UnsplashSearchResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface UnsplashService {
    @GET("/photos/random")
    suspend fun getRandom(): UnsplashPhoto

    @GET("/search/photos")
    suspend fun getSearchList(@QueryMap map: Map<String, String>): UnsplashSearchResult

    @GET("/collections")
    suspend fun getCollectionsList(@QueryMap map: Map<String, String>): List<UnsplashCollection>

    @GET("/collections/{id}/photos")
    suspend fun getCollection(
        @Path("id") collectionId: String, @QueryMap map: Map<String, String>
    ): List<UnsplashPhoto>
}