package com.vairagi.news.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vairagi.news.R
import com.vairagi.news.adapter.NewsAdapter
import com.vairagi.news.databinding.FragmentSearchBinding
import com.vairagi.news.db.ArticleDatabase
import com.vairagi.news.repository.NewsRepository
import com.vairagi.news.ui.NewsViewModel
import com.vairagi.news.ui.NewsViewModelProviderFactory
import com.vairagi.news.util.Constant.Companion.SEARCH_NEWS_TIME_DELAY
import com.vairagi.news.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentSearchBinding
    val TAG = "Search News"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(repository)

        setUpRecyclerView()


        newsAdapter.setOnItemClickListener {
                article ->

            val action = SearchFragmentDirections.actionSearchFragmentToArticleFragment(article)

            findNavController().navigate(action)


        }

        newsViewModel = ViewModelProvider(requireActivity(), viewModelProviderFactory)[NewsViewModel::class.java]

        newsViewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)

                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An Error Occured: $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }


        setUpSearch()




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        return binding.root
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }


    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }


    private fun setUpSearch() {
        val view = binding.etSearch as View
        val searchView: androidx.appcompat.widget.SearchView = view.findViewById(R.id.searchSV)

        var job: Job? = null

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchQuery: String?): Boolean {
                if(searchQuery!=null) {
                    newsViewModel.searchForNews(searchQuery)
                }

                return true
            }

            override fun onQueryTextChange(searchQuery: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_NEWS_TIME_DELAY)
                    if (searchQuery != null) {
                        if(searchQuery.isNotEmpty()) {
                            newsViewModel.searchForNews(searchQuery)
                        }

                    }
                }

                return true
            }

        })

    }


}