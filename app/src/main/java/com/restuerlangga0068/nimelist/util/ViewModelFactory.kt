package com.restuerlangga0068.nimelist.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.restuerlangga0068.nimelist.database.AnimeDao

import com.restuerlangga0068.nimelist.ui.screen.MainViewModel
import com.restuerlangga0068.nimelist.ui.screen.DetailViewModel

class ViewModelFactory(
    private val dao: AnimeDao,
    private val pref: UserPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dao, pref) as T
        }
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
