package com.fndt.unsplash.model

import com.google.gson.annotations.SerializedName

data class UnsplashCollection(
    override var id: String,
    val title: String,
    @SerializedName("total_photos")
    val totalPhotos: Int,
    @SerializedName("cover_photo")
    val coverPhoto: UnsplashPhoto,
    val private: Boolean
) : UnsplashItems
