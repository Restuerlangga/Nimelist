package com.restuerlangga0068.nimelist


import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
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

                // State List agar data tersimpan selama aplikasi hidup
                val fullAnimeList = remember { mutableStateListOf(*initialAnimeData.toTypedArray()) }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            currentList = fullAnimeList,
                            onItemClick = { animeId -> navController.navigate("review/$animeId") },
                            onTrailerClick = { url ->
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            }
                        )
                    }
                    composable("review/{animeId}") { backStackEntry ->
                        val animeId = backStackEntry.arguments?.getString("animeId")?.toInt()
                        val selectedAnime = fullAnimeList.find { it.id == animeId }

                        ReviewScreen(
                            anime = selectedAnime,
                            onBackClick = { navController.popBackStack() },
                            onSaveReview = { id, rating, review, isWatched ->
                                val index = fullAnimeList.indexOfFirst { it.id == id }
                                if (index != -1) {
                                    fullAnimeList[index] = fullAnimeList[index].copy(
                                        rating = rating,
                                        review = review,
                                        isCompleted = isWatched
                                    )
                                }
                                navController.popBackStack()
                            },
                            onShareClick = { text ->
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Review via"))
                            }
                        )
                    }
                }
            }
        }
    }
}

// Model Data
data class Anime(
    val id: Int,
    val title: String,
    val description: String,
    val imageRes: Int,
    val trailerUrl: String,
    val rating: Int = 0,
    val review: String = "",
    val isCompleted: Boolean = false
)

// Data Awal
val initialAnimeData = listOf(
    Anime(1, "Bleach", "Kisah Shinigami pembasmi Hollow.", R.drawable.poster_bleach, "https://youtu.be/W99Ef2LkyOg"),
    Anime(2, "Naruto", "Perjalanan Uzumaki Naruto menjadi Hokage.", R.drawable.poster_naruto, "https://youtu.be/QczGoCmX-pI"),
    Anime(3, "One Piece", "Petualangan Luffy mencari harta karun.", R.drawable.poster_op, "https://youtu.be/lgAwlnGLTUg")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(currentList: List<Anime>, onItemClick: (Int) -> Unit, onTrailerClick: (String) -> Unit) {
    // Gunakan delegasi 'mutableStateOf' tapi dengan akses manual jika 'by' bermasalah
    val (showMenu, setShowMenu) = remember { mutableStateOf(false) }
    val (showAboutDialog, setShowAboutDialog) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { setShowMenu(true) }) { // Pakai fungsi setter
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { setShowMenu(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_about)) },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                            onClick = {
                                setShowMenu(false)
                                setShowAboutDialog(true) // Pakai fungsi setter
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (showAboutDialog) { // Langsung panggil variabelnya
            AlertDialog(
                onDismissRequest = { setShowAboutDialog(false) },
                confirmButton = {
                    TextButton(onClick = { setShowAboutDialog(false) }) {
                        Text(stringResource(R.string.btn_ok))
                    }
                },
                title = { Text(stringResource(R.string.about_title)) },
                text = {
                    Column {
                        Text(stringResource(R.string.about_version))
                        Text(stringResource(R.string.about_developer))
                        Text("Restu Erlangga (2026)", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.about_desc))
                    }
                },
                icon = { Icon(Icons.Default.Info, contentDescription = null) }
            )
        }

        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                Text(
                    text = stringResource(R.string.header_title),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(currentList) { anime ->
                AnimeCard(anime, onClick = { onItemClick(anime.id) }, onTrailerClick = onTrailerClick)
            }
        }
    }
}

@Composable
fun AnimeCard(anime: Anime, onClick: () -> Unit, onTrailerClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = anime.imageRes),
                contentDescription = null,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(text = anime.title, style = MaterialTheme.typography.titleLarge)
                Text(text = anime.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)

                if (anime.isCompleted) {
                    SuggestionChip(onClick = {}, label = { Text(stringResource(R.string.status_completed)) })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (anime.rating > 0) {
                        Text("⭐ ${anime.rating}/5", color = Color(0xFFFFD700))
                    } else {
                        Text(stringResource(R.string.placeholder_add), style = MaterialTheme.typography.bodySmall)
                    }
                }

                Button(
                    onClick = { onTrailerClick(anime.trailerUrl) },
                    modifier = Modifier.padding(top = 4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(text = stringResource(R.string.btn_trailer), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    anime: Anime?,
    onBackClick: () -> Unit,
    onSaveReview: (Int, Int, String, Boolean) -> Unit,
    onShareClick: (String) -> Unit
) {
    var rating by remember { mutableIntStateOf(anime?.rating ?: 0) }
    var reviewText by remember { mutableStateOf(anime?.review ?: "") }
    var isWatched by remember { mutableStateOf(anime?.isCompleted ?: false) }
    var errorText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(anime?.title ?: "Review") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    if (rating > 0 && reviewText.isNotBlank() && isWatched) {
                        IconButton(onClick = {
                            val msg = "Review ${anime?.title}:\nRating: ⭐ $rating/5\nReview: $reviewText"
                            onShareClick(msg)
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            val checkboxColor = if (errorText.contains("Completed")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isWatched,
                    onCheckedChange = { isWatched = it; if (it) errorText = "" },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = if (errorText.contains("Completed")) Color.Red else MaterialTheme.colorScheme.outline
                    )
                )
                Text("Mark as Completed", color = checkboxColor, style = MaterialTheme.typography.bodyMedium)
            }

            if (errorText.contains("Completed")) {
                Text(text = errorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Rating:")
            Row {
                repeat(5) { index ->
                    IconButton(onClick = { rating = index + 1 }) {
                        Icon(
                            imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < rating) Color(0xFFFFD700) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it; if (it.isNotBlank()) errorText = "" },
                label = { Text(stringResource(R.string.label_desc)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = errorText.isNotEmpty(),
                supportingText = { if (errorText.isNotEmpty()) Text(errorText, color = MaterialTheme.colorScheme.error) }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (!isWatched) errorText = "Tandai 'Completed' dulu sebelum menyimpan!"
                    else if (reviewText.isBlank()) errorText = "Review tidak boleh kosong!"
                    else anime?.let { onSaveReview(it.id, rating, reviewText, isWatched) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_save))
            }
        }
    }
}