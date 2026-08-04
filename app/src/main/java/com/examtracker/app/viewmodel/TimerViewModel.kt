package com.examtracker.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examtracker.app.data.local.StudyEntryTypeKeys
import com.examtracker.app.data.local.StudyRecordEntity
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.data.repository.StudyRecordRepository
import com.examtracker.app.data.repository.SubjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.max

private const val TIMER_REFRESH_INTERVAL_MILLIS = 250L
private const val ONE_MINUTE_MILLIS = 60_000L

private const val KEY_PHASE = "timer_phase"
private const val KEY_IS_RUNNING = "timer_is_running"
private const val KEY_IS_PAUSED = "timer_is_paused"
private const val KEY_CURRENT_CYCLE = "timer_current_cycle"
private const val KEY_COMPLETED_POMODOROS = "timer_completed_pomodoros"
private const val KEY_COMPLETED_FOCUS_MILLIS = "timer_completed_focus_millis"
private const val KEY_PHASE_START_TIMESTAMP = "timer_phase_start_timestamp"
private const val KEY_PAUSED_REMAINING_MILLIS = "timer_paused_remaining_millis"
private const val KEY_PAUSED_ELAPSED_MILLIS = "timer_paused_elapsed_millis"
private const val KEY_IS_FINISHED = "timer_is_finished"
private const val KEY_IS_RECORD_SAVED = "timer_is_record_saved"

class TimerViewModel(
    private val examId: Long,
    private val subjectId: Long,
    private val config: TimerConfig,
    private val examRepository: ExamRepository,
    private val subjectRepository: SubjectRepository,
    private val studyRecordRepository: StudyRecordRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val focusDurationMillis =
        config.focusMinutes.toLong() * ONE_MINUTE_MILLIS

    private val breakDurationMillis =
        config.breakMinutes.toLong() * ONE_MINUTE_MILLIS

    private val _uiState = MutableStateFlow(
        TimerUiState(
            examId = examId,
            subjectId = subjectId,
            modeType = config.modeType,
            focusDurationMillis = focusDurationMillis,
            breakDurationMillis = breakDurationMillis,
            totalCycles = config.totalCycles.coerceAtLeast(1),
            remainingMillis = initialRemainingMillis()
        )
    )

    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null

    init {
        loadExamAndSubject()
        loadTodaysFocusMinutes()
        restoreTimerState()
    }

    private fun loadExamAndSubject() {
        viewModelScope.launch {
            combine(
                examRepository.getExamById(examId),
                subjectRepository.getSubjectById(subjectId)
            ) { exam, subject ->
                exam to subject
            }.collect { (exam, subject) ->
                _uiState.value = _uiState.value.copy(
                    examName = exam?.name.orEmpty(),
                    subjectName = subject?.name.orEmpty(),
                    isExamMissing = exam == null
                )
            }
        }
    }

    private fun loadTodaysFocusMinutes() {
        viewModelScope.launch {
            studyRecordRepository
                .getAllStudyRecords()
                .collect { records ->
                    val startOfToday = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis

                    val endOfToday = startOfToday + 24 * 60 * 60 * 1000L

                    val todayMinutes = records
                        .filter {
                            it.recordDateMillis in startOfToday until endOfToday
                        }
                        .sumOf { it.durationMinutes }

                    _uiState.value = _uiState.value.copy(
                        todaysFocusMinutes = todayMinutes
                    )
                }
        }
    }

    private fun restoreTimerState() {
        val restoredPhaseName =
            savedStateHandle.get<String>(KEY_PHASE)

        if (restoredPhaseName == null) {
            return
        }

        val restoredPhase = runCatching {
            TimerPhase.valueOf(restoredPhaseName)
        }.getOrDefault(TimerPhase.IDLE)

        val restoredRunning =
            savedStateHandle.get<Boolean>(KEY_IS_RUNNING) ?: false

        val restoredPaused =
            savedStateHandle.get<Boolean>(KEY_IS_PAUSED) ?: false

        val restoredCycle =
            savedStateHandle.get<Int>(KEY_CURRENT_CYCLE) ?: 1

        val restoredPomodoros =
            savedStateHandle.get<Int>(KEY_COMPLETED_POMODOROS) ?: 0

        val restoredCompletedFocusMillis =
            savedStateHandle.get<Long>(KEY_COMPLETED_FOCUS_MILLIS) ?: 0L

        val restoredFinished =
            savedStateHandle.get<Boolean>(KEY_IS_FINISHED) ?: false

        val restoredRecordSaved =
            savedStateHandle.get<Boolean>(KEY_IS_RECORD_SAVED) ?: false

        _uiState.value = _uiState.value.copy(
            phase = restoredPhase,
            isRunning = restoredRunning,
            isPaused = restoredPaused,
            currentCycle = restoredCycle,
            completedPomodoros = restoredPomodoros,
            completedFocusMillisTotal = restoredCompletedFocusMillis,
            isFinished = restoredFinished,
            isRecordSaved = restoredRecordSaved
        )

        when {
            restoredFinished -> {
                updateVisibleTime()
            }

            restoredPaused -> {
                val pausedRemaining =
                    savedStateHandle.get<Long>(
                        KEY_PAUSED_REMAINING_MILLIS
                    ) ?: initialRemainingMillis()

                val pausedElapsed =
                    savedStateHandle.get<Long>(
                        KEY_PAUSED_ELAPSED_MILLIS
                    ) ?: 0L

                _uiState.value = _uiState.value.copy(
                    remainingMillis = pausedRemaining,
                    elapsedMillis = pausedElapsed
                )
            }

            restoredRunning -> {
                updateVisibleTime()
                startTicker()
            }
        }
    }

    fun start() {
        if (_uiState.value.isFinished) {
            return
        }

        if (_uiState.value.isRunning) {
            return
        }

        val startingPhase =
            if (config.modeType == TimerModeType.STOPWATCH) {
                TimerPhase.FOCUS
            } else {
                TimerPhase.FOCUS
            }

        savedStateHandle[KEY_PHASE_START_TIMESTAMP] =
            System.currentTimeMillis()

        _uiState.value = _uiState.value.copy(
            phase = startingPhase,
            isRunning = true,
            isPaused = false,
            remainingMillis = phaseDurationMillis(startingPhase),
            elapsedMillis = 0L
        )

        persistState()
        startTicker()
    }

    fun pause() {
        val state = _uiState.value

        if (!state.isRunning || state.isPaused || state.isFinished) {
            return
        }

        updateVisibleTime()
        tickerJob?.cancel()
        tickerJob = null

        savedStateHandle[KEY_PAUSED_REMAINING_MILLIS] =
            _uiState.value.remainingMillis

        savedStateHandle[KEY_PAUSED_ELAPSED_MILLIS] =
            _uiState.value.elapsedMillis

        _uiState.value = _uiState.value.copy(
            isPaused = true
        )

        persistState()
    }

    fun resume() {
        val state = _uiState.value

        if (!state.isRunning || !state.isPaused || state.isFinished) {
            return
        }

        val now = System.currentTimeMillis()

        val newPhaseStartTimestamp =
            if (config.modeType == TimerModeType.STOPWATCH) {
                now - state.elapsedMillis
            } else {
                val phaseDuration =
                    phaseDurationMillis(state.phase)

                now - (phaseDuration - state.remainingMillis)
            }

        savedStateHandle[KEY_PHASE_START_TIMESTAMP] =
            newPhaseStartTimestamp

        _uiState.value = state.copy(
            isPaused = false
        )

        persistState()
        startTicker()
    }

    fun skipBreak() {
        val state = _uiState.value

        if (
            state.phase != TimerPhase.BREAK ||
            state.isFinished
        ) {
            return
        }

        beginNextFocusCycle()
    }

    fun finish() {
        val state = _uiState.value

        if (state.isFinished) {
            return
        }

        updateVisibleTime()

        val extraFocusMillis =
            if (_uiState.value.phase == TimerPhase.FOCUS) {
                _uiState.value.elapsedMillis
            } else {
                0L
            }

        tickerJob?.cancel()
        tickerJob = null

        _uiState.value = _uiState.value.copy(
            completedFocusMillisTotal =
                _uiState.value.completedFocusMillisTotal +
                        extraFocusMillis,
            phase = TimerPhase.FINISHED,
            isRunning = false,
            isPaused = false,
            isFinished = true,
            remainingMillis = 0L,
            elapsedMillis = 0L
        )

        persistState()
    }

    fun cancel() {
        tickerJob?.cancel()
        tickerJob = null

        clearSavedTimerState()

        _uiState.value = _uiState.value.copy(
            phase = TimerPhase.IDLE,
            isRunning = false,
            isPaused = false,
            currentCycle = 1,
            completedPomodoros = 0,
            completedFocusMillisTotal = 0L,
            remainingMillis = initialRemainingMillis(),
            elapsedMillis = 0L,
            isFinished = false,
            isRecordSaved = false
        )
    }

    fun saveStudyRecord(
        correctCount: Int,
        wrongCount: Int,
        blankCount: Int,
        note: String?
    ) {
        val state = _uiState.value

        if (!state.isFinished || state.isRecordSaved) {
            return
        }

        val durationMinutes =
            max(1, state.finalFocusMinutes)

        val entryType =
            if (state.modeType.isPomodoroStyle) {
                StudyEntryTypeKeys.POMODORO
            } else {
                StudyEntryTypeKeys.TIMER
            }

        val normalizedNote =
            note?.trim()?.takeIf { it.isNotEmpty() }

        _uiState.value = state.copy(
            isRecordSaved = true
        )

        savedStateHandle[KEY_IS_RECORD_SAVED] = true

        viewModelScope.launch {
            studyRecordRepository.insertStudyRecord(
                StudyRecordEntity(
                    examId = examId,
                    subjectId = subjectId,
                    durationMinutes = durationMinutes,
                    correctCount = correctCount.coerceAtLeast(0),
                    wrongCount = wrongCount.coerceAtLeast(0),
                    blankCount = blankCount.coerceAtLeast(0),
                    recordDateMillis = System.currentTimeMillis(),
                    note = normalizedNote,
                    entryType = entryType,
                    createdAtMillis = System.currentTimeMillis()
                )
            )

            clearSavedTimerState()
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()

        tickerJob = viewModelScope.launch {
            while (
                _uiState.value.isRunning &&
                !_uiState.value.isPaused &&
                !_uiState.value.isFinished
            ) {
                updateVisibleTime()
                delay(TIMER_REFRESH_INTERVAL_MILLIS)
            }
        }
    }

    private fun updateVisibleTime() {
        val state = _uiState.value

        if (
            !state.isRunning ||
            state.isPaused ||
            state.isFinished
        ) {
            return
        }

        val phaseStartTimestamp =
            savedStateHandle.get<Long>(
                KEY_PHASE_START_TIMESTAMP
            ) ?: System.currentTimeMillis()

        val elapsedSincePhaseStart =
            (System.currentTimeMillis() - phaseStartTimestamp)
                .coerceAtLeast(0L)

        if (config.modeType == TimerModeType.STOPWATCH) {
            _uiState.value = state.copy(
                elapsedMillis = elapsedSincePhaseStart,
                remainingMillis = 0L
            )
            return
        }

        val phaseDuration =
            phaseDurationMillis(state.phase)

        val remaining =
            (phaseDuration - elapsedSincePhaseStart)
                .coerceAtLeast(0L)

        _uiState.value = state.copy(
            remainingMillis = remaining,
            elapsedMillis = elapsedSincePhaseStart
                .coerceAtMost(phaseDuration)
        )

        if (remaining <= 0L) {
            onCurrentPhaseCompleted()
        }
    }

    private fun onCurrentPhaseCompleted() {
        val state = _uiState.value

        when (state.phase) {
            TimerPhase.FOCUS -> {
                val newCompletedFocusMillis =
                    state.completedFocusMillisTotal +
                            focusDurationMillis

                val newCompletedPomodoros =
                    state.completedPomodoros +
                            if (config.modeType.isPomodoroStyle) 1 else 0

                _uiState.value = state.copy(
                    completedFocusMillisTotal =
                        newCompletedFocusMillis,
                    completedPomodoros =
                        newCompletedPomodoros
                )

                val isFinalCycle =
                    state.currentCycle >= state.totalCycles

                if (
                    !config.modeType.hasBreaks ||
                    isFinalCycle
                ) {
                    finishAfterCompletedPhase()
                } else {
                    beginBreak()
                }
            }

            TimerPhase.BREAK -> {
                beginNextFocusCycle()
            }

            else -> Unit
        }
    }

    private fun beginBreak() {
        savedStateHandle[KEY_PHASE_START_TIMESTAMP] =
            System.currentTimeMillis()

        _uiState.value = _uiState.value.copy(
            phase = TimerPhase.BREAK,
            isRunning = true,
            isPaused = false,
            remainingMillis = breakDurationMillis,
            elapsedMillis = 0L
        )

        persistState()
    }

    private fun beginNextFocusCycle() {
        savedStateHandle[KEY_PHASE_START_TIMESTAMP] =
            System.currentTimeMillis()

        _uiState.value = _uiState.value.copy(
            phase = TimerPhase.FOCUS,
            currentCycle = (
                    _uiState.value.currentCycle + 1
                    ).coerceAtMost(_uiState.value.totalCycles),
            isRunning = true,
            isPaused = false,
            remainingMillis = focusDurationMillis,
            elapsedMillis = 0L
        )

        persistState()
        startTicker()
    }

    private fun finishAfterCompletedPhase() {
        tickerJob?.cancel()
        tickerJob = null

        _uiState.value = _uiState.value.copy(
            phase = TimerPhase.FINISHED,
            isRunning = false,
            isPaused = false,
            isFinished = true,
            remainingMillis = 0L,
            elapsedMillis = 0L
        )

        persistState()
    }

    private fun initialRemainingMillis(): Long {
        return if (config.modeType == TimerModeType.STOPWATCH) {
            0L
        } else {
            focusDurationMillis
        }
    }

    private fun phaseDurationMillis(
        phase: TimerPhase
    ): Long {
        return when (phase) {
            TimerPhase.BREAK -> breakDurationMillis
            TimerPhase.FOCUS -> focusDurationMillis
            TimerPhase.IDLE -> focusDurationMillis
            TimerPhase.FINISHED -> 0L
        }
    }

    private fun persistState() {
        val state = _uiState.value

        savedStateHandle[KEY_PHASE] =
            state.phase.name

        savedStateHandle[KEY_IS_RUNNING] =
            state.isRunning

        savedStateHandle[KEY_IS_PAUSED] =
            state.isPaused

        savedStateHandle[KEY_CURRENT_CYCLE] =
            state.currentCycle

        savedStateHandle[KEY_COMPLETED_POMODOROS] =
            state.completedPomodoros

        savedStateHandle[KEY_COMPLETED_FOCUS_MILLIS] =
            state.completedFocusMillisTotal

        savedStateHandle[KEY_IS_FINISHED] =
            state.isFinished

        savedStateHandle[KEY_IS_RECORD_SAVED] =
            state.isRecordSaved
    }

    private fun clearSavedTimerState() {
        savedStateHandle.remove<String>(KEY_PHASE)
        savedStateHandle.remove<Boolean>(KEY_IS_RUNNING)
        savedStateHandle.remove<Boolean>(KEY_IS_PAUSED)
        savedStateHandle.remove<Int>(KEY_CURRENT_CYCLE)
        savedStateHandle.remove<Int>(KEY_COMPLETED_POMODOROS)
        savedStateHandle.remove<Long>(KEY_COMPLETED_FOCUS_MILLIS)
        savedStateHandle.remove<Long>(KEY_PHASE_START_TIMESTAMP)
        savedStateHandle.remove<Long>(KEY_PAUSED_REMAINING_MILLIS)
        savedStateHandle.remove<Long>(KEY_PAUSED_ELAPSED_MILLIS)
        savedStateHandle.remove<Boolean>(KEY_IS_FINISHED)
        savedStateHandle.remove<Boolean>(KEY_IS_RECORD_SAVED)
    }

    override fun onCleared() {
        tickerJob?.cancel()
        super.onCleared()
    }
}