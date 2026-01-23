package com.projectory.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        title = "Home",
        icon = Icons.Default.Home
    )
    object Memories : BottomNavItem(
        route = Screen.Memories.route,
        title = "Memories",
        icon = Icons.Default.Description
    )
    object Achievements : BottomNavItem(
        route = Screen.Achievements.route,
        title = "Achievements",
        icon = Icons.Default.EmojiEvents
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Memories,
    BottomNavItem.Achievements
)