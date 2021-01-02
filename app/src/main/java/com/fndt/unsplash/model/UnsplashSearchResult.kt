package com.fndt.unsplash.model

import com.google.gson.annotations.SerializedName

data class UnsplashSearchResult(
    var total: Int,
    @SerializedName("total_pages")
    var totalPages: Int,
    var results: MutableList<UnsplashPhoto>,
) {
    var pagesLoaded: Int = 0
}