package com.restuerlangga0068.nimelist.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restuerlangga0068.nimelist.data.api.RetrofitClient
import com.restuerlangga0068.nimelist.database.AnimeDao
import com.restuerlangga0068.nimelist.database.AnimeEntity
import com.restuerlangga0068.nimelist.util.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, SUCCESS, ERROR }

class MainViewModel(private val dao: AnimeDao, private val pref: UserPreferences) : ViewModel() {

    private val _apiStatus = MutableStateFlow(ApiStatus.LOADING)
    val apiStatus: StateFlow<ApiStatus> = _apiStatus.asStateFlow()

    val isSortedByRating: StateFlow<Boolean> = pref.getSortSetting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isDarkMode: StateFlow<Boolean> = pref.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    val isLoggedIn: StateFlow<Boolean> = pref.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    val userEmailState: StateFlow<String> = pref.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), "")

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val animeList: StateFlow<List<AnimeEntity>> = isSortedByRating.flatMapLatest { byRating ->
        // Catatan: Jika fungsi rating sorted belum ada di Dao, gunakan dao.getAllAnime() dulu sementara waktu
        dao.getAllAnime()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    // Ganti blok init lama kamu dengan yang ini:
    init {
        viewModelScope.launch {
            // Mengamati email secara realtime. Begitu terisi (saat startup atau login), API langsung ditembak
            pref.userEmail.collect { email ->
                if (email.isNotEmpty()) {
                    refreshDataFromServer()
                } else {
                    // Jika memang belum login, pastikan statusnya tidak stuck di LOADING
                    _apiStatus.value = ApiStatus.SUCCESS
                }
            }
        }
    }


    // Tambahkan parameter email atau ambil langsung dari DataStore/Preferences jika ada
    fun refreshDataFromServer() {
        viewModelScope.launch {
            _apiStatus.value = ApiStatus.LOADING
            try {
                val apiKey = "sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"
                val token = "Bearer sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"

                // 1. Ambil email
                val currentEmail = pref.userEmail.first()

                // 🔍 TAMBAHKAN LOG INI UNTUK CEK EMAIL-NYA KOSONG ATAU TIDAK:
                android.util.Log.d("API_DEBUG", "refreshDataFromServer dipanggil. Email saat ini di DataStore: '$currentEmail'")

                if (currentEmail.isEmpty()) {
                    // 🔍 TAMBAHKAN LOG INI JUGA:
                    android.util.Log.w("API_DEBUG", "Proses API dibatalkan karena email kosong!")
                    _apiStatus.value = ApiStatus.SUCCESS
                    return@launch
                }

                val filterQuery = "eq.$currentEmail"
                android.util.Log.d("API_DEBUG", "Memulai fetch data ke API dengan query: $filterQuery")

                val remoteData = RetrofitClient.animeApiService.getAllAnime(apiKey, token, filterQuery)
                android.util.Log.d("API_DEBUG", "Data berhasil didapat dari API: $remoteData")

                dao.insertAnimeList(remoteData)
                _apiStatus.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                android.util.Log.e("API_ERROR", "Gagal fetch data: ${e.message}", e)
                _apiStatus.value = ApiStatus.ERROR
            }
        }
    }



    fun toggleTheme(current: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(!current)
        }
    }

    fun toggleSort(currentSetting: Boolean) {
        viewModelScope.launch {
            pref.saveSortSetting(!currentSetting)
        }
    }


    fun loginSukses(email: String, name: String, photoUrl: String) {
        viewModelScope.launch {
            pref.saveLoginSession(true, email, name, photoUrl)
            // Setelah berhasil login, langsung tarik data dari server sesuai email tersebut
            refreshDataFromServer()
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.clearSession()
        }
    }

    fun addAnime(
        title: String,
        description: String,
        imageUrl: String,
        rating: Int,
        isCompleted: Boolean,
        userEmail: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val apiKey = "sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"
                val token = "Bearer sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"

                // 🔥 PERBAIKAN: Sesuaikan argumen dengan struktur data AnimeEntity milikmu
                val newAnime = AnimeEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    rating = rating.toDouble(), // 🔥 FIX 1: Ubah Int menjadi Double memakai .toDouble()
                    isCompleted = isCompleted,
                    email = userEmail,
                    review = "" // 🔥 FIX 2: Masukkan parameter 'review' yang diminta (bisa diisi string kosong dulu)
                )

                val response = RetrofitClient.animeApiService.insertAnime(apiKey, token, listOf(newAnime))

                if (response.isSuccessful) {
                    refreshDataFromServer(  )
                    onSuccess()
                } else {
                    Log.e("API_ADD_ERROR", "Gagal insert: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API_ADD_ERROR", "Error: ${e.message}")
            }
        }
    }

    fun saveLoginToPreferences(name: String, email: String, photoUrl: String) {
        viewModelScope.launch {
            pref.saveLoginSession(
                isLoggedIn = true,
                email = email,
                name = name,
                photoUrl = photoUrl
            )
            // 🔥 Setelah sukses tersimpan, langsung panggil API server!
            refreshDataFromServer()
        }
    }

    fun clearLoginFromPreferences() {
        viewModelScope.launch {
            pref.clearSession()
        }
    }


}