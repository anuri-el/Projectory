package com.projectory.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Memories : Screen("memories")
    object Achievements : Screen("achievements")
    object ProjectDetail : Screen("project/{projectId}") {
        fun createRoute(projectId: Long) = "project/$projectId"
    }
    object AddProject : Screen("add_project")
    object Calendar : Screen("calendar")
    object DailyStats : Screen("daily_stats")
    object AnnualStats : Screen("annual_stats")
    object Collections : Screen("collections")
    object Library : Screen("library")
}