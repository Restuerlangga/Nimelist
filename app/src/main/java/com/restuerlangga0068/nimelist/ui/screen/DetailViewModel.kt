package com.restuerlangga0068.nimelist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restuerlangga0068.nimelist.data.local.AnimeDao
import com.restuerlangga0068.nimelist.database.AnimeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(private val dao: AnimeDao) : ViewModel() {

    fun insert(anime: AnimeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertAnime(anime)
        }
    }

    fun update(anime: AnimeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateAnime(anime)
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val anime = dao.getAnimeById(id)
            if (anime != null) {
                dao.deleteAnime(anime)
            }
        }
    }

    suspend fun getAnimeById(id: Int): AnimeEntity? {
        return dao.getAnimeById(id)
    }
}