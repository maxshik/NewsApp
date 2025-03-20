package com.example.newsapp.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.core.DatabaseConstants.DATABASE_NAME
import com.example.newsapp.core.RetrofitConstants.BASE_URL
import com.example.newsapp.data.local.AppDatabase
import com.example.newsapp.data.local.NewsDao
import com.example.newsapp.data.remote.NewsApi
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNewsApi(): NewsApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFavoriteArticleDao(database: AppDatabase): NewsDao {
        return database.favoriteArticleDao()
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsApi: NewsApi,
        dao: NewsDao
    ): NewsRepository {
        return NewsRepositoryImpl(newsApi, dao)
    }
}