package com.restuerlangga0068.nimelist.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.restuerlangga0068.nimelist.R
import com.restuerlangga0068.nimelist.data.Anime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(currentList: List<Anime>, onItemClick: (Int) -> Unit, onTrailerClick: (String) -> Unit) {

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
                                setShowAboutDialog(true)
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (showAboutDialog) {
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) ) {
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