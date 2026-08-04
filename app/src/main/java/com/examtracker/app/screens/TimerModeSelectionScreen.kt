package com.examtracker.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.examtracker.app.R
import com.examtracker.app.data.local.SubjectEntity
import com.examtracker.app.viewmodel.ExamDetailViewModel
import com.examtracker.app.viewmodel.TimerConfig
import com.examtracker.app.viewmodel.TimerModeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerModeSelectionScreen(
    viewModel: ExamDetailViewModel,
    onBackClick: () -> Unit,
    onStartSession: (
        subjectId: Long,
        config: TimerConfig
    ) -> Unit
) {
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    var selectedSubject by remember {
        mutableStateOf<SubjectEntity?>(null)
    }

    var selectedModeType by remember {
        mutableStateOf(TimerModeType.POMODORO_25_5)
    }

    var customFocusText by remember {
        mutableStateOf("25")
    }

    var customBreakText by remember {
        mutableStateOf("5")
    }

    var customCyclesText by remember {
        mutableStateOf("4")
    }

    var countdownMinutesText by remember {
        mutableStateOf("30")
    }

    fun parsePositiveInt(
        text: String,
        fallback: Int
    ): Int {
        val value = text.toIntOrNull() ?: return fallback
        return value.takeIf { it > 0 } ?: fallback
    }

    val canStart =
        subjects.isNotEmpty() && selectedSubject != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.timer_mode_selection_title
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(
                    id = R.string.timer_mode_selection_subject_label
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (subjects.isEmpty()) {
                Text(
                    text = stringResource(
                        id = R.string
                            .timer_mode_selection_no_subjects_message
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(
                        (subjects.size.coerceAtMost(4) * 56).dp
                    ),
                    verticalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = subjects,
                        key = { subject -> subject.id }
                    ) { subject ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSubject = subject
                                },
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected =
                                    selectedSubject?.id == subject.id,
                                onClick = {
                                    selectedSubject = subject
                                }
                            )

                            Text(
                                text = subject.name,
                                modifier = Modifier.padding(
                                    start = 8.dp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(
                    id = R.string.timer_mode_selection_mode_label
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            TimerModeOption(
                label = stringResource(
                    id = R.string.timer_mode_pomodoro_25_5
                ),
                selected =
                    selectedModeType ==
                            TimerModeType.POMODORO_25_5,
                onSelect = {
                    selectedModeType =
                        TimerModeType.POMODORO_25_5
                }
            )

            TimerModeOption(
                label = stringResource(
                    id = R.string.timer_mode_pomodoro_50_10
                ),
                selected =
                    selectedModeType ==
                            TimerModeType.POMODORO_50_10,
                onSelect = {
                    selectedModeType =
                        TimerModeType.POMODORO_50_10
                }
            )

            TimerModeOption(
                label = stringResource(
                    id = R.string.timer_mode_custom_pomodoro
                ),
                selected =
                    selectedModeType ==
                            TimerModeType.CUSTOM_POMODORO,
                onSelect = {
                    selectedModeType =
                        TimerModeType.CUSTOM_POMODORO
                }
            )

            if (
                selectedModeType ==
                TimerModeType.CUSTOM_POMODORO
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        NumberTextField(
                            value = customFocusText,
                            onValueChange = {
                                customFocusText = it
                            },
                            label = stringResource(
                                id = R.string
                                    .timer_mode_custom_focus_label
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        NumberTextField(
                            value = customBreakText,
                            onValueChange = {
                                customBreakText = it
                            },
                            label = stringResource(
                                id = R.string
                                    .timer_mode_custom_break_label
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        NumberTextField(
                            value = customCyclesText,
                            onValueChange = {
                                customCyclesText = it
                            },
                            label = stringResource(
                                id = R.string
                                    .timer_mode_custom_cycles_label
                            )
                        )
                    }
                }
            }

            TimerModeOption(
                label = stringResource(
                    id = R.string.timer_mode_stopwatch
                ),
                selected =
                    selectedModeType ==
                            TimerModeType.STOPWATCH,
                onSelect = {
                    selectedModeType =
                        TimerModeType.STOPWATCH
                }
            )

            TimerModeOption(
                label = stringResource(
                    id = R.string.timer_mode_countdown
                ),
                selected =
                    selectedModeType ==
                            TimerModeType.COUNTDOWN,
                onSelect = {
                    selectedModeType =
                        TimerModeType.COUNTDOWN
                }
            )

            if (
                selectedModeType ==
                TimerModeType.COUNTDOWN
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                NumberTextField(
                    value = countdownMinutesText,
                    onValueChange = {
                        countdownMinutesText = it
                    },
                    label = stringResource(
                        id = R.string
                            .timer_mode_countdown_minutes_label
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val subjectId =
                        selectedSubject?.id ?: return@Button

                    val config = when (selectedModeType) {
                        TimerModeType.POMODORO_25_5 ->
                            TimerConfig.pomodoro25x5()

                        TimerModeType.POMODORO_50_10 ->
                            TimerConfig.pomodoro50x10()

                        TimerModeType.CUSTOM_POMODORO ->
                            TimerConfig.customPomodoro(
                                focusMinutes = parsePositiveInt(
                                    customFocusText,
                                    25
                                ),
                                breakMinutes = parsePositiveInt(
                                    customBreakText,
                                    5
                                ),
                                totalCycles = parsePositiveInt(
                                    customCyclesText,
                                    4
                                )
                            )

                        TimerModeType.STOPWATCH ->
                            TimerConfig.stopwatch()

                        TimerModeType.COUNTDOWN ->
                            TimerConfig.countdown(
                                focusMinutes = parsePositiveInt(
                                    countdownMinutesText,
                                    30
                                )
                            )
                    }

                    onStartSession(
                        subjectId,
                        config
                    )
                },
                enabled = canStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        id = R.string
                            .timer_mode_selection_start_button
                    )
                )
            }
        }
    }
}

@Composable
private fun TimerModeOption(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect
        )

        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun NumberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (
                newValue.isEmpty() ||
                newValue.all { character ->
                    character.isDigit()
                }
            ) {
                onValueChange(newValue)
            }
        },
        label = {
            Text(text = label)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier.fillMaxWidth()
    )
}