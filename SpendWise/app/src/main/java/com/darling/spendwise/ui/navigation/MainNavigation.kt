package com.darling.spendwise.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darling.spendwise.ui.screens.home.HomeScreen
import com.darling.spendwise.ui.screens.chart.ChartScreen
import com.darling.spendwise.ui.screens.report.ReportScreen
import com.darling.spendwise.ui.screens.profile.ProfileScreen
import com.darling.spendwise.viewModel.TransactionViewModel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Assessment


sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Trang chủ", Icons.Filled.Home)
    object Chart : Screen("chart", "Biểu đồ", Icons.Filled.PieChart)
    object Report : Screen("report", "Báo cáo", Icons.Filled.Assessment)
    object Profile : Screen("profile", "Tôi", Icons.Filled.Person)
}

@Composable
fun MainNavigation(transactionViewModel: TransactionViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Chart,
        Screen.Report,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
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
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel = transactionViewModel)
            }
            composable(Screen.Chart.route) {
                ChartScreen(viewModel = transactionViewModel)
            }
            composable(Screen.Report.route) {
                ReportScreen(viewModel = transactionViewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}