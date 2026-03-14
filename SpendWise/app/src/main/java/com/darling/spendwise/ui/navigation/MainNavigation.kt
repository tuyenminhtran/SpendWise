package com.darling.spendwise.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.darling.spendwise.ui.screens.add.AddTransactionScreen
import com.darling.spendwise.ui.screens.chart.ChartScreen
import com.darling.spendwise.ui.screens.home.HomeScreen
import com.darling.spendwise.ui.screens.profile.ProfileScreen
import com.darling.spendwise.ui.screens.report.ReportScreen
import com.darling.spendwise.ui.theme.AppColors
import com.darling.spendwise.viewModel.TransactionViewModel

/* =======================
   BOTTOM NAV ITEMS
   ======================= */

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val isAdd: Boolean = false
)

private val bottomNavItems = listOf(
    BottomNavItem("home", "Trang chủ", Icons.Default.Home),
    BottomNavItem("chart", "Biểu đồ", Icons.Default.PieChart),
    BottomNavItem("add", "Thêm", Icons.Default.Add, isAdd = true),
    BottomNavItem("report", "Báo cáo", Icons.Default.Description),
    BottomNavItem("profile", "Tôi", Icons.Default.Person)
)

/* =======================
   MAIN NAVIGATION
   ======================= */

@Composable
fun MainNavigation(viewModel: TransactionViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        bottomBar = {
            // Ẩn bottom bar khi ở màn hình Add
            if (currentRoute != "add") {
                CustomBottomBar(
                    currentRoute = currentRoute,
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
            composable("profile") { ProfileScreen(viewModel) }
        }
    }
}

/* =======================
   CUSTOM BOTTOM BAR
   ======================= */

@Composable
private fun CustomBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        // Thanh bottom nền
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    if (!item.isAdd) {
                        BottomNavItemView(
                            item = item,
                            selected = currentRoute == item.route,
                            onClick = { onNavigate(item.route) }
                        )
                    } else {
                        Spacer(modifier = Modifier.width(64.dp))
                    }
                }
            }
        }

        // FAB Add nổi – TRÒN CHUẨN
        FloatingActionButton(
            onClick = { onNavigate("add") },
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp),
            shape = CircleShape,
            containerColor = AppColors.Primary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

/* =======================
   BOTTOM ITEM
   ======================= */

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = if (selected) AppColors.Primary else AppColors.IconInactive,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = item.title,
            fontSize = 11.sp,
            color = if (selected) AppColors.Primary else AppColors.TextHint,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
