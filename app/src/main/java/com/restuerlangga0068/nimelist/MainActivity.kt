package com.restuerlangga0068.nimelist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.restuerlangga0068.nimelist.ui.theme.NimeListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NimeListTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                // State list agar data yang ditambah bisa langsung muncul di Home
                val fullAnimeList = remember { mutableStateListOf(*initialAnimeList.toTypedArray()) }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            currentList = fullAnimeList,
                            onAddClick = { navController.navigate("add") },
                            onTrailerClick = { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        )
                    }
                    composable("add") {
                        AddScreen(
                            onBackClick = { navController.popBackStack() },
                            onSaveClick = { newAnime ->
                                fullAnimeList.add(newAnime)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

data class Anime(
    val id: Int,
    val title: String,
    val description: String,
    val imageRes: Int,
    val trailerUrl: String
)

val initialAnimeList = listOf(
    Anime(1, "Attack on Titan", "Kemanusiaan melawan Titan.", R.drawable.poster_bleach, "https://www.youtube.com/watch?v=cvmD_uG_7mQ"),
    Anime(2, "Naruto", "Perjalanan menjadi Hokage.", R.drawable.poster_naruto, "https://www.youtube.com/watch?v=-G9BqkgZXRA"),
    Anime(3, "One Piece", "Mencari harta karun legendaris.", R.drawable.poster_op, "https://www.youtube.com/watch?v=l_98K4_6uLU")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentList: SnapshotStateList<Anime>,
    onAddClick: () -> Unit,
    onTrailerClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = stringResource(id = R.string.header_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn {
                items(currentList) { anime ->
                    AnimeCard(anime, onTrailerClick)
                }
            }
        }
    }
}

@Composable
fun AnimeCard(anime: Anime, onTrailerClick: (String) -> Unit) {
    var isWatched by remember { mutableStateOf(false) }
    var rating by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = anime.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(text = anime.title, style = MaterialTheme.typography.titleLarge)
                Text(text = anime.description, maxLines = 2, style = MaterialTheme.typography.bodyMedium)

                // Fitur Rating Bintang
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = index + 1 },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (index < rating) Color(0xFFFFD700) else Color.Gray
                            )
                        }
                    }
                }

                // Fitur Checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isWatched, onCheckedChange = { isWatched = it })
                    Text(stringResource(R.string.status_completed), style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = { onTrailerClick(anime.trailerUrl) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = stringResource(id = R.string.btn_trailer))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(onBackClick: () -> Unit, onSaveClick: (Anime) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.btn_add)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.label_title)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text(stringResource(R.string.label_desc)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        // Pakai gambar default karena upload gambar belum diimplementasi
                        onSaveClick(Anime(System.currentTimeMillis().toInt(), title, desc, R.drawable.poster_bleach, "https://youtube.com"))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_save))
            }
        }
    }
}