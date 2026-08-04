package com.examtracker.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.examtracker.app.R
import com.examtracker.app.viewmodel.TimerPhase
import com.examtracker.app.viewmodel.TimerViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onBackClick: () -> Unit,
    onSessionSaved: () -> Unit
) {
    val uiState by viewModel.uiState
        .collectAsStateWithLifecycle()

    var showCompletionDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        uiState.isFinished,
        uiState.isRecordSaved
    ) {
        if (
            uiState.isFinished &&
            !uiState.isRecordSaved
        ) {
            showCompletionDialog = true
        }
    }

    if (showCompletionDialog) {
        SessionCompletionDialog(
            focusMinutes = uiState.finalFocusMinutes,
            onDismiss = {
                showCompletionDialog = false
            },
            onSave = {
                    correct,
                    wrong,
                    blank,
                    note ->

                viewModel.saveStudyRecord(
                    correctCount = correct,
                    wrongCount = wrong,
                    blankCount = blank,
                    note = note
                )

                showCompletionDialog = false
                onSessionSaved()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.timer_screen_title
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.content_description_back
                            )
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isExamMissing) {
                Text(
                    text = stringResource(
                        id = R.string.exam_detail_exam_not_found
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )

                return@Column
            }

            Text(
                text = uiState.examName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = uiState.subjectName,
                style = MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            val phaseLabelResource = when (uiState.phase) {
                TimerPhase.FOCUS ->
                    R.string.timer_screen_phase_focus

                TimerPhase.BREAK ->
                    R.string.timer_screen_phase_break

                TimerPhase.FINISHED ->
                    R.string.timer_screen_phase_finished

                TimerPhase.IDLE ->
                    R.string.timer_screen_phase_idle
            }

            Text(
                text = stringResource(
                    id = phaseLabelResource
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            CircularTimerDisplay(
                progressFraction = uiState.progressFraction,
                isCountUp = uiState.modeType.isCountUp,
                timeMillis =
                    if (uiState.modeType.isCountUp) {
                        uiState.elapsedMillis
                    } else {
                        uiState.remainingMillis
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {
                TimerStatColumn(
                    label = stringResource(
                        id = R.string
                            .timer_screen_completed_pomodoros_label
                    ),
                    value =
                        uiState.completedPomodoros.toString()
                )

                TimerStatColumn(
                    label = stringResource(
                        id = R.string
                            .timer_screen_current_cycle_label
                    ),
                    value = stringResource(
                        id = R.string.timer_screen_cycle_format,
                        uiState.currentCycle,
                        uiState.totalCycles
                    )
                )

                TimerStatColumn(
                    label = stringResource(
                        id = R.string
                            .timer_screen_todays_focus_label
                    ),
                    value = stringResource(
                        id = R.string
                            .timer_screen_minutes_format,
                        uiState.todaysFocusMinutes
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TimerControls(
                phase = uiState.phase,
                isRunning = uiState.isRunning,
                isPaused = uiState.isPaused,
                isFinished = uiState.isFinished,
                onStart = {
                    viewModel.start()
                },
                onPause = {
                    viewModel.pause()
                },
                onResume = {
                    viewModel.resume()
                },
                onSkipBreak = {
                    viewModel.skipBreak()
                },
                onFinish = {
                    viewModel.finish()
                },
                onCancel = {
                    viewModel.cancel()
                    onBackClick()
                }
            )
        }
    }
}

@Composable
private fun CircularTimerDisplay(
    progressFraction: Float,
    isCountUp: Boolean,
    timeMillis: Long
) {
    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = {
                if (isCountUp) {
                    1f
                } else {
                    progressFraction
                }
            },
            modifier = Modifier.size(240.dp),
            strokeWidth = 12.dp,
            color = MaterialTheme.colorScheme.primary,
            trackColor =
                MaterialTheme.colorScheme.surfaceVariant
        )

        Text(
            text = formatMillisAsClock(timeMillis),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TimerStatColumn(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TimerControls(
    phase: TimerPhase,
    isRunning: Boolean,
    isPaused: Boolean,
    isFinished: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSkipBreak: () -> Unit,
    onFinish: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isFinished) {
            Text(
                text = stringResource(
                    id = R.string.timer_screen_phase_finished
                ),
                style = MaterialTheme.typography.titleMedium
            )

            return
        }

        if (!isRunning) {
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        id = R.string.timer_screen_start_button
                    )
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                if (isPaused) {
                    Button(
                        onClick = onResume,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string
                                    .timer_screen_resume_button
                            )
                        )
                    }
                } else {
                    Button(
                        onClick = onPause,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string
                                    .timer_screen_pause_button
                            )
                        )
                    }
                }

                OutlinedButton(
                    onClick = onFinish,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(
                            id = R.string
                                .timer_screen_finish_button
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (phase == TimerPhase.BREAK) {
                OutlinedButton(
                    onClick = onSkipBreak,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(
                            id = R.string
                                .timer_screen_skip_break_button
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(
                    id = R.string.timer_screen_cancel_button
                )
            )
        }
    }
}

private fun formatMillisAsClock(
    millis: Long
): String {
    val totalSeconds =
        millis.coerceAtLeast(0L) / 1_000L

    val hours = totalSeconds / 3_600L
    val minutes = (totalSeconds % 3_600L) / 60L
    val seconds = totalSeconds % 60L

    return if (hours > 0L) {
        String.format(
            Locale.getDefault(),
            "%d:%02d:%02d",
            hours,
            minutes,
            seconds
        )
    } else {
        String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
    }
}