package com.darling.spendwise.ui.screens.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darling.spendwise.ui.theme.TurquoisePrimary


@Composable
fun ChartScreen() {
    var selectedFilter by remember { mutableStateOf("Tháng") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TurquoisePrimary)
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Chi tiêu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterButton("Tuần", selectedFilter == "Tuần") {
                selectedFilter = "Tuần"
            }
            FilterButton("Tháng", selectedFilter == "Tháng") {
                selectedFilter = "Tháng"
            }
            FilterButton("Năm", selectedFilter == "Năm") {
                selectedFilter = "Năm"
            }
        }

        // Pie Chart placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Biểu đồ tròn sẽ hiển thị ở đây",
                color = Color.Gray
            )
        }

        Text(
            text = "Tỉnh % chi tiêu từng category",
            modifier = Modifier.padding(16.dp),
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun FilterButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) TurquoisePrimary else Color.LightGray,
            contentColor = Color.White
        )
    ) {
        Text(text)
    }
}