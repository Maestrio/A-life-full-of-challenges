package com.challengetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.challengetracker.ui.screens.archive.ArchiveScreen
import com.challengetracker.ui.screens.create.CreateChallengeScreen
import com.challengetracker.ui.screens.detail.DetailScreen
import com.challengetracker.ui.screens.home.HomeScreen
import com.challengetracker.ui.screens.review.ReviewScreen
import com.challengetracker.ui.screens.settings.SettingsScreen
import com.challengetracker.ui.screens.statistics.StatisticsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Create : Screen("create?challengeId={challengeId}") {
        fun createRoute(challengeId: String? = null): String {
            return if (challengeId != null) "create?challengeId=$challengeId" else "create"
        }
    }
    data object Detail : Screen("detail/{challengeId}") {
        fun createRoute(challengeId: String): String = "detail/$challengeId"
    }
    data object Review : Screen("review/{challengeId}") {
        fun createRoute(challengeId: String): String = "review/$challengeId"
    }
    data object Archive : Screen("archive")
    data object Settings : Screen("settings")
    data object Statistics : Screen("statistics")
}

@Composable
fun ChallengeTrackerNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCreate = { navController.navigate(Screen.Create.createRoute()) },
                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateToArchive = { navController.navigate(Screen.Archive.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) }
            )
        }

        composable(
            route = Screen.Create.route,
            arguments = listOf(
                navArgument("challengeId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            CreateChallengeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("challengeId") { type = NavType.StringType }
            )
        ) {
            DetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.Create.createRoute(id)) },
                onNavigateToReview = { id -> navController.navigate(Screen.Review.createRoute(id)) }
            )
        }

        composable(
            route = Screen.Review.route,
            arguments = listOf(
                navArgument("challengeId") { type = NavType.StringType }
            )
        ) {
            ReviewScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Archive.route) {
            ArchiveScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
