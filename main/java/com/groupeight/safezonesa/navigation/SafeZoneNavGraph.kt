package com.groupeight.safezonesa.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.groupeight.safezonesa.ui.components.SafeZoneBottomBar
import com.groupeight.safezonesa.ui.screens.*
import com.groupeight.safezonesa.data.SessionManager

/** Top-level routes that show the bottom navigation bar (Section 4.7). */
private val bottomBarRoutes = setOf(Routes.HOME, Routes.FEED, Routes.SOS, Routes.CHAT, Routes.PROFILE)

@Composable
fun SafeZoneNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                SafeZoneBottomBar(navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(Routes.SPLASH) {
                SplashScreen { loggedIn ->
                    val next = when {
                        !SessionManager.hasSeenOnboarding(context) -> Routes.ONBOARDING
                        loggedIn -> Routes.HOME
                        else -> Routes.LOGIN
                    }
                    navController.navigate(next) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            }
            composable(Routes.ONBOARDING) {
                OnboardingScreen {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onOtpRequired = { phone -> navController.navigate(Routes.otpVerify(phone)) },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable(
                Routes.OTP_VERIFY,
                arguments = listOf(navArgument("phone") { type = NavType.StringType })
            ) { otpEntry ->
                val phone = otpEntry.arguments?.getString("phone") ?: ""
                OtpVerificationScreen(
                    phone = phone,
                    onVerified = {
                        // No token is saved at OTP verification, so send the user to Login.
                        navController.navigate(Routes.LOGIN) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    }
                )
            }

            composable(Routes.HOME) {
                HomeDashboardScreen(
                    onSos = { navController.navigate(Routes.SOS) },
                    onWalkWithMe = { navController.navigate(Routes.WALK_WITH_ME) },
                    onCrimeMap = { navController.navigate(Routes.CRIME_MAP) },
                    onDirectory = { navController.navigate(Routes.EMERGENCY_DIRECTORY) },
                    onChat = { navController.navigate(Routes.CHAT) },
                    onNotifications = { navController.navigate(Routes.NOTIFICATIONS) }
                )
            }
            composable(Routes.FEED) { CommunityFeedScreen() }
            composable(Routes.CRIME_MAP) { CrimeMapScreen() }
            composable(Routes.SOS) { SosScreen() }
            composable(Routes.CHAT) { CommunityChatScreen() }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onReputation = { navController.navigate(Routes.USER_REPUTATION) },
                    onAiPrediction = { navController.navigate(Routes.AI_CRIME_PREDICTION) },
                    onVolunteerResponders = { navController.navigate(Routes.VOLUNTEER_RESPONDERS) },
                    onEmergencyContacts = { navController.navigate(Routes.EMERGENCY_DIRECTORY) },
                    onMissingPersons = { navController.navigate(Routes.MISSING_PERSONS) },
                    onSettings = { navController.navigate(Routes.SETTINGS) },
                    onLoggedOut = {
                        navController.navigate(Routes.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
                    }
                )
            }

            composable(Routes.WALK_WITH_ME) { WalkWithMeScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.SAFE_ROUTE) { SafeRouteScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.BEWARE) { BewareScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.MISSING_PERSONS) { MissingPersonsScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.EMERGENCY_DIRECTORY) { EmergencyDirectoryScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.NOTIFICATIONS) { NotificationsScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.USER_REPUTATION) { UserReputationScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.AI_CRIME_PREDICTION) { AiCrimePredictionScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.VOLUNTEER_RESPONDERS) { VolunteerRespondersScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLoggedOut = {
                        navController.navigate(Routes.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
                    }
                )
            }
        }
    }
}