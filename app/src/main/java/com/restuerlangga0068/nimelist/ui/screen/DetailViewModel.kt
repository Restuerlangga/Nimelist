package com.restuerlangga0068.nimelist.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restuerlangga0068.nimelist.data.api.RetrofitClient
import com.restuerlangga0068.nimelist.database.AnimeDao
import com.restuerlangga0068.nimelist.database.AnimeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(
    private val dao: AnimeDao,
    private val apiKey: String = "sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK",
    private val token: String = "Bearer sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"
) : ViewModel() {

    fun insert(anime: AnimeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertAnime(anime)
        }
    }


    fun update(anime: AnimeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Update ke Supabase
                val response = RetrofitClient.animeApiService.updateAnime(apiKey, token, "eq.${anime.id}", anime)

                // 2. Jika sukses di server, baru update di lokal
                if (response.isSuccessful) {
                    dao.updateAnime(anime)
                } else {
                    Log.e("DETAIL_VM", "Gagal update ke API: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("DETAIL_VM", "Error: ${e.message}")
            }
        }
    }

    // PERBAIKAN: Ubah id: Int menjadi id: String
    fun delete(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val anime = dao.getAnimeById(id)
            if (anime != null) {
                dao.deleteAnimeById(id)
            }
        }
    }

    // PERBAIKAN: Ubah id: Int menjadi id: String
    suspend fun getAnimeById(id: String): AnimeEntity? {
        if (id.isEmpty()) return null
        return dao.getAnimeById(id)
    }
}