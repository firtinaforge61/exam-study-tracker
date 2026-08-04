package com.examtracker.app.viewmodel

enum class TimerModeType {
    POMODORO_25_5,
    POMODORO_50_10,
    CUSTOM_POMODORO,
    STOPWATCH,
    COUNTDOWN;

    val isPomodoroStyle: Boolean
        get() {
            return this == POMODORO_25_5 ||
                    this == POMODORO_50_10 ||
                    this == CUSTOM_POMODORO
        }

    val hasBreaks: Boolean
        get() = isPomodoroStyle

    val isCountUp: Boolean
        get() = this == STOPWATCH

    companion object {
        fun fromArgOrDefault(
            value: String?
        ): TimerModeType {
            return entries.firstOrNull {
                it.name == value
            } ?: POMODORO_25_5
        }
    }
}