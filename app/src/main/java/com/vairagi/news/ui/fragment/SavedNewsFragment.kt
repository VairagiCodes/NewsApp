package com.vairagi.news.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vairagi.news.R
import com.vairagi.news.adapter.NewsAdapter
import com.vairagi.news.databinding.FragmentSavedNewsBinding
import com.vairagi.news.db.ArticleDatabase
import com.vairagi.news.repository.NewsRepository
import com.vairagi.news.ui.NewsViewModel
import com.vairagi.news.ui.NewsViewModelProviderFactory


class SavedNewsFragment : Fragment() {

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentSavedNewsBinding
    private lateinit var newsViewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(repository)

        newsViewModel = ViewModelProvider(requireActivity(), viewModelProviderFactory)[NewsViewModel::class.java]

        setUpRecyclerView()
        navigateToArticle()
        getSaveNews()
        deleteSaveItem()



    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_saved_news, container, false)
        return binding.root
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()

        binding.rvSavedNews.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
        }
    }

    private fun navigateToArticle() {
        newsAdapter.setOnItemClickListener {
            article ->
            val action = SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(article)
            findNavController().navigate(action)
        }
    }

    private fun getSaveNews() {
        newsViewModel.getSaveNews().observe(viewLifecycleOwner) {
            articles ->
            newsAdapter.differ.submitList(articles)
        }
    }

    private fun deleteSaveItem() {
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(requireView(), "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        newsViewModel.saveArticle(article)

                    }

                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }
    }



}