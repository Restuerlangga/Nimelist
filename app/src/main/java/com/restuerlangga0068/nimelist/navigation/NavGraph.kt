package com.restuerlangga0068.nimelist.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.restuerlangga0068.nimelist.ui.screen.AddAnimeScreen
import com.restuerlangga0068.nimelist.ui.screen.HomeScreen
import com.restuerlangga0068.nimelist.ui.screen.MainViewModel
import com.restuerlangga0068.nimelist.ui.screen.ReviewScreen
import com.restuerlangga0068.nimelist.util.ViewModelFactory

@Composable
fun NimeNavGraph(
    viewModel: MainViewModel,
    factory: ViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current


    NavHost(navController = navController, startDestination = "home") {

        // 1. HALAMAN UTAMA (HOME)
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onItemClick = { animeId ->

                    navController.navigate("review/$animeId")
                },
                onAddClick = {

                    navController.navigate("add_anime")
                }
            )
        }

        // 2. HALAMAN TAMBAH ANIME (Poin 2d)
        composable("add_anime") {
            AddAnimeScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }


        composable(
            route = "review/{animeId}",
            arguments = listOf(navArgument("animeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val animeId = backStackEntry.arguments?.getString("animeId") ?: "new"

            ReviewScreen(
                animeId = animeId,
                viewModel = viewModel, // 🔥 Pakai MainViewModel
                onBackClick = { navController.popBackStack() },
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

