package com.vairagi.news.ui
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vairagi.news.repository.NewsRepository

class NewsViewModelProviderFactory(private val newsRepository: NewsRepository,private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository, context) as T
    }
}