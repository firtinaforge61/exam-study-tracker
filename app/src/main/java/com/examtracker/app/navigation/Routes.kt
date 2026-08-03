package com.examtracker.app.navigation

object Routes {
    const val HOME = "home"
    const val CREATE_EXAM = "create_exam"

    const val EXAM_ID_ARG = "examId"

    const val EXAM_DETAIL = "exam_detail/{$EXAM_ID_ARG}"
    const val ADD_STUDY_RECORD = "add_study_record/{$EXAM_ID_ARG}"

    fun examDetailRoute(examId: Long): String = "exam_detail/$examId"

    fun addStudyRecordRoute(examId: Long): String = "add_study_record/$examId"
}