package com.example.newsapp.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticlesSource (
    val id: String?,
    val name: String
) : Parcelable