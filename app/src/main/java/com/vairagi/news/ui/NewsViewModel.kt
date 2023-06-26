package com.vairagi.news.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.vairagi.news.model.Article
import com.vairagi.news.model.NewsResponse
import com.vairagi.news.repository.NewsRepository
import com.vairagi.news.util.ConnectivityObserver
import com.vairagi.news.util.NetworkConnectivityObserver
import com.vairagi.news.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository: NewsRepository,
    val context: Context
): ViewModel() {


    val breakingNews: LiveData<Resource<NewsResponse>>
        get() = _breakingNews
    val searchNews: LiveData<Resource<NewsResponse>>
        get() = _searchNews

    private val _breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    private val _searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    private var _breakingNewsResponse: NewsResponse ? = null
    val breakingNewsResponse = _breakingNewsResponse

    private var _searchNewsResponse: NewsResponse ? = null
    val searchNewsResponse = _searchNewsResponse



    var breakingNewsPage= 1
    var searchNewsPage= 1



    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        _breakingNews.postValue(Resource.Loading())


        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)

       _breakingNews.postValue(handleBreakingNewsResponse(response))




    }


    fun searchForNews(queryString: String) = viewModelScope.launch {
        _searchNews.postValue(Resource.Loading())

        val response = newsRepository.searchForNews(queryString,searchNewsPage)

        _searchNews.postValue(handleSearchNewsResponse(response))
    }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let {
                resultResponse ->
                breakingNewsPage++
                if(_breakingNewsResponse == null) {
                    _breakingNewsResponse = resultResponse
                }

                else {
                    val oldArticles = _breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)

                }

                return Resource.Success(_breakingNewsResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let {
                    resultResponse ->
                searchNewsPage++
                if(_searchNewsResponse == null) {
                    _searchNewsResponse = resultResponse
                }

                else {
                    val oldArticles = _searchNewsResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)

                }

                return Resource.Success(_searchNewsResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }


    fun saveArticle(article: Article) {
        viewModelScope.launch {
            newsRepository.insert(article)
        }

    }


    fun getSaveNews() = newsRepository.getSaveNews()

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            newsRepository.deleteArticle(article)
        }
    }






}