package com.example.newsapp.presentation.bookmarks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.newsapp.R
import com.example.newsapp.domain.models.Article
import com.example.newsapp.presentation.components.ErrorMessageComponent
import com.example.newsapp.presentation.news.NewsItem
import com.example.newsapp.presentation.news.NewsViewModel

@Composable
fun BookmarksScreen(
    viewModel: NewsViewModel = hiltViewModel(),
    onNewsClick: (Article) -> Unit
) {
    val state = viewModel.state.value
    val favoriteArticles by viewModel.getFavoriteArticles().collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isFavoritesLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            favoriteArticles.isEmpty() -> {
                ErrorMessageComponent(R.drawable.box, R.string.no_favourite_news)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favoriteArticles) { article ->
                        NewsItem(
                            article = article,
                            onClick = { onNewsClick(article) }
                        )
                    }
                }
            }
        }
    }
}