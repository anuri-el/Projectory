package com.projectory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.projectory.ui.achievements.AchievementsScreen
import com.projectory.ui.home.HomeScreen
import com.projectory.ui.memories.MemoriesScreen
import com.projectory.ui.navigation.*
import com.projectory.ui.theme.ProjectoryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectoryTheme {
                ProjectoryApp()
            }
        }
    }
}

@Composable
fun ProjectoryApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Check if current screen should show bottom bar
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToProject = { projectId ->
                        navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                    },
                    onNavigateToAddProject = {
                        navController.navigate(Screen.AddProject.route)
                    },
                    onNavigateToCalendar = {
                        navController.navigate(Screen.Calendar.route)
                    },
                    onNavigateToDailyStats = {
                        navController.navigate(Screen.DailyStats.route)
                    },
                    onNavigateToAnnualStats = {
                        navController.navigate(Screen.AnnualStats.route)
                    },
                    onNavigateToCollections = {
                        navController.navigate(Screen.Collections.route)
                    },
                    onNavigateToLibrary = {
                        navController.navigate(Screen.Library.route)
                    }
                )
            }

            // Memories Screen
            composable(Screen.Memories.route) {
                MemoriesScreen(
                    onNavigateToProject = { projectId ->
                        navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                    }
                )
            }

            // Achievements Screen
            composable(Screen.Achievements.route) {
                AchievementsScreen(
                    onNavigateToProject = { projectId ->
                        navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                    }
                )
            }

            // Project Detail Screen
            composable(
                route = Screen.ProjectDetail.route,
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) {
                // TODO: ProjectDetailScreen
                Text("Project Detail Screen")
            }

            // Add Project Screen
            composable(Screen.AddProject.route) {
                // TODO: AddProjectScreen
                Text("Add Project Screen")
            }

            // Calendar Screen
            composable(Screen.Calendar.route) {
                // TODO: CalendarScreen
                Text("Calendar Screen")
            }

            // Daily Stats Screen
            composable(Screen.DailyStats.route) {
                // TODO: DailyStatsScreen
                Text("Daily Stats Screen")
            }

            // Annual Stats Screen
            composable(Screen.AnnualStats.route) {
                // TODO: AnnualStatsScreen
                Text("Annual Stats Screen")
            }

            // Collections Screen
            composable(Screen.Collections.route) {
                // TODO: CollectionsScreen
                Text("Collections Screen")
            }

            // Library Screen
            composable(Screen.Library.route) {
                // TODO: LibraryScreen
                Text("Library Screen")
            }
        }
    }
}