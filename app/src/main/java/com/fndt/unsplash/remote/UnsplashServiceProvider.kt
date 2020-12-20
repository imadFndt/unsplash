package com.fndt.unsplash.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UnsplashServiceProvider {
    private const val GITHUB_URL = "https://api.unsplash.com/"
    private const val ACCESS_KEY = "mNETbr-Exmpun3tlaBNeoOsAohheidbgZRKAiAvBQfk"

    val unsplashService: UnsplashService by lazy {
        retrofitBuilder.build().create(UnsplashService::class.java)
    }

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(GITHUB_URL)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
    }

    private val okHttpClientBuilder by lazy {
        OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Client-ID $ACCESS_KEY")
                .build()
            chain.proceed(request)
        }
    }
}