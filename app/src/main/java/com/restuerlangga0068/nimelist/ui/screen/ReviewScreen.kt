package com.restuerlangga0068.nimelist.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.restuerlangga0068.nimelist.R
import com.restuerlangga0068.nimelist.data.Anime

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