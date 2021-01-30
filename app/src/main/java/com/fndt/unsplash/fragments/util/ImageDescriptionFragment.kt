package com.fndt.unsplash.fragments.util

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fndt.unsplash.R
import com.fndt.unsplash.databinding.ImageDescriptionFragmentBinding
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.viewmodels.ImageDescriptionFragmentViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class ImageDescriptionFragment : Fragment() {
    private lateinit var binding: ImageDescriptionFragmentBinding
    private val viewModel: ImageDescriptionFragmentViewModel by activityViewModels()

    private val picassoCallback = object : Callback {
        override fun onSuccess() {
            viewModel.setNetworkStatus(NetworkStatus.SUCCESS)
        }

        override fun onError(e: Exception?) {
            viewModel.setNetworkStatus(NetworkStatus.FAILURE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ImageDescriptionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setNetworkStatus(NetworkStatus.PENDING)
        viewModel.image.observe(viewLifecycleOwner) { newImage ->
            with(binding) {
                image.let { Picasso.get().load(newImage.urls.regular).into(it, picassoCallback) }
                widthValue.text = getString(R.string.value_pixels, newImage.width.toString())
                heightValue.text = getString(R.string.value_pixels, newImage.height.toString())
                imageLinkButton.setOnClickListener { openLink(newImage.urls.full) }
                descriptionValue.text = newImage.description
            }
        }
    }

    private fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}