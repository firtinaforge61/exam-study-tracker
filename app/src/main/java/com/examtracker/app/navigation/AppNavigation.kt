package com.examtracker.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.examtracker.app.screens.AddStudyRecordScreen
import com.examtracker.app.screens.CreateExamScreen
import com.examtracker.app.screens.EditStudyRecordScreen
import com.examtracker.app.screens.ExamDetailScreen
import com.examtracker.app.screens.HomeScreen
import com.examtracker.app.screens.SessionHistoryScreen
import com.examtracker.app.screens.TimerModeSelectionScreen
import com.examtracker.app.screens.TimerScreen
import com.examtracker.app.viewmodel.DashboardViewModel
import com.examtracker.app.viewmodel.DashboardViewModelFactory
import com.examtracker.app.viewmodel.EditStudyRecordViewModel
import com.examtracker.app.viewmodel.EditStudyRecordViewModelFactory
import com.examtracker.app.viewmodel.ExamDetailViewModel
import com.examtracker.app.viewmodel.ExamDetailViewModelFactory
import com.examtracker.app.viewmodel.ExamViewModel
import com.examtracker.app.viewmodel.ExamViewModelFactory
import com.examtracker.app.viewmodel.SessionHistoryViewModel
import com.examtracker.app.viewmodel.SessionHistoryViewModelFactory
import com.examtracker.app.viewmodel.TimerConfig
import com.examtracker.app.viewmodel.TimerModeType
import com.examtracker.app.viewmodel.TimerViewModel
import com.examtracker.app.viewmodel.TimerViewModelFactory
import com.examtracker.app.screens.SubjectStatisticsScreen
import com.examtracker.app.viewmodel.SubjectStatisticsViewModel

import com.examtracker.app.viewmodel.SubjectStatisticsViewModelFactory
import com.examtracker.app.screens.SettingsScreen
import com.examtracker.app.settings.SettingsPreferences
import com.examtracker.app.settings.SettingsRepository
import com.examtracker.app.settings.SettingsViewModel
import com.examtracker.app.settings.SettingsViewModelFactory
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    val database = remember {
        ExamDatabase.getInstance(
            context.applicationContext
        )
    }

    val examRepository = remember {
        ExamRepository(
            database.examDao()
        )
    }

    val subjectRepository = remember {
        SubjectRepository(
            database.subjectDao()
        )
    }

    val studyRecordRepository = remember {
        StudyRecordRepository(
            database.studyRecordDao()
        )
    }
    val settingsRepository = remember {
        SettingsRepository(
            SettingsPreferences(
                context.applicationContext
            )
        )
    }
    val examViewModel: ExamViewModel = viewModel(
        factory = ExamViewModelFactory(
            examRepository
        )
    )

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(
            route = Routes.SUBJECT_STATISTICS,
            arguments = listOf(
                navArgument(Routes.SUBJECT_ID_ARG) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val subjectId =
                backStackEntry.arguments?.getLong(Routes.SUBJECT_ID_ARG) ?: -1L

            val viewModel: SubjectStatisticsViewModel = viewModel(
                factory = SubjectStatisticsViewModelFactory(
                    subjectId = subjectId,
                    subjectRepository = subjectRepository,
                    examRepository = examRepository,
                    studyRecordRepository = studyRecordRepository
                )
            )

            SubjectStatisticsScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.SETTINGS
        ) { backStackEntry ->
            val settingsViewModel: SettingsViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = SettingsViewModelFactory(
                    repository = settingsRepository
                )
            )

            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.HOME
        ) { backStackEntry ->
            val exams by examViewModel
                .exams
                .collectAsStateWithLifecycle()

            val dashboardViewModel:
                    DashboardViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = DashboardViewModelFactory(
                    examRepository = examRepository,
                    studyRecordRepository =
                        studyRecordRepository,
                    subjectRepository =
                        subjectRepository
                )
            )

            val todaysStudyMinutes by
            dashboardViewModel
                .todaysStudyMinutes
                .collectAsStateWithLifecycle()

            val todaysGoalPercentage by
            dashboardViewModel
                .todaysGoalPercentage
                .collectAsStateWithLifecycle()

            val currentStreak by
            dashboardViewModel
                .currentStreak
                .collectAsStateWithLifecycle()

            val weeklyMinutes by
            dashboardViewModel
                .weeklyMinutes
                .collectAsStateWithLifecycle()

            val monthlyMinutes by
            dashboardViewModel
                .monthlyMinutes
                .collectAsStateWithLifecycle()

            val quickResume by
            dashboardViewModel
                .quickResume
                .collectAsStateWithLifecycle()

            val recentStudyRecords by
            dashboardViewModel
                .recentStudyRecords
                .collectAsStateWithLifecycle()

            val upcomingExams by
            dashboardViewModel
                .upcomingExams
                .collectAsStateWithLifecycle()

            val recentExams by
            dashboardViewModel
                .recentExams
                .collectAsStateWithLifecycle()

            HomeScreen(
                exams = exams,
                todaysStudyMinutes =
                    todaysStudyMinutes,
                todaysGoalPercentage =
                    todaysGoalPercentage,
                currentStreak =
                    currentStreak,
                weeklyMinutes =
                    weeklyMinutes,
                monthlyMinutes =
                    monthlyMinutes,
                quickResume =
                    quickResume,
                recentStudyRecords =
                    recentStudyRecords,
                upcomingExams =
                    upcomingExams,
                recentExams =
                    recentExams,
                onCreateExamClick = {
                    navController.navigate(
                        Routes.CREATE_EXAM
                    )
                },
                onExamClick = { exam ->
                    navController.navigate(
                        Routes.examDetailRoute(
                            exam.id
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
                onDeleteExam = { exam ->
                    examViewModel.deleteExam(
                        exam
                    )
                },
                onQuickResumeClick = { examId ->
                    navController.navigate(
                        Routes.timerModeSelectionRoute(
                            examId
                        )
                    )
                },
                onSettingsClick = {
                    navController.navigate(
                        Routes.SETTINGS
                    )
                }
            )
        }

        composable(
            route = Routes.CREATE_EXAM
        ) {
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
                        examDateMillis =
                            examDateMillis,
                        dailyQuestionGoal =
                            dailyQuestionGoal,
                        netCalculationRule =
                            netCalculationRule
                    )

                    navController.popBackStack(
                        route = Routes.HOME,
                        inclusive = false
                    )
                }
            )
        }

        composable(
            route = Routes.EXAM_DETAIL,
            arguments = listOf(
                navArgument(
                    Routes.EXAM_ID_ARG
                ) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val examId =
                backStackEntry.arguments
                    ?.getLong(
                        Routes.EXAM_ID_ARG
                    )
                    ?: -1L

            val examDetailViewModel:
                    ExamDetailViewModel = viewModel(
                viewModelStoreOwner =
                    backStackEntry,
                factory =
                    ExamDetailViewModelFactory(
                        examId = examId,
                        examRepository =
                            examRepository,
                        subjectRepository =
                            subjectRepository,
                        studyRecordRepository =
                            studyRecordRepository
                    )
            )

            ExamDetailScreen(
                viewModel = examDetailViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddStudyRecordClick = {
                    navController.navigate(
                        Routes.addStudyRecordRoute(examId)
                    )
                },
                onStartStudySessionClick = {
                    navController.navigate(
                        Routes.timerModeSelectionRoute(examId)
                    )
                },
                onHistoryClick = {
                    navController.navigate(
                        Routes.sessionHistoryRoute(examId)
                    )
                },
                onSubjectClick = { subject ->
                    navController.navigate(
                        Routes.subjectStatisticsRoute(subject.id)
                    )
                }
            )}
            composable(
                route = Routes.ADD_STUDY_RECORD,
                arguments = listOf(
                    navArgument(
                        Routes.EXAM_ID_ARG
                    ) {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val examId =
                    backStackEntry.arguments
                        ?.getLong(
                            Routes.EXAM_ID_ARG
                        )
                        ?: -1L

                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(
                            Routes.examDetailRoute(
                                examId
                            )
                        )
                    }

                val examDetailViewModel:
                        ExamDetailViewModel = viewModel(
                    viewModelStoreOwner =
                        parentEntry,
                    factory =
                        ExamDetailViewModelFactory(
                            examId = examId,
                            examRepository =
                                examRepository,
                            subjectRepository =
                                subjectRepository,
                            studyRecordRepository =
                                studyRecordRepository
                        )
                )

                AddStudyRecordScreen(
                    viewModel =
                        examDetailViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaved = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route =
                    Routes.TIMER_MODE_SELECTION,
                arguments = listOf(
                    navArgument(
                        Routes.EXAM_ID_ARG
                    ) {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val examId =
                    backStackEntry.arguments
                        ?.getLong(
                            Routes.EXAM_ID_ARG
                        )
                        ?: -1L

                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(
                            Routes.examDetailRoute(
                                examId
                            )
                        )
                    }

                val examDetailViewModel:
                        ExamDetailViewModel = viewModel(
                    viewModelStoreOwner =
                        parentEntry,
                    factory =
                        ExamDetailViewModelFactory(
                            examId = examId,
                            examRepository =
                                examRepository,
                            subjectRepository =
                                subjectRepository,
                            studyRecordRepository =
                                studyRecordRepository
                        )
                )

                TimerModeSelectionScreen(
                    viewModel =
                        examDetailViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onStartSession = {
                            subjectId,
                            config ->

                        navController.navigate(
                            Routes.timerSessionRoute(
                                examId = examId,
                                subjectId =
                                    subjectId,
                                timerModeType =
                                    config.modeType.name,
                                focusMinutes =
                                    config.focusMinutes,
                                breakMinutes =
                                    config.breakMinutes,
                                totalCycles =
                                    config.totalCycles
                            )
                        )
                    }
                )
            }

            composable(
                route = Routes.TIMER_SESSION,
                arguments = listOf(
                    navArgument(
                        Routes.EXAM_ID_ARG
                    ) {
                        type = NavType.LongType
                    },
                    navArgument(
                        Routes.SUBJECT_ID_ARG
                    ) {
                        type = NavType.LongType
                    },
                    navArgument(
                        "timerModeType"
                    ) {
                        type = NavType.StringType
                    },
                    navArgument(
                        "focusMinutes"
                    ) {
                        type = NavType.IntType
                    },
                    navArgument(
                        "breakMinutes"
                    ) {
                        type = NavType.IntType
                    },
                    navArgument(
                        "totalCycles"
                    ) {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val arguments =
                    backStackEntry.arguments

                val examId =
                    arguments?.getLong(
                        Routes.EXAM_ID_ARG
                    ) ?: -1L

                val subjectId =
                    arguments?.getLong(
                        Routes.SUBJECT_ID_ARG
                    ) ?: -1L

                val modeType =
                    TimerModeType.fromArgOrDefault(
                        arguments?.getString(
                            "timerModeType"
                        )
                    )

                val focusMinutes =
                    arguments?.getInt(
                        "focusMinutes"
                    ) ?: 25

                val breakMinutes =
                    arguments?.getInt(
                        "breakMinutes"
                    ) ?: 5

                val totalCycles =
                    arguments?.getInt(
                        "totalCycles"
                    ) ?: 4

                val config = TimerConfig(
                    modeType = modeType,
                    focusMinutes =
                        focusMinutes,
                    breakMinutes =
                        breakMinutes,
                    totalCycles =
                        totalCycles
                )

                val timerViewModel:
                        TimerViewModel = viewModel(
                    viewModelStoreOwner =
                        backStackEntry,
                    factory =
                        TimerViewModelFactory(
                            examId = examId,
                            subjectId =
                                subjectId,
                            config = config,
                            examRepository =
                                examRepository,
                            subjectRepository =
                                subjectRepository,
                            studyRecordRepository =
                                studyRecordRepository
                        )
                )

                TimerScreen(
                    viewModel =
                        timerViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSessionSaved = {
                        navController.popBackStack(
                            route =
                                Routes.examDetailRoute(
                                    examId
                                ),
                            inclusive = false
                        )
                    }
                )
            }

            composable(
                route =
                    Routes.SESSION_HISTORY,
                arguments = listOf(
                    navArgument(
                        Routes.EXAM_ID_ARG
                    ) {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val examId =
                    backStackEntry.arguments
                        ?.getLong(
                            Routes.EXAM_ID_ARG
                        )
                        ?: Routes.ALL_EXAMS

                val sessionHistoryViewModel:
                        SessionHistoryViewModel = viewModel(
                    viewModelStoreOwner =
                        backStackEntry,
                    factory =
                        SessionHistoryViewModelFactory(
                            examId = examId,
                            examRepository =
                                examRepository,
                            subjectRepository =
                                subjectRepository,
                            studyRecordRepository =
                                studyRecordRepository
                        )
                )

                SessionHistoryScreen(
                    viewModel =
                        sessionHistoryViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onEditRecordClick = { recordId ->
                        navController.navigate(
                            Routes.editStudyRecordRoute(
                                recordId
                            )
                        )
                    }
                )
            }

            composable(
                route =
                    Routes.EDIT_STUDY_RECORD,
                arguments = listOf(
                    navArgument(
                        Routes.RECORD_ID_ARG
                    ) {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val recordId =
                    backStackEntry.arguments
                        ?.getLong(
                            Routes.RECORD_ID_ARG
                        )
                        ?: -1L

                val editStudyRecordViewModel:
                        EditStudyRecordViewModel = viewModel(
                    viewModelStoreOwner =
                        backStackEntry,
                    factory =
                        EditStudyRecordViewModelFactory(
                            recordId =
                                recordId,
                            studyRecordRepository =
                                studyRecordRepository
                        )
                )

                EditStudyRecordScreen(
                    viewModel =
                        editStudyRecordViewModel,
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
