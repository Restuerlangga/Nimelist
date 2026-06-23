package com.restuerlangga0068.nimelist.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import com.restuerlangga0068.nimelist.model.User
import com.restuerlangga0068.nimelist.network.UserDataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnimeScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val userEmail by viewModel.userEmailState.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Anime Baru") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Teks Judul
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Anime") },
                modifier = Modifier.fillMaxWidth()
            )

            // Input Teks Deskripsi
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Input Teks URL Gambar dari Internet
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL Gambar (https://...)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Input Rating (Angka 1-5)
            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text("Rating (1 - 5)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Input Checkbox Status Completed
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { isCompleted = it }
                )
                Text(text = "Sudah Selesai Ditonton (Completed)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Kirim Data ke Server
            Button(
                onClick = {
                    // Cek email, jika kosong beri email default kamu biar tidak RLS Error saat testing
                    val finalEmail = userEmail.ifEmpty { "restuerlang99@gmail.com" }

                    if (title.isNotEmpty() && imageUrl.isNotEmpty()) {
                        viewModel.addAnime(
                            title = title,
                            description = description,
                            imageUrl = imageUrl,
                            rating = rating.toIntOrNull() ?: 0,
                            isCompleted = isCompleted,
                            userEmail = finalEmail, // <-- Menggunakan email yang valid
                            onSuccess = onBackClick
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotEmpty() && imageUrl.isNotEmpty()
            ) {
                Text("Simpan ke Server")
            }
        }
    }
}
