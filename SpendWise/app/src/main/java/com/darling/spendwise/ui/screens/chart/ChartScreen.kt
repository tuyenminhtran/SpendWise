package com.darling.spendwise.ui.screens.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darling.spendwise.ui.theme.TurquoisePrimary

@Composable
fun ChartScreen() {
    var selected by remember { mutableStateOf("Tháng") }

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) // Thêm dòng này
                .background(Color(0xFF00BFA6))
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SpendWise",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ChartFilter("Tuần", selected) { selected = "Tuần" }
            ChartFilter("Tháng", selected) { selected = "Tháng" }
            ChartFilter("Năm", selected) { selected = "Năm" }
        }

        // Fake Pie Chart
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Biểu đồ tròn\n(placeholder)",
                    color = Color.Gray
                )
            }
        }

        Text(
            text = "Tỷ lệ chi tiêu theo danh mục",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(8.dp))

        // Fake categories
        CategoryRow("Ăn uống", "40%", Color(0xFFF44336))
        CategoryRow("Mua sắm", "30%", Color(0xFF2196F3))
        CategoryRow("Giải trí", "20%", Color(0xFFFFC107))
        CategoryRow("Khác", "10%", Color(0xFF9E9E9E))
    }
}

@Composable
private fun ChartFilter(text: String, selected: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected == text) TurquoisePrimary else Color.LightGray
        )
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
private fun CategoryRow(title: String, percent: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Text(percent, color = color, fontWeight = FontWeight.Bold)
    }
}
