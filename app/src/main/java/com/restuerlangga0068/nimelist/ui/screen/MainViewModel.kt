package com.restuerlangga0068.nimelist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restuerlangga0068.nimelist.data.local.AnimeDao
import com.restuerlangga0068.nimelist.database.AnimeEntity
import com.restuerlangga0068.nimelist.util.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class MainViewModel(private val dao: AnimeDao, private val pref: UserPreferences) : ViewModel() {


    val isSortedByRating: StateFlow<Boolean> = pref.getSortSetting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val animeList: StateFlow<List<AnimeEntity>> = isSortedByRating.flatMapLatest { byRating ->
        if (byRating) dao.getAllAnimeSortedByRating()
        else dao.getAllAnime()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )


    fun toggleSort(currentSetting: Boolean) {
        viewModelScope.launch {
            pref.saveSortSetting(!currentSetting)
        }
    }
}