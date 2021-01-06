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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.fndt.unsplash.R
import com.fndt.unsplash.adapters.PagerAdapter
import com.fndt.unsplash.adapters.SearchListAdapter
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
            viewModel.currentSearchPage = position
            viewModel.loadIfAbsent(position)
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
        binding.placeholder.setImageDrawable(SearchListAdapter.circularDrawable(requireContext()))

        val adapter = PagerAdapter()
        adapter.onListItemClickListener = { activityViewModel.selectItem(it) }
        adapter.onListScrollListener = { hideKeyboardAndClearTextFocus() }
        binding.searchPager.adapter = adapter
        TabLayoutMediator(binding.tabs, binding.searchPager) { tab, index ->
            tab.text = (index + 1).toString()
        }.attach()
        binding.searchPager.registerOnPageChangeCallback(pagerCallback)

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

        viewModel.currentStatus.observe(viewLifecycleOwner) { status ->
            binding.searchPager.isVisible = status.pages != null
            binding.tabs.isVisible = status.pages != null
            binding.messageTextView.isVisible =
                status.pages == null && status.networkStatus != NetworkStatus.PENDING
            binding.placeholder.isVisible = status.networkStatus == NetworkStatus.PENDING
            if (status.networkStatus == NetworkStatus.SUCCESS && status.pages != null) adapter.setData(status.pages)
            status.networkStatus?.let { updateTextAndToastMessages(it) }
            if (binding.searchPager.currentItem != viewModel.currentSearchPage) {
                binding.searchPager.currentItem = viewModel.currentSearchPage
            }
        }
    }

    override fun onDestroyView() {
        binding.searchEditText.removeTextChangedListener(searchTextWatcher)
        binding.searchPager.unregisterOnPageChangeCallback(pagerCallback)
        super.onDestroyView()
    }

    private fun updateTextAndToastMessages(status: NetworkStatus) {
        when (status) {
            NetworkStatus.FAILURE -> {
                if (!isToastDisplayed) {
                    Toast.makeText(context, R.string.bad_network, Toast.LENGTH_SHORT).show()
                    isToastDisplayed = true
                }
                binding.messageTextView.text = resources.getText(R.string.bad_network)
            }
            NetworkStatus.SUCCESS_NOTHING_FOUND -> {
                isToastDisplayed = false
                binding.messageTextView.text = resources.getText(R.string.nothing_found)
            }
            NetworkStatus.SUCCESS -> {
                isToastDisplayed = false
                binding.messageTextView.text =
                    resources.getText(R.string.searched_pictures_will_be_shown_here)
            }
            NetworkStatus.PENDING -> {
                isToastDisplayed = false
            }
        }
    }

    private fun hideKeyboardAndClearTextFocus() {
        binding.searchTextLayout.clearFocus()
        val imm: InputMethodManager? = context?.getSystemService()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}