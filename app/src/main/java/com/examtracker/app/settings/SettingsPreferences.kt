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

        val CUSTOM_BACKGROUND_URI =
            stringPreferencesKey("custom_background_uri")
    }

    val appTheme: Flow<String> =
        context.settingsDataStore.data.map { preferences ->
            preferences[APP_THEME]
                ?: AppTheme.SYSTEM.name
        }

    val customBackgroundUri: Flow<String?> =
        context.settingsDataStore.data.map { preferences ->
            preferences[CUSTOM_BACKGROUND_URI]
        }

    suspend fun setAppTheme(
        value: String
    ) {
        context.settingsDataStore.edit { preferences ->
            preferences[APP_THEME] = value
        }
    }

    suspend fun setCustomBackgroundUri(
        value: String?
    ) {
        context.settingsDataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(
                    CUSTOM_BACKGROUND_URI
                )
            } else {
                preferences[CUSTOM_BACKGROUND_URI] = value
            }
        }
    }
}