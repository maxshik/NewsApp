package com.example.newsapp.domain.models

import android.os.Parcelable
import com.example.newsapp.data.local.FavoriteArticleEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: String,
    val source: ArticlesSource,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    val isBookmarked: Boolean
) : Parcelable {

    fun toFavoriteEntity(): FavoriteArticleEntity {
        return FavoriteArticleEntity(
            id = id,
            sourceName = source.name,
            author = author,
            title = title,
            description = description,
            url = url,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
            content = content,
            isBookmarked = isBookmarked
        )
    }
}