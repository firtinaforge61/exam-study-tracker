package com.examtracker.app

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.examtracker.app.navigation.AppNavigation
import com.examtracker.app.settings.AppTheme
import com.examtracker.app.settings.SettingsViewModel

@Composable
fun AppRoot(
    settingsViewModel: SettingsViewModel
) {
    val customBackgroundUri by settingsViewModel
        .customBackgroundUri
        .collectAsStateWithLifecycle()

    val appTheme by settingsViewModel
        .appTheme
        .collectAsStateWithLifecycle()

    val hasCustomBackground =
        !customBackgroundUri.isNullOrBlank()

    if (!hasCustomBackground) {
        AppNavigation()
        return
    }

    val baseColors = MaterialTheme.colorScheme

    /*
     * Fotoğraf varken Scaffold ve TopAppBar zeminlerini şeffaflaştırır.
     * Kartlar tamamen kaybolmaz; yarı şeffaf kalır.
     */
    val backgroundColors = baseColors.copy(
        background = Color.Transparent,
        surface = Color.Transparent,

        surfaceVariant = baseColors.surfaceVariant.copy(
            alpha = 0.84f
        ),

        primaryContainer = baseColors.primaryContainer.copy(
            alpha = 0.86f
        ),

        secondaryContainer = baseColors.secondaryContainer.copy(
            alpha = 0.86f
        ),

        tertiaryContainer = baseColors.tertiaryContainer.copy(
            alpha = 0.86f
        )
    )

    val darkOverlayAlpha = when (appTheme) {
        AppTheme.LIGHT,
        AppTheme.LIGHT_PAPER,
        AppTheme.SUNSET -> 0.24f

        AppTheme.SYSTEM -> 0.30f

        AppTheme.DARK,
        AppTheme.NIGHT_BLUE,
        AppTheme.DARK_GRID,
        AppTheme.AMOLED,
        AppTheme.FOREST -> 0.42f
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = Uri.parse(customBackgroundUri)
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(10.dp)
        )

        /*
         * Fotoğraf ne kadar parlak olursa olsun yazıların
         * okunabilir kalmasını sağlar.
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = darkOverlayAlpha
                    )
                )
        )

        MaterialTheme(
            colorScheme = backgroundColors,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes
        ) {
            AppNavigation()
        }
    }
}