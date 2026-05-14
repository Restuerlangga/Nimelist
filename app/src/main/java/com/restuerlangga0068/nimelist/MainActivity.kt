package com.restuerlangga0068.nimelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.restuerlangga0068.nimelist.database.AnimeDb
import com.restuerlangga0068.nimelist.navigation.NimeNavGraph
import com.restuerlangga0068.nimelist.ui.screen.MainViewModel
import com.restuerlangga0068.nimelist.ui.theme.NimeListTheme
import com.restuerlangga0068.nimelist.util.UserPreferences
import com.restuerlangga0068.nimelist.util.ViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AnimeDb.getInstance(this)
        val dao = database.dao()
        val pref = UserPreferences(this)

        val factory = ViewModelFactory(dao, pref)
        setContent {
            // Pastikan memanggil viewModel dengan factory yang sudah dibuat di atas
            val mainViewModel: MainViewModel = viewModel(factory = factory)

            // Ambil state tema secara reaktif
            val isDark by mainViewModel.isDarkMode.collectAsStateWithLifecycle()

            NimeListTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    NimeNavGraph(
                        viewModel = mainViewModel,
                        factory = factory
                    )
                }
            }
        }
    }
}






