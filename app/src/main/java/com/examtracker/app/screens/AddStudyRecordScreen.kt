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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.examtracker.app.data.local.SubjectEntity
import com.examtracker.app.viewmodel.ExamDetailViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudyRecordScreen(
    viewModel: ExamDetailViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    var expanded by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<SubjectEntity?>(null) }

    var durationText by remember { mutableStateOf("0") }
    var correctText by remember { mutableStateOf("0") }
    var wrongText by remember { mutableStateOf("0") }
    var blankText by remember { mutableStateOf("0") }
    var noteText by remember { mutableStateOf("") }

    var recordDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    fun openDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = recordDateMillis

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, pickedYear, pickedMonth, pickedDay ->
                val pickedCalendar = Calendar.getInstance()
                pickedCalendar.set(pickedYear, pickedMonth, pickedDay, 0, 0, 0)
                recordDateMillis = pickedCalendar.timeInMillis
            },
            year,
            month,
            day
        ).show()
    }

    fun parseNonNegativeInt(text: String): Int {
        val value = text.toIntOrNull() ?: 0
        return if (value < 0) 0 else value
    }

    val canSave = subjects.isNotEmpty() && selectedSubject != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.add_study_record_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.content_description_back)
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (subjects.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.add_study_record_no_subjects_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { if (subjects.isNotEmpty()) expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedSubject?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.add_study_record_subject_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    subjects.forEach { subject ->
                        DropdownMenuItem(
                            text = { Text(text = subject.name) },
                            onClick = {
                                selectedSubject = subject
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = durationText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        durationText = newValue
                    }
                },
                label = { Text(text = stringResource(id = R.string.add_study_record_duration_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correctText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        correctText = newValue
                    }
                },
                label = { Text(text = stringResource(id = R.string.add_study_record_correct_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = wrongText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        wrongText = newValue
                    }
                },
                label = { Text(text = stringResource(id = R.string.add_study_record_wrong_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = blankText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        blankText = newValue
                    }
                },
                label = { Text(text = stringResource(id = R.string.add_study_record_blank_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text(text = stringResource(id = R.string.add_study_record_note_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openDatePicker() }
            ) {
                OutlinedTextField(
                    value = dateFormatter.format(Date(recordDateMillis)),
                    onValueChange = {},
                    enabled = false,
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.add_study_record_date_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val subjectId = selectedSubject?.id
                    if (subjectId != null) {
                        viewModel.addManualStudyRecord(
                            subjectId = subjectId,
                            durationMinutes = parseNonNegativeInt(durationText),
                            correctCount = parseNonNegativeInt(correctText),
                            wrongCount = parseNonNegativeInt(wrongText),
                            blankCount = parseNonNegativeInt(blankText),
                            recordDateMillis = recordDateMillis,
                            note = noteText
                        )
                        onSaved()
                    }
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.action_save))
            }
        }
    }
}