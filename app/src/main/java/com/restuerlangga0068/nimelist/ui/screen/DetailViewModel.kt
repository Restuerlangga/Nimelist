package com.restuerlangga0068.nimelist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restuerlangga0068.nimelist.database.AnimeDao
import com.restuerlangga0068.nimelist.database.AnimeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(private val dao: AnimeDao) : ViewModel() {

    fun insert(anime: AnimeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertAnime(anime)
        }
    }

    // Catatan: Pastikan fungsi updateAnime ada di AnimeDao kamu jika ingin menggunakannya
    fun update(anime: AnimeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            // dao.updateAnime(anime)
        }
    }

    // PERBAIKAN: Ubah id: Int menjadi id: String
    fun delete(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val anime = dao.getAnimeById(id)
            if (anime != null) {
                dao.deleteAnimeById(id) // Mengarah ke deleteAnimeById String
            }
        }
    }

    // PERBAIKAN: Ubah id: Int menjadi id: String
    suspend fun getAnimeById(id: String): AnimeEntity? {
        if (id.isEmpty()) return null // Mengubah pengecekan -1 menjadi pengecekan String kosong
        return dao.getAnimeById(id)
    }
}