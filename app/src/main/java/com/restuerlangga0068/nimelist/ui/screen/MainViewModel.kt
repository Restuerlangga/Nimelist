package com.restuerlangga0068.nimelist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restuerlangga0068.nimelist.data.local.AnimeDao
import com.restuerlangga0068.nimelist.database.AnimeEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(dao: AnimeDao) : ViewModel() {

    val data: StateFlow<List<AnimeEntity>> = dao.getAllAnime().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )
}