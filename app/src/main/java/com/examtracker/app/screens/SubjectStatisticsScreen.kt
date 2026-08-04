package com.examtracker.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.examtracker.app.R
import com.examtracker.app.viewmodel.SubjectStatisticsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectStatisticsScreen(
    viewModel: SubjectStatisticsViewModel,
    onBackClick: () -> Unit
) {

    val subject =
        viewModel.subject.collectAsStateWithLifecycle()

    val sessionCount =
        viewModel.sessionCount.collectAsStateWithLifecycle()

    val totalMinutes =
        viewModel.totalMinutes.collectAsStateWithLifecycle()

    val totalQuestions =
        viewModel.totalQuestions.collectAsStateWithLifecycle()

    val averageStudyTime =
        viewModel.averageStudyTimeMinutes
            .collectAsStateWithLifecycle()

    val averageNet =
        viewModel.averageNet
            .collectAsStateWithLifecycle()

    val frequency =
        viewModel.studyFrequencyPerWeek
            .collectAsStateWithLifecycle()

    val lastDate =
        viewModel.lastStudyDateMillis
            .collectAsStateWithLifecycle()


    val formatter =
        remember {
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )
        }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =
                            subject.value?.name
                                ?: stringResource(
                                    id = R.string.subject_statistics_title
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
                                    id = R.string.content_description_back
                                )
                        )
                    }
                }
            )
        }
    ) { padding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {


            if (sessionCount.value == 0) {

                Text(
                    text =
                        stringResource(
                            id = R.string.subject_statistics_no_data
                        ),
                    style =
                        MaterialTheme.typography.bodyLarge
                )

                return@Column
            }


            StatisticCard(
                title =
                    stringResource(
                        id = R.string.subject_statistics_total_minutes_title
                    ),
                value =
                    stringResource(
                        id = R.string.subject_statistics_total_minutes_format,
                        totalMinutes.value
                    )
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            StatisticCard(
                title =
                    stringResource(
                        id = R.string.subject_statistics_total_questions_title
                    ),
                value =
                    stringResource(
                        id = R.string.subject_statistics_total_questions_format,
                        totalQuestions.value
                    )
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            StatisticCard(
                title =
                    stringResource(
                        id = R.string.subject_statistics_average_net_title
                    ),
                value =
                    stringResource(
                        id = R.string.subject_statistics_average_net_format,
                        averageNet.value
                    )
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            StatisticCard(
                title =
                    stringResource(
                        id = R.string.subject_statistics_average_study_time_title
                    ),
                value =
                    stringResource(
                        id = R.string.subject_statistics_average_study_time_format,
                        averageStudyTime.value
                    )
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            StatisticCard(
                title =
                    stringResource(
                        id = R.string.subject_statistics_study_frequency_title
                    ),
                value =
                    stringResource(
                        id = R.string.subject_statistics_study_frequency_format,
                        frequency.value
                    )
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            val lastStudy =
                lastDate.value?.let {
                    formatter.format(
                        Date(it)
                    )
                }
                    ?: stringResource(
                        id = R.string.subject_statistics_never_studied
                    )


            StatisticCard(
                title =
                    stringResource(
                        id = R.string.subject_statistics_last_study_date_title
                    ),
                value = lastStudy
            )

        }
    }
}



@Composable
private fun StatisticCard(
    title: String,
    value: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceVariant
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                style =
                    MaterialTheme.typography.labelMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = value,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}