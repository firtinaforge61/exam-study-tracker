package com.examtracker.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.examtracker.app.screens.CreateExamScreen
import com.examtracker.app.screens.HomeScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onCreateExamClick = {
                    navController.navigate(Routes.CREATE_EXAM)
                }
            )
        }
        composable(Routes.CREATE_EXAM) {
            CreateExamScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onExamCreated = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }
    }
}