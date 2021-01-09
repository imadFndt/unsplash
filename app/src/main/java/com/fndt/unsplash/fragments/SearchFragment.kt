package com.fndt.unsplash.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.fndt.unsplash.R
import com.fndt.unsplash.adapters.ImageListAdapter
import com.fndt.unsplash.adapters.PagerAdapter
import com.fndt.unsplash.databinding.SearchFragmentBinding
import com.fndt.unsplash.model.UnsplashRepository.SearchProcess
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

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            viewModel.setText(s.toString())
        }
    }
    private val pagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.currentSearchPage = position
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
        binding.recyclerLayout.placeholder.setImageDrawable(ImageListAdapter.circularDrawable(requireContext()))

        val adapter = PagerAdapter()
        adapter.onListItemClickListener = { activityViewModel.selectItem(it) }
        adapter.onListScrollListener = { hideKeyboardAndClearTextFocus() }
        adapter.onUpdatePageListener = { viewModel.loadIfAbsent(it) }
        binding.recyclerLayout.searchPager.adapter = adapter
        TabLayoutMediator(binding.recyclerLayout.tabs, binding.recyclerLayout.searchPager) { tab, index ->
            tab.text = (index + 1).toString()
        }.attach()
        binding.recyclerLayout.searchPager.registerOnPageChangeCallback(pagerCallback)

        binding.searchEditText.setText(viewModel.currentSearchText.value)
        binding.searchEditText.addTextChangedListener(searchTextWatcher)
        binding.searchEditText.setOnEditorActionListener { _, actionId, event ->
            viewModel.setText(binding.searchEditText.text.toString())
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboardAndClearTextFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        viewModel.search.observe(viewLifecycleOwner) { status ->
            updateTextMessage(status)
            binding.recyclerLayout.searchPager.isVisible = status is SearchProcess.Running && status.hasData()
            binding.recyclerLayout.tabs.isVisible = status is SearchProcess.Running && status.hasData()
            binding.recyclerLayout.messageTextView.isVisible = status !is SearchProcess.Running
            binding.recyclerLayout.placeholder.isVisible = status.isFirstPageLoading()
            if (status.hasData()) adapter.setData(status as SearchProcess.Running)
            if (binding.recyclerLayout.searchPager.currentItem != viewModel.currentSearchPage) {
                binding.recyclerLayout.searchPager.currentItem = viewModel.currentSearchPage
            }
        }
    }

    override fun onDestroyView() {
        binding.searchEditText.removeTextChangedListener(searchTextWatcher)
        binding.recyclerLayout.searchPager.unregisterOnPageChangeCallback(pagerCallback)
        super.onDestroyView()
    }

    private fun updateTextMessage(status: SearchProcess) {
        binding.recyclerLayout.messageTextView.text = when (status) {
            is SearchProcess.Idle, is SearchProcess.Running -> resources.getText(R.string.searched_pictures_will_be_shown_here)
            is SearchProcess.NothingFound -> resources.getText(R.string.nothing_found)
            is SearchProcess.Failure -> resources.getText(R.string.bad_network)
        }
    }


    private fun hideKeyboardAndClearTextFocus() {
        binding.searchTextLayout.clearFocus()
        val imm: InputMethodManager? = context?.getSystemService()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}

