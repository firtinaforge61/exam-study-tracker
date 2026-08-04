package com.examtracker.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.examtracker.app.settings.AppTheme

private val LightScheme = lightColorScheme(
    primary = Color(0xFF2457D6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE4FF),
    onPrimaryContainer = Color(0xFF00174A),

    secondary = Color(0xFF35618A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD2E5FF),
    onSecondaryContainer = Color(0xFF001D35),

    background = Color(0xFFF8F9FF),
    onBackground = Color(0xFF1A1B20),

    surface = Color(0xFFF8F9FF),
    onSurface = Color(0xFF1A1B20),

    surfaceVariant = Color(0xFFE2E2EC),
    onSurfaceVariant = Color(0xFF45464F),

    outline = Color(0xFF767680),
    outlineVariant = Color(0xFFC6C6D0),

    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFB4C5FF),
    onPrimary = Color(0xFF002A78),
    primaryContainer = Color(0xFF003FA9),
    onPrimaryContainer = Color(0xFFDCE4FF),

    secondary = Color(0xFFA1C9F3),
    onSecondary = Color(0xFF003355),
    secondaryContainer = Color(0xFF174A72),
    onSecondaryContainer = Color(0xFFD2E5FF),

    background = Color(0xFF111318),
    onBackground = Color(0xFFE2E2E9),

    surface = Color(0xFF111318),
    onSurface = Color(0xFFE2E2E9),

    surfaceVariant = Color(0xFF45464F),
    onSurfaceVariant = Color(0xFFC6C6D0),

    outline = Color(0xFF90909A),
    outlineVariant = Color(0xFF45464F),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val NightBlueScheme = darkColorScheme(
    primary = Color(0xFF9DB8FF),
    onPrimary = Color(0xFF002B73),
    primaryContainer = Color(0xFF173B70),
    onPrimaryContainer = Color(0xFFDCE6FF),

    secondary = Color(0xFF94C7E8),
    onSecondary = Color(0xFF00344A),
    secondaryContainer = Color(0xFF204B62),
    onSecondaryContainer = Color(0xFFCBEAFF),

    background = Color(0xFF0E1728),
    onBackground = Color(0xFFE6ECF7),

    surface = Color(0xFF0E1728),
    onSurface = Color(0xFFE6ECF7),

    surfaceVariant = Color(0xFF24324A),
    onSurfaceVariant = Color(0xFFD3DEEF),

    outline = Color(0xFF8291A8),
    outlineVariant = Color(0xFF3D4A60)
)

private val LightPaperScheme = lightColorScheme(
    primary = Color(0xFF6A5D24),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF2E7A5),
    onPrimaryContainer = Color(0xFF211B00),

    secondary = Color(0xFF655F40),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECE4BC),
    onSecondaryContainer = Color(0xFF201C05),

    background = Color(0xFFFFFBF1),
    onBackground = Color(0xFF26231B),

    surface = Color(0xFFFFFBF1),
    onSurface = Color(0xFF26231B),

    surfaceVariant = Color(0xFFF0E9D8),
    onSurfaceVariant = Color(0xFF514B3F),

    outline = Color(0xFF7D7666),
    outlineVariant = Color(0xFFD5CEBD)
)

private val DarkGridScheme = darkColorScheme(
    primary = Color(0xFF7CB7FF),
    onPrimary = Color(0xFF00315D),
    primaryContainer = Color(0xFF164A73),
    onPrimaryContainer = Color(0xFFD2E9FF),

    secondary = Color(0xFFA8C8E8),
    onSecondary = Color(0xFF143148),
    secondaryContainer = Color(0xFF2B485F),
    onSecondaryContainer = Color(0xFFD8EBFF),

    background = Color(0xFF0D1117),
    onBackground = Color(0xFFE6EDF3),

    surface = Color(0xFF0D1117),
    onSurface = Color(0xFFE6EDF3),

    surfaceVariant = Color(0xFF21262D),
    onSurfaceVariant = Color(0xFFC9D1D9),

    outline = Color(0xFF6E7681),
    outlineVariant = Color(0xFF30363D)
)

private val AmoledScheme = darkColorScheme(
    primary = Color(0xFF9EB8FF),
    onPrimary = Color(0xFF002E6B),
    primaryContainer = Color(0xFF173A70),
    onPrimaryContainer = Color(0xFFDCE6FF),

    secondary = Color(0xFFB8C6E8),
    onSecondary = Color(0xFF263044),
    secondaryContainer = Color(0xFF3C465B),
    onSecondaryContainer = Color(0xFFDCE6FF),

    background = Color.Black,
    onBackground = Color(0xFFF2F2F2),

    surface = Color.Black,
    onSurface = Color(0xFFF2F2F2),

    surfaceVariant = Color(0xFF161616),
    onSurfaceVariant = Color(0xFFD2D2D2),

    outline = Color(0xFF7A7A7A),
    outlineVariant = Color(0xFF2A2A2A)
)

private val ForestScheme = darkColorScheme(
    primary = Color(0xFF8FD9A8),
    onPrimary = Color(0xFF00391E),
    primaryContainer = Color(0xFF15522F),
    onPrimaryContainer = Color(0xFFAAF5C2),

    secondary = Color(0xFFA4CFB4),
    onSecondary = Color(0xFF103825),
    secondaryContainer = Color(0xFF294F3B),
    onSecondaryContainer = Color(0xFFC0ECD0),

    background = Color(0xFF0E1812),
    onBackground = Color(0xFFE0EAE2),

    surface = Color(0xFF0E1812),
    onSurface = Color(0xFFE0EAE2),

    surfaceVariant = Color(0xFF26352B),
    onSurfaceVariant = Color(0xFFC5D4C9),

    outline = Color(0xFF89978D),
    outlineVariant = Color(0xFF3F4D43)
)

private val SunsetScheme = lightColorScheme(
    primary = Color(0xFF9A3412),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCD),
    onPrimaryContainer = Color(0xFF370D00),

    secondary = Color(0xFF7B5730),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDB4),
    onSecondaryContainer = Color(0xFF2B1700),

    background = Color(0xFFFFF8F4),
    onBackground = Color(0xFF241A16),

    surface = Color(0xFFFFF8F4),
    onSurface = Color(0xFF241A16),

    surfaceVariant = Color(0xFFF5DED5),
    onSurfaceVariant = Color(0xFF53433D),

    outline = Color(0xFF85736C),
    outlineVariant = Color(0xFFD8C2BA)
)

@Composable
fun ExamTrackerTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    systemDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.SYSTEM -> {
            if (systemDarkTheme) DarkScheme else LightScheme
        }

        AppTheme.LIGHT -> LightScheme
        AppTheme.DARK -> DarkScheme
        AppTheme.NIGHT_BLUE -> NightBlueScheme
        AppTheme.LIGHT_PAPER -> LightPaperScheme
        AppTheme.DARK_GRID -> DarkGridScheme
        AppTheme.AMOLED -> AmoledScheme
        AppTheme.FOREST -> ForestScheme
        AppTheme.SUNSET -> SunsetScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}