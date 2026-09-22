package com.groupeight.safezonesa.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.groupeight.safezonesa.navigation.Routes
import com.groupeight.safezonesa.ui.theme.AlertRed
import com.groupeight.safezonesa.ui.theme.DeepBlue

/**
 * Section 4.7: after login the bottom navigation bar (Home, Feed, SOS, Chat, Profile)
 * appears on every core screen so no destination is more than two taps away.
 */
private data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

private val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Filled.Home),
    BottomNavItem(Routes.FEED, "Feed", Icons.AutoMirrored.Filled.Feed),
    BottomNavItem(Routes.SOS, "SOS", Icons.Filled.Warning),
    BottomNavItem(Routes.CHAT, "Chat", Icons.AutoMirrored.Filled.Chat),
    BottomNavItem(Routes.PROFILE, "Profile", Icons.Filled.Person)
)

@Composable
fun SafeZoneBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (item.route == Routes.SOS) AlertRed else DeepBlue,
                    selectedTextColor = if (item.route == Routes.SOS) AlertRed else DeepBlue,
                    indicatorColor = androidx.compose.ui.graphics.Color(0xFFE3ECFB)
                )
            )
        }
    }
}