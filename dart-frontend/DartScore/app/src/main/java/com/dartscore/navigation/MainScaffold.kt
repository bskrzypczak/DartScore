package com.dartscore.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dartscore.feature.friends.ui.FriendsScreen
import com.dartscore.feature.home.ui.DashboardScreen
import com.dartscore.feature.play.ui.GameSetupScreen
import com.dartscore.feature.play.ui.MatchScreen
import com.dartscore.feature.play.ui.VictoryScreen
import com.dartscore.feature.settings.ui.SettingsScreen
import com.dartscore.feature.training.ui.TrainingScreen

private val tabRoutes = TopLevelTab.entries.map { it.route }.toSet()

// Graf Main: Scaffold z dolnym paskiem + wewnętrzny NavHost.
// Pasek pokazujemy tylko na zakładkach; flow meczu (match/victory) jest bez paska.
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showBottomBar = currentRoute in tabRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TopLevelTab.entries.forEach { tab ->
                        val selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    // Standardowy wzorzec zakładek: zapisz/odtwórz stan,
                                    // nie buduj stosu duplikatów.
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            // --- zakładki paska ---
            composable(Routes.HOME) { DashboardScreen() }
            composable(Routes.FRIENDS) { FriendsScreen() }
            composable(Routes.PLAY) {
                GameSetupScreen(onStartMatch = { navController.navigate(Routes.MATCH) })
            }
            composable(Routes.TRAINING) { TrainingScreen() }
            composable(Routes.SETTINGS) { SettingsScreen() }

            // --- flow meczu (z zakładki "Graj") ---
            // MVP: trasy płaskie w tym samym NavHoście. Docelowo -> zagnieżdżony
            // graf "match" z MatchViewModel scope'owanym do tego grafu.
            composable(Routes.MATCH) {
                MatchScreen(
                    onFinish = {
                        navController.navigate(Routes.VICTORY) {
                            popUpTo(Routes.MATCH) { inclusive = true }
                        }
                    },
                    onQuit = { navController.popBackStack(Routes.PLAY, inclusive = false) },
                )
            }
            composable(Routes.VICTORY) {
                VictoryScreen(
                    onBackToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
