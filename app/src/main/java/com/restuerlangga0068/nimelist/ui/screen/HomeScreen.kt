package com.restuerlangga0068.nimelist.ui.screen




import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.restuerlangga0068.nimelist.BuildConfig
import com.restuerlangga0068.nimelist.R
import com.restuerlangga0068.nimelist.database.AnimeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onItemClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val (showMenu, setShowMenu) = remember { mutableStateOf(false) }
    val (showAboutDialog, setShowAboutDialog) = remember { mutableStateOf(false) }
    val isByRating by viewModel.isSortedByRating.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val animeList by viewModel.animeList.collectAsStateWithLifecycle()
    val apiStatus by viewModel.apiStatus.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                   IconButton(onClick = {
                       CoroutineScope(Dispatchers.IO).launch { signIn(context) }

                   }) {
                       Icon(
                           painter = painterResource(R.drawable.account_circle),
                           contentDescription = stringResource(R.string.profil),
                           tint = MaterialTheme.colorScheme.primary
                       )
                   }
                    IconButton(onClick = { viewModel.toggleSort(isByRating) }) {
                        Icon(
                            imageVector = if (isByRating) Icons.Default.Star else Icons.Default.SortByAlpha,
                            contentDescription = "Sort"
                        )
                    }
                    IconButton(onClick = { setShowMenu(true) }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { setShowMenu(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isDark) "Mode Terang" else "Mode Gelap") },
                            leadingIcon = {
                                Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, null)
                            },
                            onClick = {
                                setShowMenu(false)
                                viewModel.toggleTheme(isDark)
                            }
                        )
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddClick() }, // Disesuaikan agar memicu aksi tambah data
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Anime")
            }
        }
    ) { innerPadding ->

        when (apiStatus) {
            ApiStatus.LOADING -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            ApiStatus.SUCCESS -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    item {
                        Text(
                            text = stringResource(R.string.header_title),
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    if (animeList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxHeight(0.8f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Belum ada daftar anime.")
                            }
                        }
                    } else {
                        items(animeList, key = { it.id }) { anime ->
                            AnimeCard(
                                anime = anime,
                                onClick = { onItemClick(anime.id) }
                            )
                        }
                    }
                }
            }
            ApiStatus.ERROR -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gagal memuat data dari server.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refreshDataFromServer() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }

        if (showAboutDialog) {
            AboutDialog(onDismiss = { setShowAboutDialog(false) })
        }
    }
}

@Composable
fun AnimeCard(anime: AnimeEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(text = anime.title, style = MaterialTheme.typography.titleLarge)
                Text(text = anime.description ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 2)

                if (anime.isCompleted) {
                    SuggestionChip(onClick = {}, label = { Text(stringResource(R.string.status_completed)) })
                }

                if (anime.rating > 0) {
                    Text("⭐ ${anime.rating}/5", color = Color(0xFFFFD700))
                }
            }
        }
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_ok))
            }
        },
        title = { Text(stringResource(R.string.about_title)) },
        text = {
            Column {
                Text(stringResource(R.string.about_version))
                Text(stringResource(R.string.about_developer))
                Text("Restu Erlangga", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.about_desc))
            }
        },
        icon = { Icon(Icons.Default.Info, contentDescription = null) }
    )
}

private suspend fun signIn(context: Context) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun handleSignIn(result: GetCredentialResponse) {
    val credential = result.credential

    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            Log.d("SIGN-IN", "User email: ${googleId.id}")
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}
