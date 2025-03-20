package com.example.newsapp.domain.repository

import com.example.newsapp.domain.models.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getNews(category: String): Result<List<Article>>
    suspend fun addFavorite(article: Article)
    suspend fun removeFavorite(article: Article)
    fun getFavoriteArticles(): Flow<List<Article>>
    suspend fun isFavorite(id: String): Boolean
}