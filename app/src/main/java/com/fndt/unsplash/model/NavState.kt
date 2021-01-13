package com.fndt.unsplash.model

import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController

abstract class NavState : MutableLiveData<NavState>() {
    abstract var actualDestinationId: Int
    abstract var graphId: Int
    protected abstract val destinations: List<Int?>
    protected abstract val forwardDirections: List<Int?>

    fun navigateToDestination(navController: NavController) {
        val currentId = navController.currentDestination?.id
        currentId ?: return
        val currentIndex = destinations.indexOf(currentId)
        val destinationIndex = destinations.indexOf(actualDestinationId)
        val distance = destinationIndex - currentIndex
        for (i in if (distance > 0) currentIndex until destinationIndex else currentIndex downTo (destinationIndex + 1)) {
            val actionId = if (distance > 0) forwardDirections[i] else null
            actionId?.let { navController.navigate(it) } ?: run { navController.navigateUp() }
        }
    }
}