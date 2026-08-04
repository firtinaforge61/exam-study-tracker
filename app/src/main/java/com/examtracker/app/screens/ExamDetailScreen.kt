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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.examtracker.app.R
import com.examtracker.app.data.local.ExamEntity
import com.examtracker.app.data.local.NetCalculationRuleKeys
import com.examtracker.app.data.local.StudyEntryTypeKeys
import com.examtracker.app.data.local.StudyRecordEntity
import com.examtracker.app.ui.theme.ExamTrackerTheme
import com.examtracker.app.viewmodel.ExamDetailEvent
import com.examtracker.app.viewmodel.ExamDetailViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.examtracker.app.data.local.SubjectEntity
import com.examtracker.app.settings.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailScreen(
    viewModel: ExamDetailViewModel,
    onBackClick: () -> Unit,
    onAddStudyRecordClick: () -> Unit,
    onStartStudySessionClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSubjectClick: (SubjectEntity) -> Unit
) {
    val exam by viewModel.exam.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val recentStudyRecords by viewModel.recentStudyRecords.collectAsStateWithLifecycle()
    val totalStudyMinutes by viewModel.totalStudyMinutes.collectAsStateWithLifecycle()
    val totalSolvedQuestions by viewModel.totalSolvedQuestions.collectAsStateWithLifecycle()
    val totalNet by viewModel.totalNet.collectAsStateWithLifecycle()

    var showAddSubjectDialog by remember {
        mutableStateOf(false)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val coroutineScope = rememberCoroutineScope()

    val subjectHasRecordsMessage = stringResource(
        id = R.string.exam_detail_subject_has_records_error
    )

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ExamDetailEvent.SubjectHasRecordsCannotDelete -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            subjectHasRecordsMessage
                        )
                    }
                }
            }
        }
    }

    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = {
                showAddSubjectDialog = false
            },
            onConfirm = { name ->
                viewModel.addSubject(name)
                showAddSubjectDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = exam?.name
                            ?: stringResource(
                                id = R.string.exam_detail_title
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
                            contentDescription = stringResource(
                                id = R.string.content_description_back
                            )
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        val currentExam = exam

        if (currentExam == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.exam_detail_exam_not_found
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {
            item(key = "summary") {
                ExamSummaryCard(
                    exam = currentExam,
                    totalStudyMinutes =
                        totalStudyMinutes,
                    totalSolvedQuestions =
                        totalSolvedQuestions,
                    totalNet = totalNet
                )
            }

            item(key = "session_actions") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick =
                            onStartStudySessionClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string
                                    .exam_detail_start_study_session_button
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = onHistoryClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string
                                    .exam_detail_history_button
                            )
                        )
                    }
                }
            }

            item(key = "subjects_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            id = R.string
                                .exam_detail_subjects_title
                        ),
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    IconButton(
                        onClick = {
                            showAddSubjectDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription =
                                stringResource(
                                    id = R.string
                                        .exam_detail_add_subject_button
                                )
                        )
                    }
                }
            }

            item(key = "subjects_content") {
                if (subjects.isEmpty()) {
                    Text(
                        text = stringResource(
                            id = R.string
                                .exam_detail_no_subjects
                        ),
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                } else {
                    LazyRow(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = subjects,
                            key = { subject ->
                                subject.id
                            }
                        ) { subject ->
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = subject.name
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector =
                                            Icons.Filled.Close,
                                        contentDescription =
                                            stringResource(
                                                id = R.string
                                                    .exam_detail_delete_subject_button
                                            ),
                                        modifier =
                                            Modifier.clickable {
                                                viewModel
                                                    .deleteSubject(
                                                        subject
                                                    )
                                            }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item(key = "study_records_header") {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(
                            id = R.string
                                .exam_detail_study_records_title
                        ),
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Button(
                        onClick =
                            onAddStudyRecordClick,
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string
                                    .exam_detail_add_study_record_button
                            )
                        )
                    }
                }
            }

            if (recentStudyRecords.isEmpty()) {
                item(key = "no_records") {
                    Text(
                        text = stringResource(
                            id = R.string
                                .exam_detail_no_study_records
                        ),
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }
            } else {
                items(
                    items = recentStudyRecords,
                    key = { record ->
                        record.id
                    }
                ) { record ->
                    val subjectName =
                        subjects.firstOrNull {
                            it.id == record.subjectId
                        }?.name
                            ?: stringResource(
                                id = R.string
                                    .exam_detail_unknown_subject
                            )

                    StudyRecordCard(
                        record = record,
                        subjectName = subjectName,
                        netCalculationRule =
                            currentExam.netCalculationRule,
                        onDeleteClick = {
                            viewModel.deleteStudyRecord(
                                record
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExamSummaryCard(
    exam: ExamEntity,
    totalStudyMinutes: Int,
    totalSolvedQuestions: Int,
    totalNet: Double
) {
    val dateFormatter = remember {
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            val dateText =
                exam.examDateMillis?.let { millis ->
                    dateFormatter.format(
                        Date(millis)
                    )
                } ?: stringResource(
                    id = R.string.home_exam_no_date
                )

            Text(
                text = stringResource(
                    id = R.string.exam_detail_date_format,
                    dateText
                ),
                style =
                    MaterialTheme.typography.bodyMedium
            )

            exam.examDateMillis?.let { dateMillis ->
                val daysRemaining =
                    calculateDaysRemaining(dateMillis)

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = stringResource(
                        id = R.string
                            .exam_detail_days_remaining_format,
                        daysRemaining
                    ),
                    style =
                        MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = stringResource(
                    id = R.string
                        .exam_detail_daily_goal_format,
                    exam.dailyQuestionGoal
                ),
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = stringResource(
                    id = R.string
                        .exam_detail_total_study_time_format,
                    totalStudyMinutes
                ),
                style =
                    MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = stringResource(
                    id = R.string
                        .exam_detail_total_solved_format,
                    totalSolvedQuestions
                ),
                style =
                    MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = stringResource(
                    id = R.string
                        .exam_detail_total_net_format,
                    totalNet
                ),
                style =
                    MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StudyRecordCard(
    record: StudyRecordEntity,
    subjectName: String,
    netCalculationRule: String,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = remember {
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )
    }

    val net = remember(
        record.id,
        record.correctCount,
        record.wrongCount,
        netCalculationRule
    ) {
        NetCalculationRuleKeys.calculateNet(
            rule = netCalculationRule,
            correctCount = record.correctCount,
            wrongCount = record.wrongCount
        )
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = subjectName,
                    style =
                        MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        imageVector =
                            Icons.Filled.Delete,
                        contentDescription =
                            stringResource(
                                id = R.string
                                    .exam_detail_delete_study_record_button
                            )
                    )
                }
            }

            Text(
                text = stringResource(
                    id = R.string
                        .exam_detail_record_duration_format,
                    record.durationMinutes
                ),
                style =
                    MaterialTheme.typography.bodySmall
            )

            Text(
                text = stringResource(
                    id = R.string
                        .exam_detail_record_correct_wrong_blank_format,
                    record.correctCount,
                    record.wrongCount,
                    record.blankCount
                ),
                style =
                    MaterialTheme.typography.bodySmall
            )

            Text(
                text = stringResource(
                    id = R.string
                        .exam_detail_record_net_format,
                    net
                ),
                style =
                    MaterialTheme.typography.bodySmall
            )

            Text(
                text = stringResource(
                    id = R.string
                        .exam_detail_record_date_format,
                    dateFormatter.format(
                        Date(record.recordDateMillis)
                    )
                ),
                style =
                    MaterialTheme.typography.bodySmall
            )

            record.note?.let { note ->
                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = note,
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}

private fun calculateDaysRemaining(
    examDateMillis: Long
): Long {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val examDay = Calendar.getInstance().apply {
        timeInMillis = examDateMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return (
            examDay.timeInMillis -
                    today.timeInMillis
            ) / (
            24L * 60L * 60L * 1_000L
            )
}

@Preview(showBackground = true)
@Composable
private fun ExamSummaryCardPreview() {
    ExamTrackerTheme (
        appTheme = AppTheme.LIGHT
    ) {
        ExamSummaryCard(
            exam = ExamEntity(
                id = 1L,
                name = "KPSS 2026",
                examDateMillis =
                    System.currentTimeMillis(),
                dailyQuestionGoal = 100,
                netCalculationRule =
                    NetCalculationRuleKeys
                        .FOUR_WRONG_ONE_CORRECT,
                createdAtMillis =
                    System.currentTimeMillis()
            ),
            totalStudyMinutes = 320,
            totalSolvedQuestions = 480,
            totalNet = 410.25
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StudyRecordCardPreview() {
    ExamTrackerTheme (
        appTheme = AppTheme.LIGHT
    ) {
        StudyRecordCard(
            record = StudyRecordEntity(
                id = 1L,
                examId = 1L,
                subjectId = 1L,
                durationMinutes = 45,
                correctCount = 30,
                wrongCount = 8,
                blankCount = 2,
                recordDateMillis =
                    System.currentTimeMillis(),
                note = "Geometri çalışması",
                entryType =
                    StudyEntryTypeKeys.MANUAL,
                createdAtMillis =
                    System.currentTimeMillis()
            ),
            subjectName = "Matematik",
            netCalculationRule =
                NetCalculationRuleKeys
                    .FOUR_WRONG_ONE_CORRECT,
            onDeleteClick = {}
        )
    }
}