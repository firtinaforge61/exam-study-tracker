package com.examtracker.app.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.examtracker.app.R
import com.examtracker.app.viewmodel.EditStudyRecordViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStudyRecordScreen(
    viewModel: EditStudyRecordViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val record by viewModel.record.collectAsStateWithLifecycle()

    var durationText by remember { mutableStateOf("") }
    var correctText by remember { mutableStateOf("") }
    var wrongText by remember { mutableStateOf("") }
    var blankText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var recordDateMillis by remember {
        mutableStateOf(System.currentTimeMillis())
    }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(record) {
        val currentRecord = record

        if (currentRecord != null && !initialized) {
            durationText =
                currentRecord.durationMinutes.toString()

            correctText =
                currentRecord.correctCount.toString()

            wrongText =
                currentRecord.wrongCount.toString()

            blankText =
                currentRecord.blankCount.toString()

            noteText =
                currentRecord.note.orEmpty()

            recordDateMillis =
                currentRecord.recordDateMillis

            initialized = true
        }
    }

    val dateFormatter = remember {
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )
    }

    fun openDatePicker() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = recordDateMillis
        }

        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate =
                    Calendar.getInstance().apply {
                        set(
                            year,
                            month,
                            day,
                            0,
                            0,
                            0
                        )
                        set(
                            Calendar.MILLISECOND,
                            0
                        )
                    }

                recordDateMillis =
                    selectedDate.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun parseNonNegativeInt(
        text: String
    ): Int {
        return text
            .toIntOrNull()
            ?.coerceAtLeast(0)
            ?: 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = R.string
                                .edit_study_record_title
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                stringResource(
                                    id = R.string
                                        .content_description_back
                                )
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (record == null && initialized) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(
                        id = R.string
                            .edit_study_record_not_found
                    ),
                    style =
                        MaterialTheme.typography.bodyLarge
                )
            }

            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            EditNumberField(
                value = durationText,
                onValueChange = {
                    durationText = it
                },
                label = stringResource(
                    id = R.string
                        .add_study_record_duration_label
                )
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            EditNumberField(
                value = correctText,
                onValueChange = {
                    correctText = it
                },
                label = stringResource(
                    id = R.string
                        .add_study_record_correct_label
                )
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            EditNumberField(
                value = wrongText,
                onValueChange = {
                    wrongText = it
                },
                label = stringResource(
                    id = R.string
                        .add_study_record_wrong_label
                )
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            EditNumberField(
                value = blankText,
                onValueChange = {
                    blankText = it
                },
                label = stringResource(
                    id = R.string
                        .add_study_record_blank_label
                )
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = noteText,
                onValueChange = {
                    noteText = it
                },
                label = {
                    Text(
                        text = stringResource(
                            id = R.string
                                .add_study_record_note_label
                        )
                    )
                },
                modifier =
                    Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openDatePicker()
                    }
            ) {
                OutlinedTextField(
                    value = dateFormatter.format(
                        Date(recordDateMillis)
                    ),
                    onValueChange = {},
                    enabled = false,
                    readOnly = true,
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string
                                    .add_study_record_date_label
                            )
                        )
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                )
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Button(
                onClick = {
                    viewModel.updateRecord(
                        durationMinutes =
                            parseNonNegativeInt(
                                durationText
                            ),
                        correctCount =
                            parseNonNegativeInt(
                                correctText
                            ),
                        wrongCount =
                            parseNonNegativeInt(
                                wrongText
                            ),
                        blankCount =
                            parseNonNegativeInt(
                                blankText
                            ),
                        recordDateMillis =
                            recordDateMillis,
                        note = noteText,
                        onComplete = onSaved
                    )
                },
                enabled = record != null,
                modifier =
                    Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        id = R.string.action_save
                    )
                )
            }
        }
    }
}

@Composable
private fun EditNumberField(
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