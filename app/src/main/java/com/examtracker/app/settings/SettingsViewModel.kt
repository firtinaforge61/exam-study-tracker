package com.examtracker.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val SETTINGS_STATE_TIMEOUT_MILLIS = 5_000L

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val appTheme: StateFlow<AppTheme> =
        repository.appTheme.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                SETTINGS_STATE_TIMEOUT_MILLIS
            ),
            initialValue = AppTheme.SYSTEM
        )

    val customBackgroundUri: StateFlow<String?> =
        repository.customBackgroundUri.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                SETTINGS_STATE_TIMEOUT_MILLIS
            ),
            initialValue = null
        )

    fun setAppTheme(
        appTheme: AppTheme
    ) {
        viewModelScope.launch {
            repository.setAppTheme(
                appTheme
            )
        }
    }

    fun setCustomBackgroundUri(
        uri: String?
    ) {
        viewModelScope.launch {
            repository.setCustomBackgroundUri(
                uri
            )
        }
    }
}