package com.restuerlangga0068.nimelist.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.restuerlangga0068.nimelist.R
import com.restuerlangga0068.nimelist.database.AnimeEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    animeId: Int,
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    onShareClick: (String) -> Unit
) {
    var anime by remember { mutableStateOf<AnimeEntity?>(null) }
    var rating by remember { mutableStateOf(0f) }
    var review by remember { mutableStateOf("") } // Gunakan 'review', bukan 'reviewText'
    var isWatched by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    // Ambil data dari Room saat ID diterima
    LaunchedEffect(animeId) {
        if (animeId != -1) {
            val data = viewModel.getAnimeById(animeId)
            anime = data
            data?.let {
                rating = it.rating
                review = it.review
                isWatched = it.isCompleted
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(anime?.title ?: "Review") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    // Fitur Share (Hanya muncul jika sudah diisi)
                    if (rating > 0 && review.isNotBlank() && isWatched) {
                        IconButton(onClick = {
                            val msg = "Review ${anime?.title}:\nRating: ⭐ $rating/5\nReview: $review"
                            onShareClick(msg)
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }

                    // Fitur Hapus (Poin 2e di rubrik)
                    IconButton(onClick = {
                        viewModel.delete(animeId)
                        onBackClick()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            // Checkbox Completed
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isWatched,
                    onCheckedChange = { isWatched = it; if (it) errorText = "" },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = if (errorText.contains("Tandai")) Color.Red else MaterialTheme.colorScheme.outline
                    )
                )
                Text("Mark as Completed", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Rating:")
            Row {
                repeat(5) { index ->
                    val starIndex = (index + 1).toFloat()
                    IconButton(onClick = { rating = starIndex }) {
                        Icon(
                            imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (starIndex <= rating) Color(0xFFFFD700) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = review, // Pastikan pakai 'review'
                onValueChange = { review = it; if (it.isNotBlank()) errorText = "" },
                label = { Text(stringResource(R.string.label_desc)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = errorText.contains("Review")
            )

            if (errorText.isNotEmpty()) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (!isWatched) {
                        errorText = "Tandai 'Completed' dulu sebelum menyimpan!"
                    } else if (review.isBlank()) {
                        errorText = "Review tidak boleh kosong!"
                    } else {
                        // UPDATE DATA KE ROOM
                        anime?.let {
                            val updatedData = it.copy(
                                rating = rating,
                                review = review,
                                isCompleted = isWatched
                            )
                            viewModel.update(updatedData)
                            onBackClick() // Kembali ke Home
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_save))
            }
        }
    }
}