package com.fndt.unsplash.model

data class ListPage(
    var networkStatus: NetworkStatus,
    var items: List<UnsplashPhoto>?,
)