package com.fndt.unsplash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.fndt.unsplash.databinding.MainActivityBinding
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.MainActivityViewModel
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModelFactory = (application as UnsplashApplication).component.getActivityViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        viewModel.randomImage.observe(this) {
            Picasso.get().load(it.urls.regular).into(binding.randomImage)
        }
        binding.bottomNavigationView.selectedItemId = R.id.menu_random_image
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_search -> {
                    binding.navFragment.findNavController().navigate(R.id.to_search)
                    true
                }
                R.id.menu_random_image -> {
                    binding.navFragment.findNavController().navigate(R.id.to_random_image)
                    true
                }
                R.id.menu_collection_list -> true
                else -> true
            }
        }
    }
}