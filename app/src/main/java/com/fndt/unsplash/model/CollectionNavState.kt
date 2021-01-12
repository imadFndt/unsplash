package com.fndt.unsplash.model

import com.fndt.unsplash.R


class CollectionNavState : NavState() {
    override val destinations =
        listOf(R.id.collection_list, R.id.collection_image_list, R.id.detailed_image)

    override val forwardDirections =
        listOf(
            R.id.collection_list_to_collection_image_list,
            R.id.collection_image_list_to_detailed_image,
            null
        )
    override val backwardDirections = listOf(
        null,
        R.id.collection_image_list_to_collection_list,
        R.id.detailed_image_to_collection_image_list
    )

    override var actualDestinationId: Int = destinations[0]
    override var graphId = R.id.collections_fragment_nav_graph

    private var selectedImage: UnsplashPhoto? = null
    private var selectedCollection: UnsplashCollection? = null

    fun setImage(unsplashPhoto: UnsplashPhoto?) {
        selectedCollection ?: return
        selectedImage = unsplashPhoto
        updateCurrentDestination()
    }

    fun setCollection(unsplashCollection: UnsplashCollection?) {
        selectedCollection = unsplashCollection
        updateCurrentDestination()
    }

    private fun updateCurrentDestination() {
        actualDestinationId = when {
            selectedImage != null -> R.id.detailed_image
            selectedCollection != null -> R.id.collection_image_list
            else -> R.id.collection_list
        }
    }
}