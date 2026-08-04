package com.examtracker.app.navigation

object Routes {
    const val HOME = "home"
    const val CREATE_EXAM = "create_exam"

    const val EXAM_ID_ARG = "examId"
    const val SUBJECT_ID_ARG = "subjectId"
    const val RECORD_ID_ARG = "recordId"

    const val EXAM_DETAIL = "exam_detail/{$EXAM_ID_ARG}"
    const val ADD_STUDY_RECORD = "add_study_record/{$EXAM_ID_ARG}"

    const val TIMER_MODE_SELECTION =
        "timer_mode_selection/{$EXAM_ID_ARG}"

    const val SETTINGS = "settings"

    const val TIMER_SESSION =
        "timer_session/{$EXAM_ID_ARG}/{$SUBJECT_ID_ARG}/" +
                "{timerModeType}/{focusMinutes}/{breakMinutes}/{totalCycles}"

    const val SESSION_HISTORY =
        "session_history/{$EXAM_ID_ARG}"

    const val EDIT_STUDY_RECORD =
        "edit_study_record/{$RECORD_ID_ARG}"

    const val SUBJECT_STATISTICS =
        "subject_statistics/{$SUBJECT_ID_ARG}"

    const val NO_SUBJECT_SELECTED = -1L
    const val ALL_EXAMS = -1L

    fun examDetailRoute(
        examId: Long
    ): String {
        return "exam_detail/$examId"
    }

    fun addStudyRecordRoute(
        examId: Long
    ): String {
        return "add_study_record/$examId"
    }

    fun timerModeSelectionRoute(
        examId: Long
    ): String {
        return "timer_mode_selection/$examId"
    }

    fun timerSessionRoute(
        examId: Long,
        subjectId: Long,
        timerModeType: String,
        focusMinutes: Int,
        breakMinutes: Int,
        totalCycles: Int
    ): String {
        return "timer_session/" +
                "$examId/" +
                "$subjectId/" +
                "$timerModeType/" +
                "$focusMinutes/" +
                "$breakMinutes/" +
                "$totalCycles"
    }

    fun sessionHistoryRoute(
        examId: Long = ALL_EXAMS
    ): String {
        return "session_history/$examId"
    }

    fun editStudyRecordRoute(
        recordId: Long
    ): String {
        return "edit_study_record/$recordId"
    }

    fun subjectStatisticsRoute(
        subjectId: Long
    ): String {
        return "subject_statistics/$subjectId"
    }
}