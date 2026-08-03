package com.examtracker.app.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.examtracker.app.R
import com.examtracker.app.data.local.NetCalculationRuleKeys
import com.examtracker.app.ui.theme.ExamTrackerTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private enum class NetCalculationRuleOption(val key: String) {
    FOUR_WRONG_ONE_CORRECT(NetCalculationRuleKeys.FOUR_WRONG_ONE_CORRECT),
    THREE_WRONG_ONE_CORRECT(NetCalculationRuleKeys.THREE_WRONG_ONE_CORRECT),
    NO_EFFECT(NetCalculationRuleKeys.NO_EFFECT)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExamScreen(
    onBackClick: () -> Unit,
    onCreateExam: (
        examName: String,
        examDateMillis: Long?,
        dailyQuestionGoal: Int,
        netCalculationRule: String
    ) -> Unit
) {
    val context = LocalContext.current

    var examName by remember { mutableStateOf("") }
    var examNameError by remember { mutableStateOf(false) }

    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    var dailyGoalText by remember { mutableStateOf("") }
    var dailyGoalError by remember { mutableStateOf(false) }

    var selectedRule by remember { mutableStateOf(NetCalculationRuleOption.FOUR_WRONG_ONE_CORRECT) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    fun openDatePicker() {
        val calendar = Calendar.getInstance()
        selectedDateMillis?.let { calendar.timeInMillis = it }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, pickedYear, pickedMonth, pickedDay ->
                val pickedCalendar = Calendar.getInstance()
                pickedCalendar.set(pickedYear, pickedMonth, pickedDay, 0, 0, 0)
                selectedDateMillis = pickedCalendar.timeInMillis
            },
            year,
            month,
            day
        ).show()
    }

    fun validateAndCreate() {
        val isNameValid = examName.isNotBlank()
        examNameError = !isNameValid

        val goalValue = dailyGoalText.toIntOrNull()
        val isGoalValid = goalValue != null && goalValue >= 0
        dailyGoalError = !isGoalValid

        if (isNameValid && isGoalValid) {
            onCreateExam(
                examName.trim(),
                selectedDateMillis,
                goalValue ?: 0,
                selectedRule.key
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.create_exam_title)) },
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
            OutlinedTextField(
                value = examName,
                onValueChange = {
                    examName = it
                    if (examNameError && it.isNotBlank()) examNameError = false
                },
                label = { Text(text = stringResource(id = R.string.create_exam_name_label)) },
                isError = examNameError,
                supportingText = {
                    if (examNameError) {
                        Text(text = stringResource(id = R.string.error_exam_name_blank))
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openDatePicker() }
            ) {
                OutlinedTextField(
                    value = selectedDateMillis?.let { dateFormatter.format(Date(it)) } ?: "",
                    onValueChange = {},
                    enabled = false,
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.create_exam_date_label)) },
                    placeholder = { Text(text = stringResource(id = R.string.create_exam_date_placeholder)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = stringResource(id = R.string.create_exam_date_label)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dailyGoalText,
                onValueChange = {
                    dailyGoalText = it
                    if (dailyGoalError) dailyGoalError = false
                },
                label = { Text(text = stringResource(id = R.string.create_exam_daily_goal_label)) },
                isError = dailyGoalError,
                supportingText = {
                    if (dailyGoalError) {
                        Text(text = stringResource(id = R.string.error_daily_goal_negative))
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.create_exam_net_rule_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            NetRuleOption(
                label = stringResource(id = R.string.create_exam_net_rule_4_1),
                selected = selectedRule == NetCalculationRuleOption.FOUR_WRONG_ONE_CORRECT,
                onSelect = { selectedRule = NetCalculationRuleOption.FOUR_WRONG_ONE_CORRECT }
            )
            NetRuleOption(
                label = stringResource(id = R.string.create_exam_net_rule_3_1),
                selected = selectedRule == NetCalculationRuleOption.THREE_WRONG_ONE_CORRECT,
                onSelect = { selectedRule = NetCalculationRuleOption.THREE_WRONG_ONE_CORRECT }
            )
            NetRuleOption(
                label = stringResource(id = R.string.create_exam_net_rule_none),
                selected = selectedRule == NetCalculationRuleOption.NO_EFFECT,
                onSelect = { selectedRule = NetCalculationRuleOption.NO_EFFECT }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { validateAndCreate() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.create_exam_create_button))
            }
        }
    }
}

@Composable
private fun NetRuleOption(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateExamScreenPreview() {
    ExamTrackerTheme {
        CreateExamScreen(onBackClick = {}, onCreateExam = { _, _, _, _ -> })
    }
}