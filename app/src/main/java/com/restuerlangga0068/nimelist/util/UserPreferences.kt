package com.restuerlangga0068.nimelist.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")

        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        val SORT_BY_RATING = booleanPreferencesKey("sort_by_rating")

        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_PHOTO_KEY = stringPreferencesKey("user_photo")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }

    val getSortSetting: Flow<Boolean> = context.dataStore.data.map { it[SORT_BY_RATING] ?: false }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[IS_DARK_MODE] ?: false }

    val userEmail: Flow<String> = context.dataStore.data.map { it[USER_EMAIL_KEY] ?: "" }
    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME_KEY] ?: "" }
    val userPhoto: Flow<String> = context.dataStore.data.map { it[USER_PHOTO_KEY] ?: "" }

    suspend fun saveLoginSession(
        isLoggedIn: Boolean,
        email: String = "",
        name: String = "",
        photoUrl: String = ""
    ) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
            preferences[USER_PHOTO_KEY] = photoUrl
        }
    }

    suspend fun saveSortSetting(isByRating: Boolean) {
        context.dataStore.edit { it[SORT_BY_RATING] = isByRating }
    }

    suspend fun saveThemeSetting(isDark: Boolean) {
        context.dataStore.edit { it[IS_DARK_MODE] = isDark }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = false
            preferences[USER_EMAIL_KEY] = ""
            preferences[USER_NAME_KEY] = ""
            preferences[USER_PHOTO_KEY] = ""
        }
}   }
