package com.darling.spendwise.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomBar(
    currentRoute: String = "home",
    onNavigate: (String) -> Unit = {}
) {
    NavigationBar(
        containerColor = Color(0xFF1C1C1E),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Outlined.Home, null) },
            label = { Text("Trang chủ") }
        )

        NavigationBarItem(
            selected = currentRoute == "chart",
            onClick = { onNavigate("chart") },
            icon = { Icon(Icons.Outlined.PieChart, null) },
            label = { Text("Biểu đồ") }
        )

        // 🔥 Chừa chỗ cho nút +
        Spacer(modifier = Modifier.weight(1f))

        NavigationBarItem(
            selected = currentRoute == "report",
            onClick = { onNavigate("report") },
            icon = { Icon(Icons.Outlined.ReceiptLong, null) },
            label = { Text("Báo cáo") }
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            icon = { Icon(Icons.Outlined.Person, null) },
            label = { Text("Tôi") }
        )
    }
}
