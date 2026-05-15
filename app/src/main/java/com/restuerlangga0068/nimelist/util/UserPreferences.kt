package com.restuerlangga0068.nimelist.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
        val SORT_BY_RATING = booleanPreferencesKey("sort_by_rating")

        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    val getSortSetting: Flow<Boolean> = context.dataStore.data.map { it[SORT_BY_RATING] ?: false }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[IS_DARK_MODE]?: false }

    suspend fun saveSortSetting(isByRating: Boolean) {
        context.dataStore.edit { it[SORT_BY_RATING] = isByRating }
    }
    suspend fun saveThemeSetting(isDark: Boolean) {
        context.dataStore.edit { it[IS_DARK_MODE] = isDark }
    }
}