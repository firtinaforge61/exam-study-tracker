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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.examtracker.app.R
import com.examtracker.app.data.local.ExamEntity
import com.examtracker.app.data.local.NetCalculationRuleKeys
import com.examtracker.app.data.local.StudyEntryTypeKeys
import com.examtracker.app.data.local.StudyRecordEntity
import com.examtracker.app.ui.theme.ExamTrackerTheme
import com.examtracker.app.viewmodel.QuickResumeInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.Settings
import com.examtracker.app.settings.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    exams: List<ExamEntity>,
    todaysStudyMinutes: Int,
    todaysGoalPercentage: Float,
    currentStreak: Int,
    weeklyMinutes: Int,
    monthlyMinutes: Int,
    quickResume: QuickResumeInfo?,
    recentStudyRecords: List<StudyRecordEntity>,
    upcomingExams: List<ExamEntity>,
    recentExams: List<ExamEntity>,
    onCreateExamClick: () -> Unit,
    onExamClick: (ExamEntity) -> Unit,
    onDeleteExam: (ExamEntity) -> Unit,
    onQuickResumeClick: (Long) -> Unit,
    onSettingsClick: () -> Unit

) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.app_name
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = onSettingsClick
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(
                                id = R.string.settings_title
                            )
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
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
            item(key = "welcome") {
                Column {
                    Text(
                        text = stringResource(
                            id = R.string.home_welcome_title
                        ),
                        style =
                            MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = stringResource(
                            id = R.string.home_subtitle
                        ),
                        style =
                            MaterialTheme.typography.bodyLarge,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }
            }

            item(key = "create_exam") {
                Button(
                    onClick = onCreateExamClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(
                            id = R.string
                                .home_create_exam_button
                        )
                    )
                }
            }

            item(key = "today_and_streak") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    TodayStudyCard(
                        todaysStudyMinutes =
                            todaysStudyMinutes,
                        todaysGoalPercentage =
                            todaysGoalPercentage,
                        modifier = Modifier.weight(1f)
                    )

                    StreakCard(
                        currentStreak = currentStreak,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item(key = "weekly_and_monthly") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    PeriodStatCard(
                        title = stringResource(
                            id = R.string
                                .dashboard_weekly_minutes_title
                        ),
                        value = stringResource(
                            id = R.string
                                .dashboard_weekly_minutes_format,
                            weeklyMinutes
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    PeriodStatCard(
                        title = stringResource(
                            id = R.string
                                .dashboard_monthly_minutes_title
                        ),
                        value = stringResource(
                            id = R.string
                                .dashboard_monthly_minutes_format,
                            monthlyMinutes
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item(key = "quick_resume") {
                QuickResumeCard(
                    quickResume = quickResume,
                    onResumeClick = {
                        onQuickResumeClick(it.examId)
                    }
                )
            }

            if (upcomingExams.isNotEmpty()) {
                item(key = "upcoming_header") {
                    SectionTitle(
                        text = stringResource(
                            id = R.string
                                .dashboard_upcoming_exams_title
                        )
                    )
                }

                item(key = "upcoming_exams") {
                    LazyRow(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = upcomingExams,
                            key = { exam ->
                                "upcoming_${exam.id}"
                            }
                        ) { exam ->
                            SmallExamCard(
                                exam = exam,
                                onClick = {
                                    onExamClick(exam)
                                }
                            )
                        }
                    }
                }
            }

            if (recentExams.isNotEmpty()) {
                item(key = "recent_exams_header") {
                    SectionTitle(
                        text = stringResource(
                            id = R.string
                                .dashboard_recent_exams_title
                        )
                    )
                }

                item(key = "recent_exams") {
                    LazyRow(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = recentExams,
                            key = { exam ->
                                "recent_${exam.id}"
                            }
                        ) { exam ->
                            SmallExamCard(
                                exam = exam,
                                onClick = {
                                    onExamClick(exam)
                                }
                            )
                        }
                    }
                }
            }

            item(key = "recent_records_header") {
                SectionTitle(
                    text = stringResource(
                        id = R.string
                            .dashboard_recent_records_title
                    )
                )
            }

            if (recentStudyRecords.isEmpty()) {
                item(key = "no_recent_records") {
                    Text(
                        text = stringResource(
                            id = R.string
                                .dashboard_no_recent_records
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
                        "record_${record.id}"
                    }
                ) { record ->
                    val examName =
                        exams.firstOrNull {
                            it.id == record.examId
                        }?.name
                            ?: stringResource(
                                id = R.string
                                    .exam_detail_unknown_subject
                            )

                    RecentRecordCard(
                        record = record,
                        examName = examName
                    )
                }
            }

            item(key = "all_exams_header") {
                SectionTitle(
                    text = stringResource(
                        id = R.string
                            .dashboard_all_exams_title
                    )
                )
            }

            if (exams.isEmpty()) {
                item(key = "empty_exam_state") {
                    EmptyExamCard()
                }
            } else {
                items(
                    items = exams,
                    key = { exam ->
                        "exam_${exam.id}"
                    }
                ) { exam ->
                    ExamCard(
                        exam = exam,
                        onCardClick = {
                            onExamClick(exam)
                        },
                        onDeleteClick = {
                            onDeleteExam(exam)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayStudyCard(
    todaysStudyMinutes: Int,
    todaysGoalPercentage: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(
                    id = R.string
                        .dashboard_today_study_title
                ),
                style =
                    MaterialTheme.typography.labelMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = stringResource(
                    id = R.string
                        .dashboard_today_study_minutes_format,
                    todaysStudyMinutes
                ),
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            LinearProgressIndicator(
                progress = {
                    (
                            todaysGoalPercentage / 100f
                            ).coerceIn(0f, 1f)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = stringResource(
                    id = R.string
                        .dashboard_today_goal_percentage_format,
                    todaysGoalPercentage
                ),
                style =
                    MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun StreakCard(
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(
                    id = R.string
                        .dashboard_current_streak_title
                ),
                style =
                    MaterialTheme.typography.labelMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = stringResource(
                    id = R.string
                        .dashboard_current_streak_days_format,
                    currentStreak
                ),
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PeriodStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography.labelMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = value,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuickResumeCard(
    quickResume: QuickResumeInfo?,
    onResumeClick: (QuickResumeInfo) -> Unit
) {
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
            Text(
                text = stringResource(
                    id = R.string
                        .dashboard_quick_resume_title
                ),
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            if (quickResume == null) {
                Text(
                    text = stringResource(
                        id = R.string
                            .dashboard_no_recent_session
                    ),
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            } else {
                Text(
                    text = stringResource(
                        id = R.string
                            .dashboard_quick_resume_subject_format,
                        quickResume.examName,
                        quickResume.subjectName
                    ),
                    style =
                        MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedButton(
                    onClick = {
                        onResumeClick(quickResume)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(
                            id = R.string
                                .dashboard_continue_last_session_button
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallExamCard(
    exam: ExamEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(
            onClick = onClick
        ),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = exam.name,
                style =
                    MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RecentRecordCard(
    record: StudyRecordEntity,
    examName: String
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
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = examName,
                style =
                    MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = stringResource(
                    id = R.string
                        .exam_detail_record_duration_format,
                    record.durationMinutes
                ),
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
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
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExamCard(
    exam: ExamEntity,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = remember {
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
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
                    text = exam.name,
                    style =
                        MaterialTheme.typography.titleMedium,
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
                                    .home_delete_exam_button
                            )
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            val dateText =
                exam.examDateMillis?.let { dateMillis ->
                    dateFormatter.format(
                        Date(dateMillis)
                    )
                } ?: stringResource(
                    id = R.string.home_exam_no_date
                )

            Text(
                text = stringResource(
                    id = R.string.home_exam_date_format,
                    dateText
                ),
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Text(
                text = stringResource(
                    id = R.string
                        .home_exam_daily_goal_format,
                    exam.dailyQuestionGoal
                ),
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Text(
                text = ruleDisplayText(
                    exam.netCalculationRule
                ),
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ruleDisplayText(
    ruleKey: String
): String {
    val resourceId = when (ruleKey) {
        NetCalculationRuleKeys
            .FOUR_WRONG_ONE_CORRECT ->
            R.string.create_exam_net_rule_4_1

        NetCalculationRuleKeys
            .THREE_WRONG_ONE_CORRECT ->
            R.string.create_exam_net_rule_3_1

        else ->
            R.string.create_exam_net_rule_none
    }

    return stringResource(
        id = resourceId
    )
}

@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        style =
            MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun EmptyExamCard() {
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
            Text(
                text = stringResource(
                    id = R.string
                        .home_empty_state_title
                ),
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = stringResource(
                    id = R.string
                        .home_empty_state_description
                ),
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    ExamTrackerTheme (
        appTheme = AppTheme.LIGHT
    ) {
        HomeScreen(
            exams = emptyList(),
            todaysStudyMinutes = 0,
            todaysGoalPercentage = 0f,
            currentStreak = 0,
            weeklyMinutes = 0,
            monthlyMinutes = 0,
            quickResume = null,
            recentStudyRecords = emptyList(),
            upcomingExams = emptyList(),
            recentExams = emptyList(),
            onCreateExamClick = {},
            onExamClick = {},
            onDeleteExam = {},
            onQuickResumeClick = {},
            onSettingsClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenWithDataPreview() {
    ExamTrackerTheme (
        appTheme = AppTheme.LIGHT
    ) {
        val sampleExam = ExamEntity(
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
        )

        HomeScreen(
            exams = listOf(sampleExam),
            todaysStudyMinutes = 90,
            todaysGoalPercentage = 65f,
            currentStreak = 4,
            weeklyMinutes = 420,
            monthlyMinutes = 1_800,
            quickResume = QuickResumeInfo(
                examId = 1L,
                examName = "KPSS 2026",
                subjectName = "Matematik",
                lastStudiedMillis =
                    System.currentTimeMillis()
            ),
            recentStudyRecords = listOf(
                StudyRecordEntity(
                    id = 1L,
                    examId = 1L,
                    subjectId = 1L,
                    durationMinutes = 45,
                    correctCount = 30,
                    wrongCount = 8,
                    blankCount = 2,
                    recordDateMillis =
                        System.currentTimeMillis(),
                    note = null,
                    entryType =
                        StudyEntryTypeKeys.POMODORO,
                    createdAtMillis =
                        System.currentTimeMillis()
                )
            ),
            upcomingExams = listOf(sampleExam),
            recentExams = listOf(sampleExam),
            onCreateExamClick = {},
            onExamClick = {},
            onDeleteExam = {},
            onQuickResumeClick = {},
            onSettingsClick = {}

        )
    }
}