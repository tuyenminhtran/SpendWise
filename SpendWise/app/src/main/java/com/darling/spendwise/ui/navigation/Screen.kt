package com.darling.spendwise.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Chart : Screen("chart", "Chart")
    object Report : Screen("report", "Report")
    object Profile : Screen("profile", "Profile")
}

