package com.restuerlangga0068.nimelist.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.restuerlangga0068.nimelist.R
import com.restuerlangga0068.nimelist.database.AnimeEntity
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    animeId: String,
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    onShareClick: (String) -> Unit
) {
    var anime by remember { mutableStateOf<AnimeEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var descriptionState by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(0f) }
    var review by remember { mutableStateOf("") }
    var isWatched by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val isNew = animeId == "new" // Gunakan string "new" untuk penanda data baru

    LaunchedEffect(animeId) {
        if (!isNew) {
            val data = viewModel.getAnimeById(animeId)
            anime = data
            data?.let {
                title = it.title
                descriptionState = it.description ?: ""
                rating = it.rating.toFloat()
                review = it.review ?: ""
                isWatched = it.isCompleted
                imageUrl = it.imageUrl ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Tambah Anime Baru" else title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    if (!isNew) {
                        IconButton(onClick = { onShareClick("Review Anime $title: $review (Rating: $rating/5)") }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus Anime?") },
                text = { Text("Apakah kamu yakin ingin menghapus '$title'?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.delete(animeId)
                        onBackClick()
                    }) { Text("Hapus", color = Color.Red) }
                },
                dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") } }
            )
        }

        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
        ) {
            Text("Gambar Anime (URL):", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = imageUrl.ifBlank { "https://via.placeholder.com/150" },
                contentDescription = null,
                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Anime") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isWatched, onCheckedChange = { isWatched = it })
                Text("Mark as Completed")
            }

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

            OutlinedTextField(
                value = descriptionState,
                onValueChange = { descriptionState = it },
                label = { Text(stringResource(R.string.label_desc)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            OutlinedTextField(
                value = review,
                onValueChange = { review = it },
                label = { Text("Review") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorText = "Judul tidak boleh kosong!"
                    } else {
                        if (isNew) {
                            val newAnime = AnimeEntity(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                description = descriptionState,
                                rating = rating.toDouble(),
                                review = review,
                                isCompleted = isWatched,
                                imageUrl = imageUrl
                            )
                            viewModel.insert(newAnime)
                        } else {
                            anime?.let {
                                viewModel.update(it.copy(
                                    title = title,
                                    rating = rating.toDouble(),
                                    description = descriptionState,
                                    review = review,
                                    isCompleted = isWatched
                                ))
                            }
                        }
                        onBackClick()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simpan") }
        }
    }
}