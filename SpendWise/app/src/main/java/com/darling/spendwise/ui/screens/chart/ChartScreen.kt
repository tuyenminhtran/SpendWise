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
import androidx.compose.runtime.collectAsState
import com.darling.spendwise.viewModel.TransactionViewModel
import kotlin.math.abs

/* =======================
   COLORS & ICONS theo categoryId
   ======================= */

private val categoryColors = mapOf(
    1  to Color(0xFFFB8C00),
    2  to Color(0xFF43A047),
    3  to Color(0xFF1E88E5),
    4  to Color(0xFF8E24AA),
    5  to Color(0xFF00ACC1),
    6  to Color(0xFFE91E63),
    7  to Color(0xFF3949AB),
    8  to Color(0xFF00897B),
    9  to Color(0xFFFFB300),
    10 to Color(0xFF6D4C41),
    11 to Color(0xFF546E7A),
    12 to Color(0xFFD81B60),
    13 to Color(0xFF757575),
    14 to Color(0xFF5E35B1),
    15 to Color(0xFF039BE5),
    16 to Color(0xFFE53935)
)

private val categoryIcons = mapOf(
    1  to Icons.Default.ShoppingCart,
    2  to Icons.Default.Restaurant,
    3  to Icons.Default.Smartphone,
    4  to Icons.Default.SportsEsports,
    5  to Icons.Default.School,
    6  to Icons.Default.ContentCut,
    7  to Icons.Default.FitnessCenter,
    8  to Icons.Default.People,
    9  to Icons.Default.DirectionsBus,
    10 to Icons.Default.Checkroom,
    11 to Icons.Default.DirectionsCar,
    12 to Icons.Default.LocalBar,
    13 to Icons.Default.SmokingRooms,
    14 to Icons.Default.Computer,
    15 to Icons.Default.Flight,
    16 to Icons.Default.FavoriteBorder
)

private val categoryNames = mapOf(
    1  to "Mua sắm",
    2  to "Đồ ăn",
    3  to "Điện thoại",
    4  to "Giải trí",
    5  to "Giáo dục",
    6  to "Sắc đẹp",
    7  to "Thể thao",
    8  to "Xã hội",
    9  to "Đi lại",
    10 to "Quần áo",
    11 to "Ô tô",
    12 to "Rượu",
    13 to "Thuốc lá",
    14 to "Thiết bị",
    15 to "Du lịch",
    16 to "Sức khỏe"
)

/* =======================
   MODEL
   ======================= */

data class CategoryExpense(
    val name: String,
    val percentage: Float,
    val amount: Long,
    val color: Color,
    val icon: ImageVector
)

/* =======================
   CONSTANTS
   ======================= */

private val PrimaryBlue = Color(0xFF1E88E5)
private val PrimaryBlueSoft = Color(0xFFE3F2FD)

/* =======================
   CHART SCREEN
   ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(viewModel: TransactionViewModel) {
    var selectedTab by remember { mutableStateOf(1) }
    val transactions by viewModel.transactions.collectAsState()

    // Lọc chi tiêu
    val expenseList = transactions.filter { it.type == "expense" }
    val totalExpense = expenseList.sumOf { it.amount }.toLong()

    // Group theo categoryId → tính % và amount
    val grouped: List<CategoryExpense> = expenseList
        .groupBy { it.categoryId }
        .map { (categoryId, list) ->
            val amount = list.sumOf { it.amount }.toLong()
            val percentage = if (totalExpense > 0)
                (amount * 100f / totalExpense) else 0f
            CategoryExpense(
                name       = categoryNames[categoryId] ?: "Khác",
                percentage = percentage,
                amount     = amount,
                color      = categoryColors[categoryId] ?: Color.Gray,
                icon       = categoryIcons[categoryId] ?: Icons.Default.Category
            )
        }
        .sortedByDescending { it.amount }

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

                // Tabs Tuần / Tháng / Năm
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

            // Donut chart + legend
            item {
                if (totalExpense == 0L) {
                    // Trạng thái trống
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Chưa có giao dịch",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Donut giả
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
                                    text = formatMoney(totalExpense),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFFE53935)
                                )
                            }
                        }

                        // Legend — chỉ hiện top 4
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            grouped.take(4).forEach { category ->
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
                                        Text(
                                            category.name,
                                            fontSize = 13.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            "${String.format("%.1f", category.percentage)}%",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = category.color
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Divider
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFEEEEEE)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Chi tiết theo danh mục",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Danh sách danh mục thật
            if (grouped.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Không có dữ liệu", color = Color.Gray)
                    }
                }
            } else {
                items(grouped) { category ->
                    CategoryItem(category)
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFEEEEEE),
                        thickness = 0.5.dp
                    )
                }
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
                Text(category.name, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(
                    formatMoney(category.amount),
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE53935)
                )
            }

            Spacer(Modifier.height(6.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFEEEEEE))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(category.percentage / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(category.color)
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                "${String.format("%.1f", category.percentage)}%",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

/* =======================
   UTIL
   ======================= */

private fun formatMoney(value: Long): String {
    return "%,d₫".format(abs(value)).replace(",", ".")
}