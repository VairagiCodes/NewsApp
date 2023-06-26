package com.vairagi.news.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.vairagi.news.R
import com.vairagi.news.databinding.FragmentArticleBinding
import com.vairagi.news.db.ArticleDatabase
import com.vairagi.news.repository.NewsRepository
import com.vairagi.news.ui.NewsViewModel
import com.vairagi.news.ui.NewsViewModelProviderFactory


class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentArticleBinding
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var newsViewModel: NewsViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(repository, requireContext())
        setUpWebView()

        newsViewModel = ViewModelProvider(requireActivity(), viewModelProviderFactory)[NewsViewModel::class.java]



    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)

        return binding.root
    }

    private fun setUpWebView() {
        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            article?.let {
                loadUrl(
                    it.url
                )
            }
        }

        binding.fab.setOnClickListener {
            if (article != null) {
                newsViewModel.saveArticle(article)
                Snackbar.make(requireView(), "Article saved successfully",Snackbar.LENGTH_SHORT).show()
            }
        }
    }


  }
