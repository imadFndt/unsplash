package com.fndt.unsplash.util

import com.fndt.unsplash.model.NetworkStatus

fun combineStatus(
    networkStatus: NetworkStatus?,
    imageNetworkStatus: NetworkStatus?
): NetworkStatus? {
    imageNetworkStatus ?: return networkStatus
    var status: NetworkStatus = NetworkStatus.SUCCESS
    if (networkStatus == imageNetworkStatus && networkStatus == NetworkStatus.SUCCESS) {
        status = NetworkStatus.SUCCESS
    } else if (networkStatus == NetworkStatus.FAILURE || imageNetworkStatus == NetworkStatus.FAILURE) {
        status = NetworkStatus.FAILURE
    } else if (networkStatus == NetworkStatus.PENDING || imageNetworkStatus == NetworkStatus.PENDING) {
        status = NetworkStatus.PENDING
    }
    return status
}