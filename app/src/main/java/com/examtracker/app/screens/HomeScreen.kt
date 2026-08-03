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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.examtracker.app.ui.theme.ExamTrackerTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    exams: List<ExamEntity>,
    onCreateExamClick: () -> Unit,
    onExamClick: (ExamEntity) -> Unit,
    onDeleteExam: (ExamEntity) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.home_welcome_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCreateExamClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.home_create_exam_button))
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (exams.isEmpty()) {
                EmptyExamCard()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = exams, key = { it.id }) { exam ->
                        ExamCard(
                            exam = exam,
                            onCardClick = { onExamClick(exam) },
                            onDeleteClick = { onDeleteExam(exam) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamCard(
    exam: ExamEntity,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exam.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.home_delete_exam_button)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val dateText = exam.examDateMillis?.let { dateFormatter.format(Date(it)) }
                ?: stringResource(id = R.string.home_exam_no_date)
            Text(
                text = stringResource(id = R.string.home_exam_date_format, dateText),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(
                    id = R.string.home_exam_daily_goal_format,
                    exam.dailyQuestionGoal
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = ruleDisplayText(exam.netCalculationRule),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ruleDisplayText(ruleKey: String): String {
    val resId = when (ruleKey) {
        NetCalculationRuleKeys.FOUR_WRONG_ONE_CORRECT -> R.string.create_exam_net_rule_4_1
        NetCalculationRuleKeys.THREE_WRONG_ONE_CORRECT -> R.string.create_exam_net_rule_3_1
        else -> R.string.create_exam_net_rule_none
    }
    return stringResource(id = resId)
}

@Composable
private fun EmptyExamCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(id = R.string.home_empty_state_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.home_empty_state_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    ExamTrackerTheme {
        HomeScreen(exams = emptyList(), onCreateExamClick = {}, onExamClick = {}, onDeleteExam = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenWithExamsPreview() {
    ExamTrackerTheme {
        HomeScreen(
            exams = listOf(
                ExamEntity(
                    id = 1L,
                    name = "YKS 2027",
                    examDateMillis = System.currentTimeMillis(),
                    dailyQuestionGoal = 100,
                    netCalculationRule = NetCalculationRuleKeys.FOUR_WRONG_ONE_CORRECT,
                    createdAtMillis = System.currentTimeMillis()
                ),
                ExamEntity(
                    id = 2L,
                    name = "KPSS",
                    examDateMillis = null,
                    dailyQuestionGoal = 50,
                    netCalculationRule = NetCalculationRuleKeys.NO_EFFECT,
                    createdAtMillis = System.currentTimeMillis()
                )
            ),
            onCreateExamClick = {},
            onExamClick = {},
            onDeleteExam = {}
        )
    }
}