package com.restuerlangga0068.nimelist.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.restuerlangga0068.nimelist.ui.screen.DetailViewModel
import com.restuerlangga0068.nimelist.ui.screen.HomeScreen
import com.restuerlangga0068.nimelist.ui.screen.MainViewModel
import com.restuerlangga0068.nimelist.ui.screen.ReviewScreen
import com.restuerlangga0068.nimelist.util.ViewModelFactory

@Composable
fun NimeNavGraph(viewModel: MainViewModel,
                 factory: ViewModelFactory,
                 navController: NavHostController = rememberNavController()) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "home") {

        // HALAMAN HOME
        composable("home") {

            HomeScreen(
                viewModel = viewModel,
                onItemClick = { animeId ->
                    navController.navigate("review/$animeId")
                }
            )
        }


        composable(
            route = "review/{animeId}",
            arguments = listOf(navArgument("animeId") { type = NavType.IntType }) // Tambahkan argumen tipe Int
        ) { backStackEntry ->
            val animeId = backStackEntry.arguments?.getInt("animeId") ?: -1
            val detailViewModel: DetailViewModel = viewModel(factory = factory)

            ReviewScreen(
                animeId = animeId, // Pakai variabel animeId hasil getInt
                viewModel = detailViewModel, // Kirim viewModel ke screen
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