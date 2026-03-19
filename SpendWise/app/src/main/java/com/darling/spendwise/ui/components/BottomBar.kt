/*
package com.darling.spendwise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val isAdd: Boolean = false
)

val bottomNavItems = listOf(
    BottomNavItem("home", Icons.Default.Home, "Trang chủ"),
    BottomNavItem("chart", Icons.Default.PieChart, "Biểu đồ"),
    BottomNavItem("add", Icons.Default.Add, "Thêm", isAdd = true),
    BottomNavItem("report", Icons.Default.Description, "Báo cáo"),
    BottomNavItem("profile", Icons.Default.Person, "Tôi")
)

@Composable
fun BottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                if (item.isAdd) {
                    // Nút + ở giữa (nổi lên)
                    FloatingActionButton(
                        onClick = { onNavigate(item.route) },
                        containerColor = Color(0xFFFFC107),
                        contentColor = Color.Black,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    // Các tab bình thường
                    BottomNavItemView(
                        item = item,
                        selected = currentRoute == item.route,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .width(56.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (selected) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = item.label,
            fontSize = 11.sp,
            color = if (selected) Color(0xFFFFC107) else Color.Gray,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}*/
