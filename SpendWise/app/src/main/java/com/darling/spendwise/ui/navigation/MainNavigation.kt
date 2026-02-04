package com.darling.spendwise.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.*
import com.darling.spendwise.ui.screens.chart.ChartScreen
import com.darling.spendwise.ui.screens.home.HomeScreen
import com.darling.spendwise.ui.screens.profile.ProfileScreen
import com.darling.spendwise.ui.screens.report.ReportScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    val bottomItems = listOf(
        Screen.Home,
        Screen.Chart,
        Screen.Report,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route)
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    Screen.Home -> Icons.Default.Home
                                    Screen.Chart -> Icons.Default.BarChart
                                    Screen.Report -> Icons.Default.ReceiptLong
                                    Screen.Profile -> Icons.Default.Person
                                },
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(screen.title)
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Chart.route) { ChartScreen() }
            composable(Screen.Report.route) { ReportScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}
