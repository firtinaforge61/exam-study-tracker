package com.examtracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.examtracker.app.navigation.AppNavigation
import com.examtracker.app.ui.theme.ExamTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExamTrackerTheme {
                AppNavigation()
            }
        }
    }
}