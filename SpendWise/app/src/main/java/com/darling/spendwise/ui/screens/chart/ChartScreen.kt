package com.darling.spendwise.ui.screens.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

/* =======================
   FAKE DATA
   ======================= */

data class CategoryExpense(
    val name: String,
    val percentage: Float,
    val amount: Long,
    val color: Color,
    val icon: ImageVector
)

private val demoCategories = listOf(
    CategoryExpense("Mua sắm", 95.25f, 16_070_000, Color(0xFFFB8C00), Icons.Default.ShoppingCart),
    CategoryExpense("Giải trí", 4.14f, 700_000, Color(0xFF43A047), Icons.Default.SportsEsports),
    CategoryExpense("Vé số", 0.59f, 100_000, Color(0xFF8E24AA), Icons.Default.ConfirmationNumber)
)

private val totalExpense = 16_900_000L

private val PrimaryBlue = Color(0xFF1E88E5)
private val PrimaryBlueSoft = Color(0xFFE3F2FD)

/* =======================
   CHART SCREEN
   ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen() {
    var selectedTab by remember { mutableStateOf(1) }
    var selectedPeriod by remember { mutableStateOf("Tháng trước") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Chi tiêu",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = Color.White
                    )
                }

                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.25f)),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Tuần", "Tháng", "Năm").forEachIndexed { index, text ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selectedTab == index) Color.White
                                    else Color.Transparent
                                )
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = text,
                                color = if (selectedTab == index) PrimaryBlue else Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // Period selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("thg 11 2025", color = Color.Gray, fontSize = 14.sp)
                    Text("thg 12 2025", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        selectedPeriod,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(3.dp)
                            .background(PrimaryBlue)
                            .align(Alignment.CenterEnd)
                    )
                }
            }

            // Donut chart
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${formatMoney(totalExpense)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        demoCategories.forEach { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(category.color)
                                )
                                Column {
                                    Text(category.name, fontSize = 14.sp)
                                    Text(
                                        "${String.format("%.2f", category.percentage)}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Dots indicator
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == 0) PrimaryBlue
                                    else Color(0xFF90CAF9)
                                )
                        )
                    }
                }
            }

            // Category list
            items(demoCategories) { category ->
                CategoryItem(category)
            }
        }
    }
}

/* =======================
   CATEGORY ITEM
   ======================= */

@Composable
private fun CategoryItem(category: CategoryExpense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(category.color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${category.name}  ${String.format("%.2f", category.percentage)}%",
                    fontSize = 16.sp
                )
                Text(
                    formatMoney(category.amount),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFEEEEEE))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(category.percentage / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(category.color)
                )
            }
        }
    }
}

/* =======================
   UTIL
   ======================= */

private fun formatMoney(value: Long): String {
    return "%,d".format(abs(value)).replace(",", ".")
}
