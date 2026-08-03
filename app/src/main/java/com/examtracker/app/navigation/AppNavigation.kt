package com.examtracker.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.examtracker.app.data.local.ExamDatabase
import com.examtracker.app.data.repository.ExamRepository
import com.examtracker.app.screens.CreateExamScreen
import com.examtracker.app.screens.HomeScreen
import com.examtracker.app.viewmodel.ExamViewModel
import com.examtracker.app.viewmodel.ExamViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    val database = remember {
        ExamDatabase.getInstance(context.applicationContext)
    }

    val repository = remember {
        ExamRepository(database.examDao())
    }

    val examViewModel: ExamViewModel = viewModel(
        factory = ExamViewModelFactory(repository)
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
                onCreateExam = {
                        examName,
                        examDateMillis,
                        dailyQuestionGoal,
                        netCalculationRule ->

                    examViewModel.createExam(
                        examName = examName,
                        examDateMillis = examDateMillis,
                        dailyQuestionGoal = dailyQuestionGoal,
                        netCalculationRule = netCalculationRule
                    )

                    navController.popBackStack(
                        route = Routes.HOME,
                        inclusive = false
                    )
                }
            )
        }
    }
}