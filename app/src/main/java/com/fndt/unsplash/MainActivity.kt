package com.fndt.unsplash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.fndt.unsplash.databinding.MainActivityBinding
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.util.setupWithNavController
import com.fndt.unsplash.viewmodels.MainActivityViewModel
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: MainActivityBinding
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setupBottomNavigationBar()
        setContentView(binding.root)
        val viewModelFactory = (application as UnsplashApplication).component.getActivityViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        binding.bottomNavigationView.selectedItemId = R.id.random_image
    }

    private fun setupBottomNavigationBar() {
        val liveData = binding.bottomNavigationView.setupWithNavController(
            navGraphIds = listOf(
                R.navigation.random_image_nav_graph,
                R.navigation.search_fragment_nav_graph,
                R.navigation.collections_fragment_nav_graph
            ),
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_fragment,
        )
        currentNavController = liveData
    }
}