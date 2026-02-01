package com.darling.spendwise.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darling.spendwise.ui.screens.chart.ChartScreen
import com.darling.spendwise.ui.screens.home.HomeScreen
import com.darling.spendwise.ui.screens.profile.ProfileScreen
import com.darling.spendwise.ui.screens.report.ReportScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(navController) }
        composable("chart") { ChartScreen() }
        composable("report") { ReportScreen() }
        composable("profile") { ProfileScreen() }
    }
}
