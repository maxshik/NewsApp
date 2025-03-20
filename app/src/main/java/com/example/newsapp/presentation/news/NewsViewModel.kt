package com.example.newsapp.presentation.news

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.core.ErrorConstants.NO_INTERNET_CONNECTION
import com.example.newsapp.domain.models.Article
import com.example.newsapp.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsState(
    val news: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isFavoritesLoading: Boolean = true,
    val error: String? = null,
    val isOffline: Boolean = false,
    val selectedCategory: String = "general",
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    val repository: NewsRepository,
) : ViewModel() {

    private val _state = mutableStateOf(NewsState())
    val state: State<NewsState> = _state

    init {
        loadNews(_state.value.selectedCategory)
        loadFavoriteNews()
    }

    fun loadNews(category: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                selectedCategory = category,
                error = null,
                isOffline = false
            )
            val result = repository.getNews(category)
            if (result.isSuccess) {
                _state.value = _state.value.copy(
                    news = result.getOrNull() ?: emptyList(),
                    isLoading = false,
                    error = null,
                    isOffline = false
                )
            } else {
                val cachedArticles = result.getOrNull() ?: emptyList()
                _state.value = _state.value.copy(
                    news = cachedArticles,
                    isLoading = false,
                    error = result.exceptionOrNull()?.message,
                    isOffline = result.exceptionOrNull()?.message == NO_INTERNET_CONNECTION
                )
            }
        }
    }

    private fun loadFavoriteNews() {
        viewModelScope.launch {
            repository.getFavoriteArticles().collect { favorites ->
                Log.i("NewsViewModel", "Loaded ${favorites.size} favorite articles")
                _state.value = _state.value.copy(isFavoritesLoading = false)
            }
        }
    }

    fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            if (article.isBookmarked) {
                repository.removeFavorite(article)
            } else {
                repository.addFavorite(article)
            }

            val updatedNews = _state.value.news.map {
                if (it.id == article.id) {
                    it.copy(isBookmarked = !it.isBookmarked)
                } else {
                    it
                }
            }
            _state.value = _state.value.copy(news = updatedNews)
        }
    }

    fun getFavoriteArticles(): Flow<List<Article>> {
        return repository.getFavoriteArticles()
    }
}