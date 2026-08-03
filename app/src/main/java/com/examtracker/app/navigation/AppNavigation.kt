package com.examtracker.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.examtracker.app.data.local.ExamDatabase
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.data.repository.StudyRecordRepository
import com.examtracker.app.data.repository.SubjectRepository
import com.examtracker.app.screens.CreateExamScreen
import com.examtracker.app.screens.ExamDetailScreen
import com.examtracker.app.screens.HomeScreen
import com.examtracker.app.viewmodel.ExamDetailViewModel
import com.examtracker.app.viewmodel.ExamDetailViewModelFactory
import com.examtracker.app.viewmodel.ExamViewModel
import com.examtracker.app.viewmodel.ExamViewModelFactory
import com.examtracker.app.screens.AddStudyRecordScreen
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    val database = remember { ExamDatabase.getInstance(context.applicationContext) }
    val examRepository = remember { ExamRepository(database.examDao()) }
    val subjectRepository = remember { SubjectRepository(database.subjectDao()) }
    val studyRecordRepository = remember { StudyRecordRepository(database.studyRecordDao()) }

    val examViewModel: ExamViewModel = viewModel(
        factory = ExamViewModelFactory(examRepository)
    )

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val exams by examViewModel.exams.collectAsStateWithLifecycle()

            HomeScreen(
                exams = exams,
                onCreateExamClick = {
                    navController.navigate(Routes.CREATE_EXAM)
                },
                onExamClick = { exam ->
                    navController.navigate(Routes.examDetailRoute(exam.id)) {
                        launchSingleTop = true
                    }
                },
                onDeleteExam = { exam ->
                    examViewModel.deleteExam(exam)
                }
            )
        }

        composable(Routes.CREATE_EXAM) {
            CreateExamScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCreateExam = { examName, examDateMillis, dailyQuestionGoal, netCalculationRule ->
                    examViewModel.createExam(
                        examName = examName,
                        examDateMillis = examDateMillis,
                        dailyQuestionGoal = dailyQuestionGoal,
                        netCalculationRule = netCalculationRule
                    )
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        composable(
            route = Routes.EXAM_DETAIL,
            arguments = listOf(navArgument(Routes.EXAM_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong(Routes.EXAM_ID_ARG) ?: -1L

            val examDetailViewModel: ExamDetailViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = ExamDetailViewModelFactory(
                    examId = examId,
                    examRepository = examRepository,
                    subjectRepository = subjectRepository,
                    studyRecordRepository = studyRecordRepository
                )
            )

            ExamDetailScreen(
                viewModel = examDetailViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddStudyRecordClick = {
                    navController.navigate(Routes.addStudyRecordRoute(examId))
                }
            )
        }

        composable(
            route = Routes.ADD_STUDY_RECORD,
            arguments = listOf(navArgument(Routes.EXAM_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong(Routes.EXAM_ID_ARG) ?: -1L

            // Share the same ExamDetailViewModel instance already created for
            // the ExamDetail destination, by scoping to that back stack entry.
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.examDetailRoute(examId))
            }

            val examDetailViewModel: ExamDetailViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = ExamDetailViewModelFactory(
                    examId = examId,
                    examRepository = examRepository,
                    subjectRepository = subjectRepository,
                    studyRecordRepository = studyRecordRepository
                )
            )

            AddStudyRecordScreen(
                viewModel = examDetailViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaved = {
                    navController.popBackStack()
                }
            )
        }
    }
}