package com.fndt.unsplash.model

import com.fndt.unsplash.R

class SearchNavState : NavState() {
    override val destinations = listOf(R.id.search, R.id.image_details)
    override val forwardDirections = listOf(R.id.search_to_image_details, null)
    override var actualDestinationId: Int = destinations[0]
    override var graphId = R.id.search_fragment_nav_graph

    private var currentSelectedImage: UnsplashPhoto? = null

    fun setSelectedImage(unsplashPhoto: UnsplashPhoto?) {
        currentSelectedImage = unsplashPhoto
        updateCurrentDestination()
    }

    private fun updateCurrentDestination() {
        actualDestinationId = currentSelectedImage?.let { R.id.image_details } ?: run { R.id.search }
    }
}