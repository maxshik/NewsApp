package com.example.newsapp.data.remote

import com.example.newsapp.core.NewsApiConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET(NewsApiConstants.ENDPOINT_TOP_HEADLINES)
    suspend fun getTopHeadlines(
        @Query(NewsApiConstants.QUERY_CATEGORY) category: String,
        @Query(NewsApiConstants.QUERY_API_KEY) apiKey: String,
        @Query(NewsApiConstants.QUERY_COUNTRY) country: String = NewsApiConstants.DEFAULT_COUNTRY
    ): NewsResponse
}