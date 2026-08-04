package com.examtracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examtracker.app.data.local.StudyRecordEntity
import com.examtracker.app.data.local.SubjectEntity
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.data.repository.StudyRecordRepository
import com.examtracker.app.data.repository.SubjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

private const val HISTORY_STATE_TIMEOUT_MILLIS = 5_000L
private const val ONE_DAY_MILLIS = 24L * 60L * 60L * 1_000L

enum class HistoryDateFilter {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    CUSTOM,
    ALL
}

data class HistoryFilterState(
    val dateFilter: HistoryDateFilter = HistoryDateFilter.ALL,
    val customRangeStartMillis: Long? = null,
    val customRangeEndMillis: Long? = null,
    val subjectId: Long? = null,
    val searchQuery: String = ""
)

class SessionHistoryViewModel(
    private val examId: Long,
    examRepository: ExamRepository,
    subjectRepository: SubjectRepository,
    private val studyRecordRepository: StudyRecordRepository
) : ViewModel() {

    private val sharingStarted =
        SharingStarted.WhileSubscribed(
            HISTORY_STATE_TIMEOUT_MILLIS
        )

    private val allRecordsForScope:
            StateFlow<List<StudyRecordEntity>> =
        (
                if (examId >= 0L) {
                    studyRecordRepository
                        .getStudyRecordsForExam(examId)
                } else {
                    studyRecordRepository
                        .getAllStudyRecords()
                }
                ).stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = emptyList()
            )

    val subjectsInScope:
            StateFlow<List<SubjectEntity>> =
        (
                if (examId >= 0L) {
                    subjectRepository
                        .getSubjectsForExam(examId)
                } else {
                    subjectRepository
                        .getAllSubjects()
                }
                ).stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = emptyList()
            )

    val netRuleByExamId:
            StateFlow<Map<Long, String>> =
        examRepository
            .getAllExams()
            .map { exams ->
                exams.associate { exam ->
                    exam.id to exam.netCalculationRule
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = emptyMap()
            )

    private val _filterState =
        MutableStateFlow(
            HistoryFilterState()
        )

    val filterState:
            StateFlow<HistoryFilterState> =
        _filterState.asStateFlow()

    val filteredRecords:
            StateFlow<List<StudyRecordEntity>> =
        combine(
            allRecordsForScope,
            _filterState
        ) { records, filter ->
            applyFilter(
                records = records,
                filter = filter
            )
        }.stateIn(
            scope = viewModelScope,
            started = sharingStarted,
            initialValue = emptyList()
        )

    fun setDateFilter(
        dateFilter: HistoryDateFilter
    ) {
        _filterState.value =
            _filterState.value.copy(
                dateFilter = dateFilter
            )
    }

    fun setCustomDateRange(
        startMillis: Long,
        endMillis: Long
    ) {
        _filterState.value =
            _filterState.value.copy(
                dateFilter =
                    HistoryDateFilter.CUSTOM,
                customRangeStartMillis =
                    startMillis,
                customRangeEndMillis =
                    endMillis
            )
    }

    fun setSubjectFilter(
        subjectId: Long?
    ) {
        _filterState.value =
            _filterState.value.copy(
                subjectId = subjectId
            )
    }

    fun setSearchQuery(
        query: String
    ) {
        _filterState.value =
            _filterState.value.copy(
                searchQuery = query
            )
    }

    fun deleteStudyRecord(
        record: StudyRecordEntity
    ) {
        viewModelScope.launch {
            studyRecordRepository
                .deleteStudyRecord(record)
        }
    }

    private fun applyFilter(
        records: List<StudyRecordEntity>,
        filter: HistoryFilterState
    ): List<StudyRecordEntity> {
        var result = records

        result = when (filter.dateFilter) {
            HistoryDateFilter.TODAY -> {
                val start =
                    startOfDay(
                        System.currentTimeMillis()
                    )

                val end =
                    start + ONE_DAY_MILLIS

                result.filter { record ->
                    record.recordDateMillis in
                            start until end
                }
            }

            HistoryDateFilter.THIS_WEEK -> {
                val end =
                    startOfDay(
                        System.currentTimeMillis()
                    ) + ONE_DAY_MILLIS

                val start =
                    end - (7L * ONE_DAY_MILLIS)

                result.filter { record ->
                    record.recordDateMillis in
                            start until end
                }
            }

            HistoryDateFilter.THIS_MONTH -> {
                val end =
                    startOfDay(
                        System.currentTimeMillis()
                    ) + ONE_DAY_MILLIS

                val start =
                    end - (30L * ONE_DAY_MILLIS)

                result.filter { record ->
                    record.recordDateMillis in
                            start until end
                }
            }

            HistoryDateFilter.CUSTOM -> {
                val start =
                    filter.customRangeStartMillis

                val end =
                    filter.customRangeEndMillis

                if (
                    start != null &&
                    end != null
                ) {
                    result.filter { record ->
                        record.recordDateMillis in
                                start..end
                    }
                } else {
                    result
                }
            }

            HistoryDateFilter.ALL -> {
                result
            }
        }

        filter.subjectId?.let { subjectId ->
            result = result.filter { record ->
                record.subjectId == subjectId
            }
        }

        if (filter.searchQuery.isNotBlank()) {
            val normalizedQuery =
                filter.searchQuery
                    .trim()
                    .lowercase()

            result = result.filter { record ->
                record.note
                    ?.lowercase()
                    ?.contains(normalizedQuery) == true
            }
        }

        return result.sortedByDescending {
            it.recordDateMillis
        }
    }

    private fun startOfDay(
        millis: Long
    ): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}