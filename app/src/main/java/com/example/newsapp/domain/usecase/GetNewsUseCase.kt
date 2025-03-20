package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.repository.NewsRepositoryImpl
import com.example.newsapp.domain.models.Article
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val repository: NewsRepositoryImpl
) {
    suspend operator fun invoke(category: String): Result<List<Article>> {
        return repository.getNews(category)
    }
}