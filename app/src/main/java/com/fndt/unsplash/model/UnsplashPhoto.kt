package com.fndt.unsplash.model

data class UnsplashPhoto(
    var id: String,
    var urls: UnsplashUrls,
    var width: Int,
    var height: Int,
    var description: String
)
