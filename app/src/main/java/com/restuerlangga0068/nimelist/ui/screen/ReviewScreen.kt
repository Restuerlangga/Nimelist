package com.restuerlangga0068.nimelist.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    val listPoster = listOf(
        R.drawable.poster1,
        R.drawable.poster2,
        R.drawable.poster3,
        R.drawable.poster4,
        R.drawable.poster5,
        R.drawable.poster6,
        R.drawable.poster7,
        R.drawable.poster8,
        R.drawable.poster9,
        R.drawable.poster10,
        android.R.drawable.ic_menu_gallery
    )
    var anime by remember { mutableStateOf<AnimeEntity?>(null) }


    var title by remember { mutableStateOf("") }
    var imageIndex by remember { mutableIntStateOf(0) }
    var selectedImageRes by remember { mutableIntStateOf(android.R.drawable.ic_menu_gallery) }

    var rating by remember { mutableStateOf(0f) }
    var review by remember { mutableStateOf("") }
    var isWatched by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    LaunchedEffect(animeId) {
        if (animeId != -1) {
            val data = viewModel.getAnimeById(animeId)
            anime = data
            data?.let {
                title = it.title
                rating = it.rating
                review = it.review
                isWatched = it.isCompleted
                selectedImageRes = it.imageRes
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (animeId == -1) "Tambah Anime Baru" else title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    if (animeId != -1) {
                        IconButton(onClick = {
                            viewModel.delete(animeId)
                            onBackClick()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
        ) {

            Text("Gambar Anime:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = selectedImageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                if (animeId == -1) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {

                        imageIndex = (imageIndex + 1) % listPoster.size
                        selectedImageRes = listPoster[imageIndex]
                    }) {
                        Text("Ganti Gambar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Anime") },
                modifier = Modifier.fillMaxWidth(),
                enabled = animeId == -1,
                isError = errorText.contains("Judul")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- CHECKBOX & RATING (Sama seperti sebelumnya) ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isWatched, onCheckedChange = { isWatched = it })
                Text("Mark as Completed")
            }

            Spacer(modifier = Modifier.height(8.dp))
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
                value = review,
                onValueChange = { review = it },
                label = { Text(stringResource(R.string.label_desc)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (errorText.isNotEmpty()) {
                Text(errorText, color = Color.Red, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorText = "Judul tidak boleh kosong!"
                    } else {
                        if (animeId == -1) {
                            val newAnime = AnimeEntity(
                                title = title,
                                description = "Anime ditambahkan secara manual",
                                rating = rating,
                                review = review,
                                isCompleted = isWatched,
                                imageRes = selectedImageRes,

                            )

                            viewModel.insert(newAnime)

                        } else {

                            anime?.let {
                                viewModel.update(it.copy(
                                    rating = rating,
                                    review = review,
                                    isCompleted = isWatched
                                ))
                            }
                        }
                        onBackClick()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan")
            }
        }
    }
}