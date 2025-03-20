package com.example.newsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapp.core.DatabaseConstants.CACHED_ARTICLES

@Entity(tableName = CACHED_ARTICLES)
data class CachedArticleEntity(
    @PrimaryKey val id: String,
    val sourceName: String,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    val cachedAt: Long
)