package com.example.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.ui.screens.AddEditTaskScreen
import com.example.myapplication.presentation.ui.screens.MainScreen

@Composable
fun TodoNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(route = Screen.Main.route) {
            MainScreen(
                onNavigateToAddTask = {
                    navController.navigate(Screen.AddEditTask.route)
                },
                onNavigateToEditTask = { taskId ->
                    navController.navigate(Screen.AddEditTask.createRoute(taskId))
                }
            )
        }
        
        composable(
            route = Screen.AddEditTask.route,
        ) {
            AddEditTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.AddEditTask.routeWithArgs,
        ) {
            AddEditTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object AddEditTask : Screen("add_edit_task") {
        const val routeWithArgs = "add_edit_task?taskId={taskId}"
        fun createRoute(taskId: String) = "add_edit_task?taskId=$taskId"
    }
}