package com.examtracker.app.settings

enum class BackgroundStyle {
    DEFAULT,
    NIGHT_BLUE,
    LIGHT_PAPER,
    DARK_GRID;

    companion object {
        fun fromName(value: String?): BackgroundStyle {
            return entries.firstOrNull { it.name == value }
                ?: DEFAULT
        }
    }
}