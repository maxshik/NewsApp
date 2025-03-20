package com.example.newsapp.presentation.navigation

import android.content.Context
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.newsapp.R
import com.example.newsapp.core.Routes
import com.example.newsapp.presentation.bookmarks.BookmarksScreen
import com.example.newsapp.presentation.news.NewsScreen
import com.example.newsapp.presentation.newsDetails.NewsDetails
import com.example.newsapp.presentation.news.NewsViewModel
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    context: Context
) {
    val sharedViewModel: NewsViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.MAIN_SCREEN,
        modifier = modifier
    ) {
        composable(Routes.MAIN_SCREEN) {
            NewsScreen(viewModel = sharedViewModel, context = context) { article ->
                val encodedUrl = URLEncoder.encode(article.url, "UTF-8")
                navController.navigate(Routes.newsDetailRoute(encodedUrl))
            }
        }
        composable(Routes.BOOKMARKS) {
            BookmarksScreen(viewModel = sharedViewModel) { article ->
                val encodedUrl = URLEncoder.encode(article.url, "UTF-8")
                navController.navigate(Routes.newsDetailRoute(encodedUrl))
            }
        }
        composable(
            route = Routes.NEWS_DETAIL,
            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedArticleId = backStackEntry.arguments?.getString("articleId")
            val articleId = encodedArticleId?.let { URLDecoder.decode(it, "UTF-8") }

            val favoriteArticles by sharedViewModel.getFavoriteArticles().collectAsState(initial = emptyList())
            val initialArticle = sharedViewModel.state.value.news.find { it.id == articleId }
                ?: favoriteArticles.find { it.id == articleId }

            var article by remember { mutableStateOf(initialArticle) }

            LaunchedEffect(initialArticle) {
                if (article == null && initialArticle != null) {
                    article = initialArticle
                }
            }

            if (article != null) {
                LaunchedEffect(articleId) {
                    val isBookmarked = sharedViewModel.repository.isFavorite(articleId!!)
                    if (article!!.isBookmarked != isBookmarked) {
                        article = article!!.copy(isBookmarked = isBookmarked)
                    }
                }
                NewsDetails(
                    article = article!!,
                    onBookmarkClick = { updatedArticle ->
                        sharedViewModel.toggleBookmark(updatedArticle)
                        article = updatedArticle.copy(isBookmarked = !updatedArticle.isBookmarked)
                    },
                    context = LocalContext.current
                )
            } else {
                Text(stringResource(R.string.article_not_founded) + ": $articleId")
            }
        }
    }
}