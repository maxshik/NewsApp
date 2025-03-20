package com.example.newsapp.domain.repository

import android.util.Log
import com.example.newsapp.core.ErrorConstants.NO_INTERNET_CONNECTION
import com.example.newsapp.core.RetrofitConstants.NEWS_API_KEY
import com.example.newsapp.data.local.CachedArticleEntity
import com.example.newsapp.data.local.NewsDao
import com.example.newsapp.data.remote.NewsApi
import com.example.newsapp.domain.models.Article
import com.example.newsapp.domain.models.ArticlesSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val dao: NewsDao
) : NewsRepository {

    companion object {
        private const val CACHE_EXPIRY_TIME = 30 * 60 * 1000
    }

    private suspend fun mapToArticles(cached: List<CachedArticleEntity>): List<Article> {
        return cached.map { entity ->
            Article(
                id = entity.id,
                source = ArticlesSource(null, entity.sourceName),
                author = entity.author,
                title = entity.title,
                description = entity.description,
                url = entity.url,
                urlToImage = entity.urlToImage,
                publishedAt = entity.publishedAt,
                content = entity.content,
                isBookmarked = dao.isFavorite(entity.id)
            )
        }
    }

    private fun isCacheValid(cachedArticles: List<CachedArticleEntity>): Boolean {
        return cachedArticles.isNotEmpty() && cachedArticles.all {
            System.currentTimeMillis() - it.cachedAt < CACHE_EXPIRY_TIME
        }
    }

    private suspend fun fetchArticlesFromApi(category: String): List<Article> {
        val response = newsApi.getTopHeadlines(category = category, apiKey = NEWS_API_KEY)
        if (response.status != "ok") {
            throw Exception("API returned error: ${response.status}")
        }
        val articles = response.articles.map { it.toArticle() }
        dao.insertCachedArticles(articles.map { article ->
            CachedArticleEntity(
                id = article.id,
                sourceName = article.source.name,
                author = article.author,
                title = article.title,
                description = article.description,
                url = article.url,
                urlToImage = article.urlToImage,
                publishedAt = article.publishedAt,
                content = article.content,
                cachedAt = System.currentTimeMillis()
            )
        })
        return articles.map { article ->
            article.copy(isBookmarked = dao.isFavorite(article.id))
        }
    }

    private suspend fun getCachedArticles(): List<CachedArticleEntity> {
        return dao.getCachedArticles().first()
    }

    override suspend fun getNews(category: String): Result<List<Article>> {
        val cachedArticles = getCachedArticles()
        val isCacheValid = isCacheValid(cachedArticles)
        Log.i("NewsRepo", "Cache valid: $isCacheValid, cached articles: ${cachedArticles.size}")

        if (isCacheValid) {
            return Result.success(mapToArticles(cachedArticles))
        }

        return try {
            val freshArticles = fetchArticlesFromApi(category)
            Log.i("NewsRepo", "Fetched ${freshArticles.size} articles from API")
            Result.success(freshArticles)
        } catch (e: IOException) {
            Log.e("NewsRepo", "No internet connection: ${e.message}")
            if (cachedArticles.isNotEmpty()) {
                Result.success(mapToArticles(cachedArticles))
            } else {
                Result.failure(Exception(NO_INTERNET_CONNECTION))
            }
        } catch (e: Exception) {
            Log.e("NewsRepo", "Error: ${e.message}")
            if (cachedArticles.isNotEmpty()) {
                Result.success(mapToArticles(cachedArticles))
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun addFavorite(article: Article) {
        dao.insertFavorite(article.toFavoriteEntity())
    }

    override suspend fun removeFavorite(article: Article) {
        dao.deleteFavorite(article.toFavoriteEntity())
    }

    override fun getFavoriteArticles(): Flow<List<Article>> {
        return dao.getAllFavorites().map { entities ->
            entities.map { entity ->
                Article(
                    id = entity.id,
                    source = ArticlesSource(null, entity.sourceName),
                    author = entity.author,
                    title = entity.title,
                    description = entity.description,
                    url = entity.url,
                    urlToImage = entity.urlToImage,
                    publishedAt = entity.publishedAt,
                    content = entity.content,
                    isBookmarked = true
                )
            }
        }
    }

    override suspend fun isFavorite(id: String): Boolean {
        return dao.isFavorite(id)
    }
}