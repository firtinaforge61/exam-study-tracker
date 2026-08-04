package com.examtracker.app.screens

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.examtracker.app.R
import com.examtracker.app.data.local.NetCalculationRuleKeys
import com.examtracker.app.data.local.StudyRecordEntity
import com.examtracker.app.viewmodel.HistoryDateFilter
import com.examtracker.app.viewmodel.SessionHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionHistoryScreen(
    viewModel: SessionHistoryViewModel,
    onBackClick: () -> Unit,
    onEditRecordClick: (Long) -> Unit
) {
    val filterState by viewModel
        .filterState
        .collectAsStateWithLifecycle()

    val subjects by viewModel
        .subjectsInScope
        .collectAsStateWithLifecycle()

    val filteredRecords by viewModel
        .filteredRecords
        .collectAsStateWithLifecycle()

    val netRuleByExamId by viewModel
        .netRuleByExamId
        .collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.session_history_title
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
        ) {
            OutlinedTextField(
                value = filterState.searchQuery,
                onValueChange = {
                    viewModel.setSearchQuery(it)
                },
                label = {
                    Text(
                        text = stringResource(
                            id = R.string.session_history_search_label
                        )
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                item(key = "all") {
                    FilterChip(
                        selected =
                            filterState.dateFilter ==
                                    HistoryDateFilter.ALL,
                        onClick = {
                            viewModel.setDateFilter(
                                HistoryDateFilter.ALL
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(
                                    id = R.string
                                        .session_history_filter_all
                                )
                            )
                        }
                    )
                }

                item(key = "today") {
                    FilterChip(
                        selected =
                            filterState.dateFilter ==
                                    HistoryDateFilter.TODAY,
                        onClick = {
                            viewModel.setDateFilter(
                                HistoryDateFilter.TODAY
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(
                                    id = R.string
                                        .session_history_filter_today
                                )
                            )
                        }
                    )
                }

                item(key = "week") {
                    FilterChip(
                        selected =
                            filterState.dateFilter ==
                                    HistoryDateFilter.THIS_WEEK,
                        onClick = {
                            viewModel.setDateFilter(
                                HistoryDateFilter.THIS_WEEK
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(
                                    id = R.string
                                        .session_history_filter_week
                                )
                            )
                        }
                    )
                }

                item(key = "month") {
                    FilterChip(
                        selected =
                            filterState.dateFilter ==
                                    HistoryDateFilter.THIS_MONTH,
                        onClick = {
                            viewModel.setDateFilter(
                                HistoryDateFilter.THIS_MONTH
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(
                                    id = R.string
                                        .session_history_filter_month
                                )
                            )
                        }
                    )
                }
            }

            if (subjects.isNotEmpty()) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    item(key = "all_subjects") {
                        FilterChip(
                            selected =
                                filterState.subjectId == null,
                            onClick = {
                                viewModel.setSubjectFilter(
                                    null
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(
                                        id = R.string
                                            .session_history_all_subjects
                                    )
                                )
                            }
                        )
                    }

                    items(
                        items = subjects,
                        key = { subject ->
                            subject.id
                        }
                    ) { subject ->
                        FilterChip(
                            selected =
                                filterState.subjectId ==
                                        subject.id,
                            onClick = {
                                viewModel.setSubjectFilter(
                                    subject.id
                                )
                            },
                            label = {
                                Text(
                                    text = subject.name
                                )
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (filteredRecords.isEmpty()) {
                Text(
                    text = stringResource(
                        id = R.string
                            .session_history_no_results
                    ),
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredRecords,
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

                        val netRule =
                            netRuleByExamId[record.examId]
                                ?: NetCalculationRuleKeys.NO_EFFECT

                        SessionHistoryRecordCard(
                            record = record,
                            subjectName = subjectName,
                            netCalculationRule = netRule,
                            onEditClick = {
                                onEditRecordClick(
                                    record.id
                                )
                            },
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
}

@Composable
private fun SessionHistoryRecordCard(
    record: StudyRecordEntity,
    subjectName: String,
    netCalculationRule: String,
    onEditClick: () -> Unit,
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

                Row {
                    IconButton(
                        onClick = onEditClick
                    ) {
                        Icon(
                            imageVector =
                                Icons.Filled.Edit,
                            contentDescription =
                                stringResource(
                                    id = R.string
                                        .session_history_edit_button
                                )
                        )
                    }

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