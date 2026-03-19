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
    // Chi tiêu
    1  to Color(0xFFFB8C00), 2  to Color(0xFF43A047),
    3  to Color(0xFF1E88E5), 4  to Color(0xFF8E24AA),
    5  to Color(0xFF00ACC1), 6  to Color(0xFFE91E63),
    7  to Color(0xFF3949AB), 8  to Color(0xFF00897B),
    9  to Color(0xFFFFB300), 10 to Color(0xFF6D4C41),
    11 to Color(0xFF546E7A), 12 to Color(0xFFD81B60),
    13 to Color(0xFF757575), 14 to Color(0xFF5E35B1),
    15 to Color(0xFF039BE5), 16 to Color(0xFFE53935),
    // Thu nhập
    101 to Color(0xFF43A047), 102 to Color(0xFF00ACC1),
    103 to Color(0xFF1E88E5), 104 to Color(0xFFFB8C00),
    105 to Color(0xFF8E24AA), 106 to Color(0xFF039BE5),
    107 to Color(0xFFFFB300), 108 to Color(0xFF3949AB),
    109 to Color(0xFF00897B), 110 to Color(0xFFE91E63),
    111 to Color(0xFF5E35B1), 112 to Color(0xFF757575),
    // Chuyển khoản
    201 to Color(0xFFFF8F00), 202 to Color(0xFFE53935),
    203 to Color(0xFF1E88E5), 204 to Color(0xFF43A047),
    205 to Color(0xFF8E24AA), 206 to Color(0xFF6D4C41),
    207 to Color(0xFF00ACC1), 208 to Color(0xFF757575)
)

private val categoryIcons = mapOf(
    // Chi tiêu
    1  to Icons.Default.ShoppingCart, 2  to Icons.Default.Restaurant,
    3  to Icons.Default.Smartphone,   4  to Icons.Default.SportsEsports,
    5  to Icons.Default.School,       6  to Icons.Default.ContentCut,
    7  to Icons.Default.FitnessCenter,8  to Icons.Default.People,
    9  to Icons.Default.DirectionsBus,10 to Icons.Default.Checkroom,
    11 to Icons.Default.DirectionsCar,12 to Icons.Default.LocalBar,
    13 to Icons.Default.SmokingRooms, 14 to Icons.Default.Computer,
    15 to Icons.Default.Flight,       16 to Icons.Default.FavoriteBorder,
    // Thu nhập
    101 to Icons.Default.AccountBalance,   102 to Icons.Default.EmojiEvents,
    103 to Icons.Default.TrendingUp,       104 to Icons.Default.Storefront,
    105 to Icons.Default.VolunteerActivism,106 to Icons.Default.School,
    107 to Icons.Default.CardGiftcard,     108 to Icons.Default.Laptop,
    109 to Icons.Default.ShowChart,        110 to Icons.Default.Home,
    111 to Icons.Default.Replay,           112 to Icons.Default.MoreHoriz,
    // Chuyển khoản
    201 to Icons.Default.Savings,              202 to Icons.Default.CreditCard,
    203 to Icons.Default.PersonAdd,            204 to Icons.Default.AttachMoney,
    205 to Icons.Default.AccountBalanceWallet, 206 to Icons.Default.LocalAtm,
    207 to Icons.Default.SwapHoriz,            208 to Icons.Default.MoreHoriz
)

private val categoryNames = mapOf(
    // Chi tiêu
    1  to "Mua sắm",   2  to "Đồ ăn",      3  to "Điện thoại",
    4  to "Giải trí",  5  to "Giáo dục",   6  to "Sắc đẹp",
    7  to "Thể thao",  8  to "Xã hội",     9  to "Đi lại",
    10 to "Quần áo",   11 to "Ô tô",       12 to "Rượu",
    13 to "Thuốc lá",  14 to "Thiết bị",   15 to "Du lịch",
    16 to "Sức khỏe",
    // Thu nhập
    101 to "Lương",     102 to "Thưởng",    103 to "Đầu tư",
    104 to "Bán hàng",  105 to "Cho vay",   106 to "Học bổng",
    107 to "Phụ cấp",   108 to "Freelance", 109 to "Cổ tức",
    110 to "Tiền thuê", 111 to "Hoàn tiền", 112 to "Khác",
    // Chuyển khoản
    201 to "Tiết kiệm", 202 to "Trả nợ",    203 to "Cho mượn",
    204 to "Thu nợ",    205 to "Nạp ví",    206 to "Rút tiền",
    207 to "Chuyển khoản", 208 to "Khác"
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
    var selectedTab by remember { mutableIntStateOf(1) }
    var showTypePicker by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("expense") }

    val now = Calendar.getInstance()
    var selectedMonth by remember { mutableIntStateOf(now.get(Calendar.MONTH) + 1) }
    var selectedYear by remember { mutableIntStateOf(now.get(Calendar.YEAR)) }
    var selectedWeek by remember { mutableIntStateOf(now.get(Calendar.WEEK_OF_YEAR)) }
    var selectedWeekYear by remember { mutableIntStateOf(now.get(Calendar.YEAR)) }

    var showMonthPicker by remember { mutableStateOf(false) }
    var showWeekPicker by remember { mutableStateOf(false) }

    val transactions by viewModel.transactions.collectAsState()

    val filtered = transactions.filter { tx ->
        val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
        val matchType = tx.type == selectedType
        val matchPeriod = when (selectedTab) {
            0 -> cal.get(Calendar.WEEK_OF_YEAR) == selectedWeek && cal.get(Calendar.YEAR) == selectedWeekYear
            1 -> cal.get(Calendar.MONTH) + 1 == selectedMonth && cal.get(Calendar.YEAR) == selectedYear
            else -> cal.get(Calendar.YEAR) == selectedYear
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

    val accentColor = when (selectedType) {
        "income"   -> Color(0xFF43A047)
        "transfer" -> Color(0xFFFF8F00)
        else       -> Color(0xFFE53935)
    }
    val typeLabel = when (selectedType) {
        "income"   -> "Thu nhập"
        "transfer" -> "Chuyển khoản"
        else       -> "Chi tiêu"
    }

    val weekLabel = remember(selectedWeek, selectedWeekYear) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedWeekYear)
            set(Calendar.WEEK_OF_YEAR, selectedWeek)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val start = "%02d/%02d".format(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1)
        cal.add(Calendar.DAY_OF_WEEK, 6)
        val end = "%02d/%02d".format(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1)
        "$start-$end"
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(PrimaryBlue)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showTypePicker = true }
                    ) {
                        Text(typeLabel, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { when (selectedTab) { 0 -> showWeekPicker = true; else -> showMonthPicker = true } }
                    ) {
                        Text(
                            text = when (selectedTab) {
                                0    -> weekLabel
                                1    -> "$selectedMonth/$selectedYear"
                                else -> "$selectedYear"
                            },
                            fontSize = 13.sp, color = Color.White
                        )
                        Icon(Icons.Default.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

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
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { selectedTab = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text, color = if (selectedTab == index) PrimaryBlue else Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp)
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
            item {
                if (total == 0L) {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PieChart, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("Chưa có dữ liệu", color = Color.Gray, fontSize = 16.sp)
                            Text(
                                when (selectedTab) {
                                    0    -> "tuần $weekLabel"
                                    1    -> "tháng $selectedMonth/$selectedYear"
                                    else -> "năm $selectedYear"
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
                        Box(modifier = Modifier.size(180.dp), contentAlignment = Alignment.Center) {
                            DonutChart(categories = grouped, modifier = Modifier.size(180.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(formatShort(total), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = accentColor)
                                Text(typeLabel, fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            grouped.take(4).forEach { category ->
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(category.color))
                                    Column {
                                        Text(category.name, fontSize = 12.sp, maxLines = 1)
                                        Text("${String.format("%.1f", category.percentage)}%",
                                            fontWeight = FontWeight.Bold, fontSize = 12.sp, color = category.color)
                                    }
                                }
                            }
                            if (grouped.size > 4) Text("+${grouped.size - 4} khác", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFEEEEEE))
                Spacer(Modifier.height(8.dp))
                Text("Chi tiết theo danh mục",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Gray)
            }

            if (grouped.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Không có dữ liệu", color = Color.Gray)
                    }
                }
            } else {
                items(grouped) { category ->
                    CategoryItem(category = category, accentColor = accentColor)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                }
            }
        }
    }

    // Picker loại
    if (showTypePicker) {
        AlertDialog(
            onDismissRequest = { showTypePicker = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = { Text("Chọn loại", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "expense"  to "Chi tiêu",
                        "income"   to "Thu nhập",
                        "transfer" to "Chuyển khoản"
                    ).forEach { (type, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedType == type) PrimaryBlue.copy(0.1f) else Color(0xFFF5F5F5))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    selectedType = type; showTypePicker = false
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                                color = if (selectedType == type) PrimaryBlue else Color.DarkGray)
                            if (selectedType == type) Icon(Icons.Default.Check, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showMonthPicker) {
        ChartMonthPickerDialog(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            showMonthSelector = selectedTab == 1,
            onSelect = { month, year -> selectedMonth = month; selectedYear = year; showMonthPicker = false },
            onDismiss = { showMonthPicker = false }
        )
    }

    if (showWeekPicker) {
        WeekPickerDialog(
            currentWeek = selectedWeek,
            currentYear = selectedWeekYear,
            onSelect = { week, year -> selectedWeek = week; selectedWeekYear = year; showWeekPicker = false },
            onDismiss = { showWeekPicker = false }
        )
    }
}

/* =======================
   DONUT CHART
   ======================= */

@Composable
private fun DonutChart(categories: List<CategoryExpense>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 28.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val topLeft = androidx.compose.ui.geometry.Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
        val arcSize = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        var startAngle = -90f
        val gap = 2f

        if (categories.isEmpty()) {
            drawArc(color = Color(0xFFEEEEEE), startAngle = 0f, sweepAngle = 360f,
                useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(width = strokeWidth))
        } else {
            categories.forEach { category ->
                val sweepAngle = (category.percentage / 100f) * (360f - gap * categories.size)
                drawArc(color = category.color, startAngle = startAngle, sweepAngle = sweepAngle,
                    useCenter = false, topLeft = topLeft, size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt))
                startAngle += sweepAngle + gap
            }
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { year-- }) { Icon(Icons.Default.ChevronLeft, null, tint = PrimaryBlue) }
                Text("$year", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                IconButton(onClick = { if (year < maxYear) year++ }) {
                    Icon(Icons.Default.ChevronRight, null, tint = if (year < maxYear) PrimaryBlue else Color.LightGray)
                }
            }
        },
        text = {
            Column {
                if (showMonthSelector) {
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(Modifier.height(12.dp))
                    (1..12).chunked(4).forEach { rowMonths ->
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowMonths.forEach { month ->
                                val isSelected = month == currentMonth && year == currentYear
                                Box(
                                    modifier = Modifier
                                        .weight(1f).clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PrimaryBlue else Color(0xFFF5F5F5))
                                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelect(month, year) }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Thg $month", fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Color.DarkGray)
                                }
                            }
                        }
                    }
                } else {
                    Text("Bấm xác nhận để xem năm $year", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        },
        confirmButton = {
            if (!showMonthSelector) {
                Button(onClick = { onSelect(currentMonth, year) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)) { Text("Xác nhận", color = Color.White) }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Đóng", color = Color.Gray) } }
    )
}

/* =======================
   WEEK PICKER DIALOG
   ======================= */

@Composable
private fun WeekPickerDialog(
    currentWeek: Int,
    currentYear: Int,
    onSelect: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var year by remember { mutableIntStateOf(currentYear) }
    var week by remember { mutableIntStateOf(currentWeek) }
    val maxYear = Calendar.getInstance().get(Calendar.YEAR)
    val maxWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)

    val totalWeeks = remember(year) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
        }.get(Calendar.WEEK_OF_YEAR).let { if (it == 1) 52 else it }
    }

    val weekLabel = remember(week, year) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.WEEK_OF_YEAR, week)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val start = "%02d/%02d".format(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1)
        cal.add(Calendar.DAY_OF_WEEK, 6)
        val end = "%02d/%02d".format(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1)
        "$start - $end"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { year--; week = 1 }) { Icon(Icons.Default.ChevronLeft, null, tint = PrimaryBlue) }
                Text("$year", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                IconButton(onClick = { if (year < maxYear) { year++; week = 1 } }) {
                    Icon(Icons.Default.ChevronRight, null, tint = if (year < maxYear) PrimaryBlue else Color.LightGray)
                }
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (week > 1) week-- }) {
                        Icon(Icons.Default.ChevronLeft, null, tint = if (week > 1) PrimaryBlue else Color.LightGray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tuần $week", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(weekLabel, fontSize = 13.sp, color = Color.Gray)
                    }
                    val canNext = year < maxYear || (year == maxYear && week < maxWeek)
                    IconButton(onClick = { if (canNext && week < totalWeeks) week++ }) {
                        Icon(Icons.Default.ChevronRight, null, tint = if (canNext && week < totalWeeks) PrimaryBlue else Color.LightGray)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Button(onClick = { onSelect(week, year) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(10.dp)) { Text("Chọn", color = Color.White) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Đóng", color = Color.Gray) } }
    )
}

/* =======================
   CATEGORY ITEM
   ======================= */

@Composable
private fun CategoryItem(category: CategoryExpense, accentColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(category.color), contentAlignment = Alignment.Center) {
            Icon(category.icon, null, tint = Color.White)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(category.name, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(formatMoney(category.amount), fontWeight = FontWeight.Medium, color = accentColor)
            }
            Spacer(Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFEEEEEE))) {
                Box(modifier = Modifier.fillMaxWidth(category.percentage / 100f).fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(category.color))
            }
            Spacer(Modifier.height(4.dp))
            Text("${String.format("%.1f", category.percentage)}%", fontSize = 12.sp, color = Color.Gray)
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