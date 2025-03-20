package com.example.newsapp.core

object Routes {
    const val MAIN_SCREEN = "main_screen"
    const val NEWS_DETAIL = "news_detail/{articleId}"
    const val BOOKMARKS = "bookmarks"

    fun newsDetailRoute(articleId: String): String = "news_detail/$articleId"
}