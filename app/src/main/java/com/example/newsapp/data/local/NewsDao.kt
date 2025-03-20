package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(article: FavoriteArticleEntity)

    @Delete
    suspend fun deleteFavorite(article: FavoriteArticleEntity)

    @Query("SELECT * FROM favorite_articles")
    fun getAllFavorites(): Flow<List<FavoriteArticleEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_articles WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedArticles(articles: List<CachedArticleEntity>)

    @Query("SELECT * FROM cached_articles")
    fun getCachedArticles(): Flow<List<CachedArticleEntity>>

    @Query("DELETE FROM cached_articles")
    suspend fun clearCachedArticles()
}