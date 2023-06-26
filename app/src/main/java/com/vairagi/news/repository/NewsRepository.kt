package com.vairagi.news.repository

import com.vairagi.news.api.RetrofitInstance
import com.vairagi.news.db.ArticleDatabase
import com.vairagi.news.model.Article

class NewsRepository(
val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    suspend fun searchForNews(queryString: String, pageNumber: Int) = RetrofitInstance.api.searchForNews(queryString, pageNumber = pageNumber)

    suspend fun insert(article: Article) = db.getArticleDao().insert(article)

    fun getSaveNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}