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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.fndt.unsplash.R
import com.fndt.unsplash.databinding.SearchFragmentBinding
import com.fndt.unsplash.fragments.util.ImageListFragment
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.MainActivityViewModel
import com.fndt.unsplash.viewmodels.SearchFragmentViewModel
import kotlinx.android.synthetic.main.search_fragment.view.*

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerFragment =
            (childFragmentManager.findFragmentByTag(resources.getString(R.string.image_list_tag)) as ImageListFragment).apply {
                itemClickListener = { activityViewModel.selectSearchItem(it) }
                onRequestUpdateListener = { viewModel.loadIfAbsent(it) }
                onScrollListener = { hideKeyboardAndClearTextFocus() }
                onPageSelectedListener = { viewModel.currentSearchPage = it }
            }

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
            recyclerFragment.setData(status, viewModel.currentSearchPage)
        }
    }

    override fun onDestroyView() {
        binding.searchEditText.removeTextChangedListener(searchTextWatcher)
        (childFragmentManager.findFragmentByTag(resources.getString(R.string.image_list_tag)) as ImageListFragment).apply {
            itemClickListener = null
            onRequestUpdateListener = null
            onScrollListener = null
            onPageSelectedListener = null
        }
        super.onDestroyView()
    }

    private fun hideKeyboardAndClearTextFocus() {
        binding.searchTextLayout.clearFocus()
        val imm: InputMethodManager? = context?.getSystemService()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}

