package com.fndt.unsplash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
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

        val controllerData = setupBottomNavigationBar()
        binding.bottomNavigationView.selectedItemId = viewModel.getCurrentGraph()
        setContentView(binding.root)

        controllerData.observe(this) { viewModel.setNavState(it.graph.id) }
        viewModel.navState.observe(this) { navState ->
            controllerData.value?.let { navState?.navigateToDestination(it) }
        }
    }

    private fun setupBottomNavigationBar(): LiveData<NavController> =
        binding.bottomNavigationView.setupWithNavController(
            navGraphIds = listOf(
                R.navigation.search_fragment_nav_graph,
                R.navigation.random_image_nav_graph,
                R.navigation.collections_fragment_nav_graph
            ),
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_fragment,
        )

    override fun onBackPressed() {
        val navState = viewModel.navState.value
        when (navState?.graphId) {
            R.id.search_fragment_nav_graph -> {
                val item = viewModel.searchSelectedItem.value
                item?.let { viewModel.selectSearchItem(null) } ?: run { clearAndFinish() }
            }
            R.id.collections_fragment_nav_graph -> {
                val collection = viewModel.selectedCollection.value
                val image = viewModel.collectionSelectedItem.value
                image?.let {
                    viewModel.selectCollectionItem(null)
                    return
                }
                collection?.let { viewModel.selectCollection(null) } ?: run { clearAndFinish() }
            }
            else -> {
                clearAndFinish()
            }
        }
    }

    private fun clearAndFinish() {
        viewModelStore.clear()
        finish()
    }
}
