package com.darling.spendwise.ui.screens.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darling.spendwise.viewModel.TransactionViewModel
import java.util.Calendar
import kotlin.math.abs

/* =======================
   MAPS
   ======================= */

private val categoryColors = mapOf(
    1  to Color(0xFFFB8C00), 2  to Color(0xFF43A047),
    3  to Color(0xFF1E88E5), 4  to Color(0xFF8E24AA),
    5  to Color(0xFF00ACC1), 6  to Color(0xFFE91E63),
    7  to Color(0xFF3949AB), 8  to Color(0xFF00897B),
    9  to Color(0xFFFFB300), 10 to Color(0xFF6D4C41),
    11 to Color(0xFF546E7A), 12 to Color(0xFFD81B60),
    13 to Color(0xFF757575), 14 to Color(0xFF5E35B1),
    15 to Color(0xFF039BE5), 16 to Color(0xFFE53935)
)

private val categoryIcons = mapOf(
    1  to Icons.Default.ShoppingCart, 2  to Icons.Default.Restaurant,
    3  to Icons.Default.Smartphone,   4  to Icons.Default.SportsEsports,
    5  to Icons.Default.School,       6  to Icons.Default.ContentCut,
    7  to Icons.Default.FitnessCenter,8  to Icons.Default.People,
    9  to Icons.Default.DirectionsBus,10 to Icons.Default.Checkroom,
    11 to Icons.Default.DirectionsCar,12 to Icons.Default.LocalBar,
    13 to Icons.Default.SmokingRooms, 14 to Icons.Default.Computer,
    15 to Icons.Default.Flight,       16 to Icons.Default.FavoriteBorder
)

private val categoryNames = mapOf(
    1  to "Mua sắm",   2  to "Đồ ăn",     3  to "Điện thoại",
    4  to "Giải trí",  5  to "Giáo dục",  6  to "Sắc đẹp",
    7  to "Thể thao",  8  to "Xã hội",    9  to "Đi lại",
    10 to "Quần áo",   11 to "Ô tô",      12 to "Rượu",
    13 to "Thuốc lá",  14 to "Thiết bị",  15 to "Du lịch",
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

private val PrimaryBlue = Color(0xFF1E88E5)

/* =======================
   CHART SCREEN
   ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(viewModel: TransactionViewModel) {
    // 0 = Tuần, 1 = Tháng, 2 = Năm
    var selectedTab by remember { mutableIntStateOf(1) }
    var showTypePicker by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("expense") } // expense / income

    val now = Calendar.getInstance()
    var selectedMonth by remember { mutableIntStateOf(now.get(Calendar.MONTH) + 1) }
    var selectedYear by remember { mutableIntStateOf(now.get(Calendar.YEAR)) }
    var showMonthPicker by remember { mutableStateOf(false) }

    val transactions by viewModel.transactions.collectAsState()

    // Lọc theo tab + tháng/năm
    val filtered = transactions.filter { tx ->
        val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
        val matchType = tx.type == selectedType
        val matchPeriod = when (selectedTab) {
            0 -> { // Tuần hiện tại
                val txWeek = cal.get(Calendar.WEEK_OF_YEAR)
                val txYear = cal.get(Calendar.YEAR)
                val nowWeek = now.get(Calendar.WEEK_OF_YEAR)
                val nowYear = now.get(Calendar.YEAR)
                txWeek == nowWeek && txYear == nowYear
            }
            1 -> { // Tháng đang chọn
                cal.get(Calendar.MONTH) + 1 == selectedMonth &&
                        cal.get(Calendar.YEAR) == selectedYear
            }
            else -> { // Năm đang chọn
                cal.get(Calendar.YEAR) == selectedYear
            }
        }
        matchType && matchPeriod
    }

    val total = filtered.sumOf { it.amount }.toLong()

    val grouped: List<CategoryExpense> = filtered
        .groupBy { it.categoryId }
        .map { (categoryId, list) ->
            val amount = list.sumOf { it.amount }.toLong()
            val percentage = if (total > 0) (amount * 100f / total) else 0f
            CategoryExpense(
                name       = categoryNames[categoryId] ?: "Khác",
                percentage = percentage,
                amount     = amount,
                color      = categoryColors[categoryId] ?: Color.Gray,
                icon       = categoryIcons[categoryId] ?: Icons.Default.Category
            )
        }
        .sortedByDescending { it.amount }

    val accentColor = if (selectedType == "expense") Color(0xFFE53935) else Color(0xFF43A047)
    val typeLabel = if (selectedType == "expense") "Chi tiêu" else "Thu nhập"

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(PrimaryBlue)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bấm để chọn Chi tiêu / Thu nhập
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showTypePicker = true }
                    ) {
                        Text(typeLabel,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White)
                        Icon(Icons.Default.ArrowDropDown,
                            null, tint = Color.White)
                    }

                    // Bấm để chọn tháng (chỉ hiện khi tab Tháng hoặc Năm)
                    if (selectedTab != 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showMonthPicker = true }
                        ) {
                            Text(
                                text = if (selectedTab == 1) "$selectedMonth/$selectedYear" else "$selectedYear",
                                fontSize = 14.sp, color = Color.White
                            )
                            Icon(Icons.Default.CalendarMonth,
                                null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    } else {
                        Icon(Icons.Default.CalendarMonth,
                            null, tint = Color.White)
                    }
                }

                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                ) {
                    listOf("Tuần", "Tháng", "Năm").forEachIndexed { index, text ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedTab == index) Color.White else Color.Transparent)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { selectedTab = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text,
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
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // Donut chart
            item {
                if (total == 0L) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PieChart,
                                null, tint = Color.LightGray,
                                modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("Chưa có dữ liệu", color = Color.Gray, fontSize = 16.sp)
                            Text(
                                when (selectedTab) {
                                    0 -> "trong tuần này"
                                    1 -> "trong tháng $selectedMonth/$selectedYear"
                                    else -> "trong năm $selectedYear"
                                },
                                color = Color.LightGray, fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Donut chart thật
                        Box(
                            modifier = Modifier.size(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DonutChart(
                                categories = grouped,
                                modifier = Modifier.size(180.dp)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = formatShort(total),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = accentColor
                                )
                                Text(
                                    text = typeLabel,
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Legend top 4
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            grouped.take(4).forEach { category ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(12.dp).clip(CircleShape)
                                            .background(category.color)
                                    )
                                    Column {
                                        Text(category.name, fontSize = 12.sp, maxLines = 1)
                                        Text(
                                            "${String.format("%.1f", category.percentage)}%",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = category.color
                                        )
                                    }
                                }
                            }
                            if (grouped.size > 4) {
                                Text("+${grouped.size - 4} khác", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // Divider + label
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFEEEEEE))
                Spacer(Modifier.height(8.dp))
                Text(
                    "Chi tiết theo danh mục",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (grouped.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center) {
                        Text("Không có dữ liệu", color = Color.Gray)
                    }
                }
            } else {
                items(grouped) { category ->
                    CategoryItem(category)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                }
            }
        }
    }

    // Picker loại Chi tiêu / Thu nhập
    if (showTypePicker) {
        AlertDialog(
            onDismissRequest = { showTypePicker = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = { Text("Chọn loại", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("expense" to "Chi tiêu", "income" to "Thu nhập").forEach { (type, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedType == type)
                                    PrimaryBlue.copy(0.1f) else Color(0xFFF5F5F5))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedType = type
                                    showTypePicker = false
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                                color = if (selectedType == type) PrimaryBlue else Color.DarkGray)
                            if (selectedType == type) {
                                Icon(Icons.Default.Check,
                                    null, tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Picker tháng/năm
    if (showMonthPicker) {
        ChartMonthPickerDialog(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            showMonthSelector = selectedTab == 1,
            onSelect = { month, year ->
                selectedMonth = month
                selectedYear = year
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false }
        )
    }
}

/* =======================
   DONUT CHART THẬT
   ======================= */

@Composable
private fun DonutChart(
    categories: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 28.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val topLeft = androidx.compose.ui.geometry.Offset(
            (size.width - radius * 2) / 2,
            (size.height - radius * 2) / 2
        )
        val arcSize = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)

        var startAngle = -90f
        val gap = 2f

        categories.forEach { category ->
            val sweepAngle = (category.percentage / 100f) * (360f - gap * categories.size)
            drawArc(
                color = category.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            startAngle += sweepAngle + gap
        }

        // Nền vòng tròn xám nhạt nếu chưa full 360
        if (categories.isEmpty()) {
            drawArc(
                color = Color(0xFFEEEEEE),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}

/* =======================
   MONTH PICKER DIALOG
   ======================= */

@Composable
private fun ChartMonthPickerDialog(
    currentMonth: Int,
    currentYear: Int,
    showMonthSelector: Boolean,
    onSelect: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var year by remember { mutableIntStateOf(currentYear) }
    val maxYear = Calendar.getInstance().get(Calendar.YEAR)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { year-- }) {
                    Icon(Icons.Default.ChevronLeft,
                        null, tint = PrimaryBlue)
                }
                Text("$year", fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue)
                IconButton(onClick = { if (year < maxYear) year++ }) {
                    Icon(Icons.Default.ChevronRight, null,
                        tint = if (year < maxYear) PrimaryBlue else Color.LightGray)
                }
            }
        },
        text = {
            Column {
                if (showMonthSelector) {
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(Modifier.height(12.dp))
                    (1..12).chunked(4).forEach { rowMonths ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowMonths.forEach { month ->
                                val isSelected = month == currentMonth && year == currentYear
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) PrimaryBlue else Color(0xFFF5F5F5))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { onSelect(month, year) }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Thg $month", fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Tab Năm — chỉ cần chọn năm
                    Text(
                        "Bấm xác nhận để xem năm $year",
                        color = Color.Gray, fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            if (!showMonthSelector) {
                Button(
                    onClick = { onSelect(currentMonth, year) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Xác nhận", color = Color.White) }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Đóng", color = Color.Gray) }
        }
    )
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
            modifier = Modifier.size(48.dp).clip(CircleShape).background(category.color),
            contentAlignment = Alignment.Center
        ) {
            Icon(category.icon, null, tint = Color.White)
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(category.name, fontSize = 15.sp,
                    fontWeight = FontWeight.Medium)
                Text(formatMoney(category.amount),
                    fontWeight = FontWeight.Medium, color = Color(0xFFE53935))
            }
            Spacer(Modifier.height(6.dp))
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
            Text("${String.format("%.1f", category.percentage)}%",
                fontSize = 12.sp, color = Color.Gray)
        }
    }
}

/* =======================
   UTIL
   ======================= */

private fun formatMoney(value: Long): String {
    return "%,d₫".format(abs(value)).replace(",", ".")
}

private fun formatShort(value: Long): String {
    val abs = abs(value)
    return when {
        abs >= 1_000_000_000 -> String.format("%.1f", abs / 1_000_000_000.0) + "tỷ₫"
        abs >= 1_000_000     -> String.format("%.1f", abs / 1_000_000.0) + "tr₫"
        abs >= 1_000         -> String.format("%.0f", abs / 1_000.0) + "k₫"
        else                 -> "%,d₫".format(abs).replace(",", ".")
    }
}