package com.restuerlangga0068.nimelist.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restuerlangga0068.nimelist.data.api.RetrofitClient
import com.restuerlangga0068.nimelist.database.AnimeDao
import com.restuerlangga0068.nimelist.database.AnimeEntity
import com.restuerlangga0068.nimelist.util.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        dao.getAllAnime()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            pref.userEmail.collect { email ->
                if (email.isNotEmpty()) {
                    refreshDataFromServer()
                } else {
                    _apiStatus.value = ApiStatus.SUCCESS
                }
            }
        }
    }

    fun refreshDataFromServer() {
        viewModelScope.launch(Dispatchers.IO) {
            _apiStatus.value = ApiStatus.LOADING
            try {
                val apiKey = "sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"
                val token = "Bearer sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"

                val currentEmail = pref.userEmail.first()

                if (currentEmail.isEmpty()) {
                    _apiStatus.value = ApiStatus.SUCCESS
                    return@launch
                }

                val filterQuery = "eq.$currentEmail"
                val remoteData = RetrofitClient.animeApiService.getAllAnime(apiKey, token, filterQuery)

                Log.d("REFRESH_DEBUG", "Email: $currentEmail, Data dari server: ${remoteData.size} item")
                remoteData.forEach { Log.d("REFRESH_DEBUG", "  - ${it.id} | ${it.title} | email: ${it.email}") }

                // Hapus dulu data lama milik user ini, baru insert yang baru
                dao.deleteAllByEmail(currentEmail)
                dao.insertAnimeList(remoteData)

                _apiStatus.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.e("API_ERROR", "Gagal fetch data: ${e.message}", e)
                _apiStatus.value = ApiStatus.ERROR
            }
        }
    }

    suspend fun getAnimeById(id: String): AnimeEntity? {
        return withContext(Dispatchers.IO) {
            dao.getAnimeById(id)
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = "sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"
                val token = "Bearer sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"

                val newAnime = AnimeEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    rating = rating.toDouble(),
                    isCompleted = isCompleted,
                    email = userEmail,
                    review = ""
                )

                val response = RetrofitClient.animeApiService.insertAnime(apiKey, token, listOf(newAnime))

                if (response.isSuccessful) {
                    refreshDataFromServer()
                    withContext(Dispatchers.Main) { onSuccess() }
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
            refreshDataFromServer()
        }
    }

    fun clearLoginFromPreferences() {
        viewModelScope.launch {
            pref.clearSession()
        }
    }

    fun updateAnime(anime: AnimeEntity, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = "sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"
                val token = "Bearer sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"

                val response = RetrofitClient.animeApiService.updateAnime(apiKey, token, "eq.${anime.id}", anime)

                Log.d("UPDATE_DEBUG", "Code: ${response.code()}, Body: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    dao.updateAnime(anime)
                    refreshDataFromServer()
                    withContext(Dispatchers.Main) { onSuccess() }
                } else {
                    Log.e("UPDATE_ERROR", "Gagal update: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UPDATE_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    fun deleteAnime(anime: AnimeEntity, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = "sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"
                val token = "Bearer sb_publishable_v4RMQvw75dy6Fv5bYFSedw_mGQCtXJK"

                val response = RetrofitClient.animeApiService.deleteAnime(apiKey, token, "eq.${anime.id}")

                Log.d("DELETE_DEBUG", "Code: ${response.code()}, Body: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    dao.deleteAnime(anime)
                    refreshDataFromServer()
                    withContext(Dispatchers.Main) { onSuccess() }
                } else {
                    Log.e("DELETE_ERROR", "Gagal hapus di API: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DELETE_ERROR", "Exception: ${e.message}", e)
            }
        }
    }
}
