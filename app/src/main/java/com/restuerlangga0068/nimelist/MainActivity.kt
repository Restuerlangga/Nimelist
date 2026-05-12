package com.restuerlangga0068.nimelist


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.restuerlangga0068.nimelist.data.initialAnimeData
import com.restuerlangga0068.nimelist.navigation.NimeNavGraph
import com.restuerlangga0068.nimelist.ui.theme.NimeListTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NimeListTheme {

                val fullAnimeList = remember { mutableStateListOf(*initialAnimeData.toTypedArray()) }

                NimeNavGraph(fullAnimeList = fullAnimeList)

            }
        }
    }
}






