package com.examtracker.app.settings

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK,
    NIGHT_BLUE,
    LIGHT_PAPER,
    DARK_GRID,
    AMOLED,
    FOREST,
    SUNSET;

    companion object {
        fun fromName(value: String?): AppTheme {
            return entries.firstOrNull { it.name == value }
                ?: SYSTEM
        }
    }
}