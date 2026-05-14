package com.restuerlangga0068.nimelist.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.restuerlangga0068.nimelist.data.local.AnimeDao


class ViewModelFactory(private val dao: AnimeDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(com.restuerlangga0068.nimelist.ui.screen.MainViewModel::class.java)) {
            return com.restuerlangga0068.nimelist.ui.screen.MainViewModel(dao) as T
        }
        if (modelClass.isAssignableFrom(com.restuerlangga0068.nimelist.ui.screen.DetailViewModel::class.java)) {
            return com.restuerlangga0068.nimelist.ui.screen.DetailViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}