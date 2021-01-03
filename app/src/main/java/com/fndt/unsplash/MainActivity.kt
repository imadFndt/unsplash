package com.fndt.unsplash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.fndt.unsplash.databinding.MainActivityBinding
import com.fndt.unsplash.util.setupWithNavController
import com.fndt.unsplash.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding = MainActivityBinding.inflate(layoutInflater)

        binding.bottomNavigationView.selectedItemId =
            viewModel.currentController?.value?.graph?.id ?: run { R.id.random_image_nav_graph }
        setupBottomNavigationBar()
        setContentView(binding.root)

        viewModel.searchSelectedItem.observe(this) { image ->
            val currentController = viewModel.currentController?.value
            currentController ?: return@observe

            if (currentController.graph.id == R.id.search_fragment_nav_graph) {
                image?.let {
                    currentController.navigateIfNotHere(R.id.image_details)
                } ?: run {
                    currentController.navigateUp()
                }
            }
        }
    }

    private fun setupBottomNavigationBar() {
        viewModel.currentController = binding.bottomNavigationView.setupWithNavController(
            navGraphIds = listOf(
                R.navigation.random_image_nav_graph,
                R.navigation.search_fragment_nav_graph,
                R.navigation.collections_fragment_nav_graph
            ),
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_fragment,
        )
    }


    private fun NavController.navigateIfNotHere(toId: Int) {
        if (this.currentDestination?.id != toId) navigate(toId)
    }

    override fun onBackPressed() {
        val controller = viewModel.currentController?.value
        controller ?: run {
            super.onBackPressed()
            return
        }
        when (controller.graph.id) {
            R.id.search_fragment_nav_graph -> {
                val item = viewModel.searchSelectedItem.value
                item?.let {
                    viewModel.selectItem(null)
                } ?: run {
                    finish()
                }
            }
            R.id.collections_fragment_nav_graph -> {
                //TODO
            }
            R.id.random_image_nav_graph -> {
                super.onBackPressed()
            }
        }
    }
}
