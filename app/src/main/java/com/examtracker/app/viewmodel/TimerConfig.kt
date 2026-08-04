package com.examtracker.app.viewmodel

data class TimerConfig(
    val modeType: TimerModeType,
    val focusMinutes: Int,
    val breakMinutes: Int,
    val totalCycles: Int
) {

    companion object {

        fun pomodoro25x5(): TimerConfig {
            return TimerConfig(
                modeType = TimerModeType.POMODORO_25_5,
                focusMinutes = 25,
                breakMinutes = 5,
                totalCycles = 4
            )
        }

        fun pomodoro50x10(): TimerConfig {
            return TimerConfig(
                modeType = TimerModeType.POMODORO_50_10,
                focusMinutes = 50,
                breakMinutes = 10,
                totalCycles = 4
            )
        }

        fun customPomodoro(
            focusMinutes: Int,
            breakMinutes: Int,
            totalCycles: Int
        ): TimerConfig {
            return TimerConfig(
                modeType = TimerModeType.CUSTOM_POMODORO,
                focusMinutes = focusMinutes.coerceAtLeast(1),
                breakMinutes = breakMinutes.coerceAtLeast(1),
                totalCycles = totalCycles.coerceAtLeast(1)
            )
        }

        fun stopwatch(): TimerConfig {
            return TimerConfig(
                modeType = TimerModeType.STOPWATCH,
                focusMinutes = 0,
                breakMinutes = 0,
                totalCycles = 1
            )
        }

        fun countdown(
            focusMinutes: Int
        ): TimerConfig {
            return TimerConfig(
                modeType = TimerModeType.COUNTDOWN,
                focusMinutes = focusMinutes.coerceAtLeast(1),
                breakMinutes = 0,
                totalCycles = 1
            )
        }
    }
}