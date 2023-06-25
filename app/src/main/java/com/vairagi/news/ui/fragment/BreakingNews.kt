package com.vairagi.news.ui.fragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vairagi.news.R
import com.vairagi.news.adapter.NewsAdapter
import com.vairagi.news.databinding.FragmentBreakingNewsBinding
import com.vairagi.news.repository.NewsRepository
import com.vairagi.news.ui.NewsViewModel
import com.vairagi.news.ui.NewsViewModelProviderFactory
import com.vairagi.news.util.Resource


class BreakingNews : Fragment() {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentBreakingNewsBinding
    val TAG = "Breaking News"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = NewsRepository()
        val viewModelProviderFactory = NewsViewModelProviderFactory(repository)

        setUpRecyclerView()

        newsViewModel = ViewModelProvider(requireActivity(), viewModelProviderFactory)[NewsViewModel::class.java]



        newsViewModel.breakingNews.observe(viewLifecycleOwner) { response ->
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_breaking_news, container, false)
        return binding.root
    }


    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


    private fun setUpSearch() {
        binding.etSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query!=null) {

                    newsViewModel.searchForNews(query)

                }

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }


    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }


    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }


}