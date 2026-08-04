package com.examtracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.examtracker.app.navigation.AppNavigation
import com.examtracker.app.settings.AppTheme
import com.examtracker.app.settings.SettingsPreferences
import com.examtracker.app.settings.SettingsRepository
import com.examtracker.app.settings.SettingsViewModel
import com.examtracker.app.settings.SettingsViewModelFactory
import com.examtracker.app.ui.theme.ExamTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val applicationContext =
                LocalContext.current.applicationContext

            val settingsRepository = remember {
                SettingsRepository(
                    preferences = SettingsPreferences(
                        context = applicationContext
                    )
                )
            }

            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(
                    repository = settingsRepository
                )
            )

            val appTheme by settingsViewModel
                .appTheme
                .collectAsStateWithLifecycle()

            val systemDarkTheme = isSystemInDarkTheme()


            ExamTrackerTheme(
                appTheme = appTheme,
                systemDarkTheme = systemDarkTheme
            ) {
                AppNavigation()
            }
        }
    }
}