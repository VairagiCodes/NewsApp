package com.vairagi.news.repository

import com.vairagi.news.api.RetrofitInstance
import com.vairagi.news.db.ArticleDatabase

class NewsRepository(

) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    suspend fun searchForNews(queryString: String) = RetrofitInstance.api.searchForNews(queryString)

}