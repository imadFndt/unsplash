package com.fndt.unsplash.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.util.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.fndt.unsplash.R
import com.fndt.unsplash.adapters.PagerAdapter
import com.fndt.unsplash.databinding.SearchFragmentBinding
import com.fndt.unsplash.model.NetworkStatus
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.MainActivityViewModel
import com.fndt.unsplash.viewmodels.SearchFragmentViewModel
import com.google.android.material.tabs.TabLayoutMediator


class SearchFragment : Fragment() {
    private lateinit var binding: SearchFragmentBinding
    private val viewModel: SearchFragmentViewModel by viewModels {
        (requireActivity().application as UnsplashApplication).component.getSearchFragmentModelFactory()
    }
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var isToastDisplayed = false

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            viewModel.setText(s.toString())
        }
    }
    private val pagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.photos.value?.get(position)?.let { return }
            viewModel.currentSearchText.value?.let { viewModel.requestSearch(it, position) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PagerAdapter()
        adapter.onListItemClickListener = { activityViewModel.selectItem(it) }
        binding.searchPager.adapter = adapter
        binding.searchPager.registerOnPageChangeCallback(pagerCallback)
        TabLayoutMediator(binding.tabs, binding.searchPager) { tab, index ->
            tab.text = (index + 1).toString()
        }.attach()

        binding.searchEditText.setText(viewModel.currentSearchText.value)
        binding.searchEditText.addTextChangedListener(searchTextWatcher)
        binding.searchEditText.setOnEditorActionListener { _, actionId, event ->
            viewModel.setText(binding.searchEditText.text.toString())
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchTextLayout.clearFocus()
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        viewModel.currentSearchText.observe(viewLifecycleOwner) {
            viewModel.requestSearch(it, 0)
        }

        viewModel.photos.observe(viewLifecycleOwner) { result ->
            binding.messageTextView.text = result?.let {
                resources.getText(R.string.nothing_found)
            } ?: run {
                resources.getText(R.string.searched_pictures_will_be_shown_here)
            }
            binding.searchPager.isVisible = result != null
            binding.messageTextView.isVisible = result == null || result.isEmpty()
            result ?: return@observe
            adapter.setData(result)
        }
        viewModel.networkStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                NetworkStatus.FAILURE -> {
                    if (!isToastDisplayed) {
                        Toast.makeText(context, R.string.bad_network, Toast.LENGTH_SHORT).show()
                        isToastDisplayed = true
                    }
                }
                NetworkStatus.SUCCESS -> {
                    isToastDisplayed = false
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.searchEditText.removeTextChangedListener(searchTextWatcher)
        binding.searchPager.unregisterOnPageChangeCallback(pagerCallback)
        super.onDestroyView()
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager? = context?.getSystemService()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}