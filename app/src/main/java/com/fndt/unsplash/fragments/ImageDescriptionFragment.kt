package com.fndt.unsplash.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fndt.unsplash.R
import com.fndt.unsplash.databinding.DescriptionFragmentBinding
import com.fndt.unsplash.viewmodels.ImageDescriptionFragmentViewModel
import com.squareup.picasso.Picasso


class ImageDescriptionFragment : Fragment() {
    private lateinit var binding: DescriptionFragmentBinding
    private lateinit var viewModel: ImageDescriptionFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DescriptionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity()).get(ImageDescriptionFragmentViewModel::class.java)
        viewModel.image.observe(viewLifecycleOwner) { image ->
            binding.image.let { Picasso.get().load(image.urls.regular).into(it) }
            binding.widthValue.text = resources.getString(R.string.value_pixels, image.width.toString())
            binding.heightValue.text = resources.getString(R.string.value_pixels, image.height.toString())
            binding.imageLinkButton.setOnClickListener { openLink(image.urls.full) }
            binding.descriptionValue.text = image.description
        }
    }

    private fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}