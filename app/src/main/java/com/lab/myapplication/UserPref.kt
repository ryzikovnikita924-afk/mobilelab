package com.lab.myapplication

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_settings"
)

data class UserSettings(
    val language: String = "ru",
    val darkTheme: Boolean = false
)

class UserPreferences(context: Context) {

    private val dataStore = context.applicationContext.dataStore

    val settings: Flow<UserSettings> = dataStore.data.map { preferences ->
        UserSettings(
            language = preferences[LANGUAGE] ?: "ru",
            darkTheme = preferences[DARK_THEME] ?: false
        )
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME] = enabled
        }
    }

    private companion object {
        val LANGUAGE = stringPreferencesKey("language")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }
}