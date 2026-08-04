package com.examtracker.app.viewmodel

data class TimerUiState(
    val examId: Long = -1L,
    val subjectId: Long = -1L,
    val examName: String = "",
    val subjectName: String = "",
    val modeType: TimerModeType = TimerModeType.POMODORO_25_5,
    val focusDurationMillis: Long = 0L,
    val breakDurationMillis: Long = 0L,
    val totalCycles: Int = 1,
    val currentCycle: Int = 1,
    val phase: TimerPhase = TimerPhase.IDLE,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val completedFocusMillisTotal: Long = 0L,
    val completedPomodoros: Int = 0,
    val remainingMillis: Long = 0L,
    val elapsedMillis: Long = 0L,
    val isFinished: Boolean = false,
    val isRecordSaved: Boolean = false,
    val todaysFocusMinutes: Int = 0,
    val isExamMissing: Boolean = false
) {

    val finalFocusMinutes: Int
        get() {
            val currentPhaseFocusMillis =
                if (phase == TimerPhase.FOCUS) {
                    elapsedMillis
                } else {
                    0L
                }

            val totalMillis =
                completedFocusMillisTotal + currentPhaseFocusMillis

            return (totalMillis / 60_000L)
                .toInt()
                .coerceAtLeast(0)
        }

    val progressFraction: Float
        get() {
            val totalPhaseMillis =
                if (phase == TimerPhase.BREAK) {
                    breakDurationMillis
                } else {
                    focusDurationMillis
                }

            if (
                modeType == TimerModeType.STOPWATCH ||
                totalPhaseMillis <= 0L
            ) {
                return 0f
            }

            val elapsedInPhase =
                totalPhaseMillis - remainingMillis

            return (
                    elapsedInPhase.toFloat() /
                            totalPhaseMillis.toFloat()
                    ).coerceIn(0f, 1f)
        }
}