package com.fndt.unsplash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fndt.unsplash.databinding.CollectionsFragmentBinding
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.CollectionsViewModel

class CollectionsFragment : Fragment() {
    private val viewModel: CollectionsViewModel by viewModels {
        (requireActivity().application as UnsplashApplication).component.getCollectionsViewModelFactory()
    }

    private lateinit var binding: CollectionsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CollectionsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}