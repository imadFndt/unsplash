package com.fndt.unsplash.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fndt.unsplash.R
import com.fndt.unsplash.adapters.SearchListAdapter
import com.fndt.unsplash.databinding.SearchFragmentBinding
import com.fndt.unsplash.util.UnsplashApplication
import com.fndt.unsplash.viewmodels.MainActivityViewModel
import com.fndt.unsplash.viewmodels.SearchFragmentViewModel


class SearchFragment : Fragment() {
    private lateinit var binding: SearchFragmentBinding
    private val activityViewModel: MainActivityViewModel by activityViewModels {
        (requireActivity().application as UnsplashApplication).component.getActivityViewModelFactory()
    }
    private val viewModel: SearchFragmentViewModel by viewModels {
        (requireActivity().application as UnsplashApplication).component.getSearchFragmentModelFactory()
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            viewModel.currentSearchText = s.toString()
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
        val adapter = SearchListAdapter()
        binding.recyclerList.adapter = adapter
        binding.recyclerList.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        with(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)) {
            setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
            binding.recyclerList.addItemDecoration(this)
        }
        with(DividerItemDecoration(context, DividerItemDecoration.VERTICAL)) {
            setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
            binding.recyclerList.addItemDecoration(this)
        }
        binding.recyclerList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> Log.d(
                        "SearchListScrollState", "The RecyclerView is not scrolling"
                    )
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        binding.searchTextLayout.clearFocus()
                        Log.e("a", "Scrolling now")
                        hideKeyboard()
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> Log.d("SearchListScrollState", "Scroll Settling")
                }
            }
        })

        binding.searchEditText.addTextChangedListener(searchTextWatcher)
        binding.searchEditText.setOnEditorActionListener { _, actionId, event ->
            viewModel.currentSearchText = binding.searchEditText.text.toString()
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchTextLayout.clearFocus()
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        viewModel.photos.observe(viewLifecycleOwner) { result ->
            binding.messageTextView.text = result?.let {
                resources.getText(R.string.nothing_found)
            } ?: run {
                resources.getText(R.string.searched_pictures_will_be_shown_here)
            }
            binding.messageTextView.isVisible = result.results.isEmpty() || result == null
            result ?: return@observe
            adapter.setItems(result)
        }
    }

    override fun onDestroyView() {
        binding.searchEditText.removeTextChangedListener(searchTextWatcher)
        super.onDestroyView()
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager? = context?.getSystemService()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}