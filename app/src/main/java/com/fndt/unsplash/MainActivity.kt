package com.fndt.unsplash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fndt.unsplash.databinding.MainActivityBinding
import com.fndt.unsplash.util.UnsplashApplication
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
    }
}