package com.restuerlangga0068.nimelist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restuerlangga0068.nimelist.data.api.RetrofitClient // Sesuaikan import RetrofitClient
import com.restuerlangga0068.nimelist.database.AnimeDao
import com.restuerlangga0068.nimelist.database.AnimeEntity
import com.restuerlangga0068.nimelist.util.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val animeList: StateFlow<List<AnimeEntity>> = isSortedByRating.flatMapLatest { byRating ->
        // Catatan: Jika fungsi rating sorted belum ada di Dao, gunakan dao.getAllAnime() dulu sementara waktu
        dao.getAllAnime()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    init {
        refreshDataFromServer()
    }

    fun refreshDataFromServer() {
        viewModelScope.launch {
            _apiStatus.value = ApiStatus.LOADING
            try {
                val apiKey = "sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"
                val token = "Bearer sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"

                // PERBAIKAN: Akses RetrofitClient secara singleton & insert lewat instance dao
                val remoteData = RetrofitClient.animeApiService.getAllAnime(apiKey, token)
                android.util.Log.d("API_DEBUG", "Data dari API: $remoteData")
                dao.insertAnimeList(remoteData)

                _apiStatus.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
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

    fun logout() {
        viewModelScope.launch {
            pref.saveLoginSession(false)

        }
    }


    fun loginSukses() {
        viewModelScope.launch {
            pref.saveLoginSession(true)
        }
    }

}