package com.example.newsapp.presentation.newsDetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.newsapp.R
import com.example.newsapp.domain.models.Article
import com.example.newsapp.ui.theme.MontserratFontFamily
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NewsDetails(
    article: Article,
    onBookmarkClick: (Article) -> Unit,
    context: Context,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        if (!article.urlToImage.isNullOrBlank()) {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = "News image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = article.title,
            style = typography.titleLarge.copy(fontFamily = MontserratFontFamily),
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val authorsName = "${stringResource(R.string.by)} ${article.author ?: stringResource(R.string.unknown)}"
            val maxChars = 30

            Text(
                text = shortenTheText(maxChars, authorsName),
                style = typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = formatDate(article.publishedAt),
                style = typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!article.description.isNullOrBlank()) {
            Text(
                text = article.description,
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!article.content.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = article.content,
                        style = typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(article.url)
                    }
                    context.startActivity(intent)
                }
            ) {
                val nameOfSource = article.source.name
                val maxCharsOfSource = 15

                Text(
                    text = stringResource(R.string.source) + ": ${
                        shortenTheText(
                            maxCharsOfSource,
                            nameOfSource
                        )
                    }",
                    style = typography.labelSmall.copy(fontFamily = MontserratFontFamily),
                    color = MaterialTheme.colorScheme.tertiary
                )

                Icon(
                    modifier = Modifier.padding(top = 2.dp),
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                onClick = { onBookmarkClick(article) },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = if (article.isBookmarked) stringResource(R.string.delete_from_favorite) else stringResource(
                        R.string.save_to_favorite
                    ),
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        inputFormat.parse(dateString)?.let { outputFormat.format(it) } ?: "Unknown date"
    } catch (e: Exception) {
        "Unknown date"
    }
}

private fun shortenTheText(maxChars: Int, text: String) =
    if (text.length > maxChars) text.take(maxChars) + "..." else text
