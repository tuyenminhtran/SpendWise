package com.darling.spendwise.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.darling.spendwise.ui.screens.add.AddTransactionScreen
import com.darling.spendwise.ui.screens.auth.LoginScreen
import com.darling.spendwise.ui.screens.auth.RegisterScreen
import com.darling.spendwise.ui.screens.chart.ChartScreen
import com.darling.spendwise.ui.screens.home.HomeScreen
import com.darling.spendwise.ui.screens.profile.EditProfileScreen
import com.darling.spendwise.ui.screens.profile.ProfileScreen
import com.darling.spendwise.ui.screens.report.ReportScreen
import com.darling.spendwise.viewModel.AuthViewModel
import com.darling.spendwise.viewModel.TransactionViewModel

private val NavSelected   = Color(0xFF1E88E5)
private val NavUnselected = Color(0xFFB0BEC5)

// Routes không hiện BottomBar
private val noBottomBarRoutes = setOf("add", "edit_profile", "login", "register")

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val isAdd: Boolean = false
)

private val bottomNavItems = listOf(
    BottomNavItem("home",    "Trang chủ", Icons.Default.Home),
    BottomNavItem("chart",   "Biểu đồ",  Icons.Default.PieChart),
    BottomNavItem("add",     "Thêm",     Icons.Default.Add, isAdd = true),
    BottomNavItem("report",  "Báo cáo",  Icons.Default.Description),
    BottomNavItem("profile", "Tôi",      Icons.Default.Person)
)

@Composable
fun MainNavigation(
    viewModel: TransactionViewModel,
    authViewModel: AuthViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    val navBg = if (isDarkMode) Color(0xFF1A2633) else Color.White

    Scaffold(
        containerColor = if (isDarkMode) Color(0xFF0F1923) else Color(0xFFF0F4F8),
        bottomBar = {
            if (currentRoute !in noBottomBarRoutes) {
                ModernBottomBar(
                    currentRoute = currentRoute,
                    isDarkMode = isDarkMode,
                    navBg = navBg,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen(viewModel) }
            composable("chart") { ChartScreen(viewModel) }
            composable("add") {
                AddTransactionScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("report") { ReportScreen(viewModel) }
            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    authViewModel = authViewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode
                )
            }
            composable("edit_profile") {
                EditProfileScreen(onBack = { navController.popBackStack() })
            }
            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = { navController.popBackStack() },
                    onNavigateToRegister = {
                        navController.navigate("register") {
                            launchSingleTop = true
                        }
                    },
                    onSkip = { navController.popBackStack() }
                )
            }
            composable("register") {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun ModernBottomBar(
    currentRoute: String,
    isDarkMode: Boolean,
    navBg: Color,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(navBg)
            .navigationBarsPadding()
            .padding(top = 22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                if (item.isAdd) {
                    Spacer(modifier = Modifier.width(64.dp))
                } else {
                    ModernNavItem(
                        item = item,
                        selected = currentRoute == item.route,
                        isDarkMode = isDarkMode,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }

        // FAB giữa
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopCenter)
                .shadow(elevation = 12.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1E88E5), Color(0xFF1565C0))
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNavigate("add") },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, "Thêm", tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun ModernNavItem(
    item: BottomNavItem,
    selected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val unselectedColor = if (isDarkMode) Color(0xFF4A6080) else NavUnselected
    val iconColor by animateColorAsState(
        targetValue = if (selected) NavSelected else unselectedColor,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconColor"
    )

    Column(
        modifier = Modifier
            .width(64.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NavSelected.copy(alpha = 0.12f))
                )
            }
            Icon(item.icon, item.title, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        Text(
            item.title,
            fontSize = 10.sp,
            color = iconColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1
        )
    }
}