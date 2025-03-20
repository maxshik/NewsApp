package com.example.newsapp.data.remote

import com.example.newsapp.core.DataConstants
import com.example.newsapp.domain.models.Article
import com.example.newsapp.domain.models.ArticlesSource
import java.util.UUID

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleResponse>
)

data class ArticleResponse(
    val source: SourceResponse?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?,
    val isBookmarked: Boolean
) {
    fun toArticle(): Article = Article(
        id = url ?: UUID.randomUUID().toString(),
        source = source?.toArticlesSource() ?: ArticlesSource(
            name = DataConstants.UNKNOWN_VALUE,
            id = DataConstants.DEFAULT_ID
        ),
        author = author ?: DataConstants.UNKNOWN_VALUE,
        title = title ?: DataConstants.NO_TITLE,
        description = description,
        url = url ?: DataConstants.EMPTY_STRING,
        urlToImage = urlToImage,
        publishedAt = publishedAt ?: DataConstants.EMPTY_STRING,
        content = content,
        isBookmarked = false
    )
}

data class SourceResponse(
    val id: String?,
    val name: String?
) {
    fun toArticlesSource(): ArticlesSource = ArticlesSource(
        id = id ?: DataConstants.DEFAULT_ID,
        name = name ?: DataConstants.UNKNOWN_VALUE
    )
}