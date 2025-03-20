package com.example.newsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapp.core.DatabaseConstants.FAVORITE_ARTICLES

@Entity(tableName = FAVORITE_ARTICLES)
data class FavoriteArticleEntity(
    @PrimaryKey val id: String,
    val sourceName: String,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    val isBookmarked: Boolean,
)