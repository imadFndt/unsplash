package com.fndt.unsplash.model

data class ListPage<T>(
    var networkStatus: NetworkStatus,
    var items: List<T>?,
)