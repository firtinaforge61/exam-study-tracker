package com.examtracker.app.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val preferences: SettingsPreferences
) {

    val appTheme: Flow<AppTheme> =
        preferences.appTheme.map { value ->
            AppTheme.fromName(value)
        }

    suspend fun setAppTheme(
        appTheme: AppTheme
    ) {
        preferences.setAppTheme(
            appTheme.name
        )
    }
}