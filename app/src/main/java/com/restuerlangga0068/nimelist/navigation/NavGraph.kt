package com.restuerlangga0068.nimelist.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.restuerlangga0068.nimelist.data.Anime
import com.restuerlangga0068.nimelist.ui.screen.HomeScreen
import com.restuerlangga0068.nimelist.ui.screen.ReviewScreen


@Composable
fun NimeNavGraph(fullAnimeList: SnapshotStateList<Anime>) {
    val navController = rememberNavController()
    val context = LocalContext.current



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

