package com.examtracker.app.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(
    name = "settings"
)

class SettingsPreferences(
    private val context: Context
) {

    private companion object {
        val APP_THEME =
            stringPreferencesKey("app_theme")
    }

    val appTheme: Flow<String> =
        context.settingsDataStore.data.map { preferences ->
            preferences[APP_THEME]
                ?: AppTheme.SYSTEM.name
        }

    suspend fun setAppTheme(
        value: String
    ) {
        context.settingsDataStore.edit { preferences ->
            preferences[APP_THEME] = value
        }
    }
}