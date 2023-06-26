package com.vairagi.news.ui.fragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vairagi.news.R
import com.vairagi.news.adapter.NewsAdapter
import com.vairagi.news.databinding.FragmentBreakingNewsBinding
import com.vairagi.news.db.ArticleDatabase
import com.vairagi.news.model.Article
import com.vairagi.news.repository.NewsRepository
import com.vairagi.news.ui.NewsViewModel
import com.vairagi.news.ui.NewsViewModelProviderFactory
import com.vairagi.news.util.ConnectivityObserver
import com.vairagi.news.util.NetworkConnectivityObserver
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
        val repository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(repository, requireContext())

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            article ->


            Log.d("article", article.toString())



            if(article.author.isNullOrEmpty()) {
                article.author = "abc"
            }

            if(article.source.id.isNullOrEmpty()) {
                article.source.id = "1"
            }

            if(article.source.name.isNullOrEmpty()) {
                article.source.name = "abc"
            }




            val bundle = Bundle().apply {
                putSerializable("article",article)
            }

            val action = BreakingNewsDirections.actionBreakingNewsToArticleFragment(article)

            findNavController().navigate(
                R.id.action_breakingNews_to_articleFragment,
                bundle
            )


        }



        newsViewModel = ViewModelProvider(requireActivity(), viewModelProviderFactory)[NewsViewModel::class.java]

        checkForInterNetConnectionAndCallForBreakingNews()


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

                else -> {

                }
            }
        }


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





    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }


    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun checkForInterNetConnectionAndCallForBreakingNews() {
        NetworkConnectivityObserver(requireContext()).observe(viewLifecycleOwner) {
            when(it) {
                ConnectivityObserver.Available -> {
                    newsViewModel.getBreakingNews("in")
                }
                ConnectivityObserver.Unavailable -> {
                    Snackbar.make(requireView(), "No Internet Connection", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }


}