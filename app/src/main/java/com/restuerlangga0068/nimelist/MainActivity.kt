package com.restuerlangga0068.nimelist


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.restuerlangga0068.nimelist.data.initialAnimeData
import com.restuerlangga0068.nimelist.database.AnimeDb
import com.restuerlangga0068.nimelist.navigation.NimeNavGraph
import com.restuerlangga0068.nimelist.ui.theme.NimeListTheme
import com.restuerlangga0068.nimelist.util.ViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AnimeDb.getInstance(this)
        val dao = database.dao()

        val factory = ViewModelFactory(dao
        )
        setContent {
            NimeListTheme {

                val fullAnimeList = remember { mutableStateListOf(*initialAnimeData.toTypedArray()) }


                NimeNavGraph(factory = factory)

            }
        }
    }
}






