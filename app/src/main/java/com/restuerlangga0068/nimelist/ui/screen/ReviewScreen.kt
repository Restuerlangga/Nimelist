package com.restuerlangga0068.nimelist.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.restuerlangga0068.nimelist.database.AnimeEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    animeId: String,
    viewModel: MainViewModel,
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
    var showDeleteDialog by remember { mutableStateOf(false) }
    val userEmail by viewModel.userEmailState.collectAsState()

    val isNew = animeId == "new"

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
                        anime?.let {
                            viewModel.deleteAnime(it) {
                                onBackClick()
                            }
                        }
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

            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL Gambar") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Anime") }, modifier = Modifier.fillMaxWidth())

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

            OutlinedTextField(value = descriptionState, onValueChange = { descriptionState = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = review, onValueChange = { review = it }, label = { Text("Review") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isNew) {
                        viewModel.addAnime(title, descriptionState, imageUrl, rating.toInt(), isWatched, userEmail) { onBackClick() }
                    } else {
                        anime?.let {
                            viewModel.updateAnime(
                                it.copy(
                                    title = title,
                                    rating = rating.toDouble(),
                                    description = descriptionState,
                                    review = review,
                                    isCompleted = isWatched,
                                    imageUrl = imageUrl
                                )
                            ) { onBackClick() }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simpan") }
        }
    }
}