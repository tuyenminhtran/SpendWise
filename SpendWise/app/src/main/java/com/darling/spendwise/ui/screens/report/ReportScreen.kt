package com.darling.spendwise.ui.screens.report

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darling.spendwise.viewModel.TransactionViewModel
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.max

private val Primary       = Color(0xFF1565C0)
private val PrimaryLight  = Color(0xFF1E88E5)
private val ExpenseRed    = Color(0xFFE53935)
private val IncomeGreen   = Color(0xFF43A047)
private val BgColor       = Color(0xFFF0F4F8)
private val CardColor     = Color.White
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val DividerColor  = Color(0xFFEEF2F7)

private val HeaderGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1565C0), Color(0xFF1E88E5), Color(0xFF42A5F5))
)

private val categoryNames = mapOf(
    1  to "Mua sắm",   2  to "Đồ ăn",     3  to "Điện thoại",
    4  to "Giải trí",  5  to "Giáo dục",  6  to "Sắc đẹp",
    7  to "Thể thao",  8  to "Xã hội",    9  to "Đi lại",
    10 to "Quần áo",   11 to "Ô tô",      12 to "Rượu",
    13 to "Thuốc lá",  14 to "Thiết bị",  15 to "Du lịch",
    16 to "Sức khỏe",
    101 to "Lương",    102 to "Thưởng",   103 to "Đầu tư",
    104 to "Bán hàng", 105 to "Cho vay",  106 to "Học bổng",
    107 to "Phụ cấp",  108 to "Freelance",109 to "Cổ tức",
    110 to "Tiền thuê",111 to "Hoàn tiền",112 to "Khác"
)

private val categoryIcons = mapOf(
    1  to Icons.Default.ShoppingCart, 2  to Icons.Default.Restaurant,
    3  to Icons.Default.Smartphone,   4  to Icons.Default.SportsEsports,
    5  to Icons.Default.School,       6  to Icons.Default.ContentCut,
    7  to Icons.Default.FitnessCenter,8  to Icons.Default.People,
    9  to Icons.Default.DirectionsBus,10 to Icons.Default.Checkroom,
    11 to Icons.Default.DirectionsCar,12 to Icons.Default.LocalBar,
    13 to Icons.Default.SmokingRooms, 14 to Icons.Default.Computer,
    15 to Icons.Default.Flight,       16 to Icons.Default.FavoriteBorder,
    101 to Icons.Default.AccountBalance,   102 to Icons.Default.EmojiEvents,
    103 to Icons.Default.TrendingUp,       104 to Icons.Default.Storefront,
    105 to Icons.Default.VolunteerActivism,106 to Icons.Default.School,
    107 to Icons.Default.CardGiftcard,     108 to Icons.Default.Laptop,
    109 to Icons.Default.ShowChart,        110 to Icons.Default.Home,
    111 to Icons.Default.Replay,           112 to Icons.Default.MoreHoriz
)

private val categoryColors = mapOf(
    1  to Color(0xFFFB8C00), 2  to Color(0xFF43A047),
    3  to Color(0xFF1E88E5), 4  to Color(0xFF8E24AA),
    5  to Color(0xFF00ACC1), 6  to Color(0xFFE91E63),
    7  to Color(0xFF3949AB), 8  to Color(0xFF00897B),
    9  to Color(0xFFFFB300), 10 to Color(0xFF6D4C41),
    11 to Color(0xFF546E7A), 12 to Color(0xFFD81B60),
    13 to Color(0xFF757575), 14 to Color(0xFF5E35B1),
    15 to Color(0xFF039BE5), 16 to Color(0xFFE53935),
    101 to Color(0xFF43A047), 102 to Color(0xFF00ACC1),
    103 to Color(0xFF1E88E5), 104 to Color(0xFFFB8C00),
    105 to Color(0xFF8E24AA), 106 to Color(0xFF039BE5),
    107 to Color(0xFFFFB300), 108 to Color(0xFF3949AB),
    109 to Color(0xFF00897B), 110 to Color(0xFFE91E63),
    111 to Color(0xFF5E35B1), 112 to Color(0xFF757575)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(viewModel: TransactionViewModel) {
    val now = Calendar.getInstance()
    var selectedMonth by remember { mutableIntStateOf(now.get(Calendar.MONTH) + 1) }
    var selectedYear  by remember { mutableIntStateOf(now.get(Calendar.YEAR)) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Chi tiêu, 1 = Thu nhập

    val transactions by viewModel.transactions.collectAsState()

    // Filter tháng đang chọn
    val monthFiltered = transactions.filter { tx ->
        val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
        cal.get(Calendar.MONTH) + 1 == selectedMonth && cal.get(Calendar.YEAR) == selectedYear
    }

    val totalExpense = monthFiltered.filter { it.type == "expense" }.sumOf { it.amount }
    val totalIncome  = monthFiltered.filter { it.type == "income"  }.sumOf { it.amount }
    val balance      = totalIncome - totalExpense

    // Top 5 danh mục theo tab
    val topCategories = monthFiltered
        .filter { it.type == if (selectedTab == 0) "expense" else "income" }
        .groupBy { it.categoryId }
        .map { (id, list) -> id to list.sumOf { it.amount } }
        .sortedByDescending { it.second }
        .take(5)

    val maxCategoryAmount = topCategories.maxOfOrNull { it.second } ?: 1.0

    // Dữ liệu 6 tháng gần nhất cho biểu đồ cột
    val last6Months = (5 downTo 0).map { offset ->
        val cal = Calendar.getInstance().apply {
            set(Calendar.MONTH, now.get(Calendar.MONTH) - offset)
            set(Calendar.YEAR, now.get(Calendar.YEAR))
        }
        val m = cal.get(Calendar.MONTH) + 1
        val y = cal.get(Calendar.YEAR)
        val monthTx = transactions.filter { tx ->
            val c = Calendar.getInstance().apply { timeInMillis = tx.date }
            c.get(Calendar.MONTH) + 1 == m && c.get(Calendar.YEAR) == y
        }
        Triple(
            "T$m",
            monthTx.filter { it.type == "expense" }.sumOf { it.amount },
            monthTx.filter { it.type == "income"  }.sumOf { it.amount }
        )
    }

    val maxBarValue = last6Months.maxOf { max(it.second, it.third) }.coerceAtLeast(1.0)

    Scaffold(
        containerColor = BgColor,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(brush = HeaderGradient)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Báo cáo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /* ===== Chọn tháng ===== */
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardColor)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            showMonthPicker = true
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Primary.copy(0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CalendarMonth, null, tint = Primary, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            "Tháng $selectedMonth/$selectedYear",
                            fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary
                        )
                    }
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }

            /* ===== Tổng quan tháng ===== */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardColor)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Tổng quan tháng $selectedMonth", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    HorizontalDivider(color = DividerColor)

                    // 3 số liệu
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryCard("Chi tiêu", totalExpense.toLong(), ExpenseRed, Modifier.weight(1f))
                        SummaryCard("Thu nhập", totalIncome.toLong(), IncomeGreen, Modifier.weight(1f))
                        SummaryCard("Số dư", balance.toLong(), if (balance >= 0) IncomeGreen else ExpenseRed, Modifier.weight(1f))
                    }

                    // Progress bar chi tiêu / thu nhập
                    if (totalIncome > 0 || totalExpense > 0) {
                        val total = totalIncome + totalExpense
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tỉ lệ chi tiêu", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    if (total > 0) "${String.format("%.1f", totalExpense / total * 100)}%" else "0%",
                                    fontSize = 12.sp, color = ExpenseRed, fontWeight = FontWeight.Medium
                                )
                            }
                            Box(
                                modifier = Modifier.fillMaxWidth().height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)).background(IncomeGreen.copy(0.2f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (total > 0) (totalExpense / total).toFloat() else 0f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ExpenseRed)
                                )
                            }
                        }
                    }
                }
            }

            /* ===== Biểu đồ cột 6 tháng ===== */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardColor)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("6 tháng gần nhất", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    HorizontalDivider(color = DividerColor)

                    // Legend
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ExpenseRed))
                            Text("Chi tiêu", fontSize = 11.sp, color = TextSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(IncomeGreen))
                            Text("Thu nhập", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    // Bars
                    Row(
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        last6Months.forEach { (label, expense, income) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier.height(120.dp)
                                ) {
                                    // Cột chi tiêu
                                    val expenseRatio = (expense / maxBarValue).toFloat()
                                    Box(
                                        modifier = Modifier
                                            .width(10.dp)
                                            .fillMaxHeight(expenseRatio.coerceIn(0.02f, 1f))
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(ExpenseRed)
                                    )
                                    // Cột thu nhập
                                    val incomeRatio = (income / maxBarValue).toFloat()
                                    Box(
                                        modifier = Modifier
                                            .width(10.dp)
                                            .fillMaxHeight(incomeRatio.coerceIn(0.02f, 1f))
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(IncomeGreen)
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(label, fontSize = 10.sp, color = TextSecondary, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }

            /* ===== Top danh mục ===== */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardColor)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tab Chi tiêu / Thu nhập
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Top danh mục", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BgColor)
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            listOf("Chi tiêu", "Thu nhập").forEachIndexed { index, label ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selectedTab == index) CardColor else Color.Transparent)
                                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { selectedTab = index }
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        label, fontSize = 11.sp,
                                        color = if (selectedTab == index) Primary else TextSecondary,
                                        fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = DividerColor)

                    if (topCategories.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.BarChart, null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Chưa có dữ liệu", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    } else {
                        topCategories.forEachIndexed { index, (categoryId, amount) ->
                            val name  = categoryNames[categoryId] ?: "Khác"
                            val icon  = categoryIcons[categoryId] ?: Icons.Default.Category
                            val color = categoryColors[categoryId] ?: Color.Gray
                            val ratio = (amount / maxCategoryAmount).toFloat()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Rank
                                Text(
                                    "${index + 1}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (index == 0) Primary else TextSecondary,
                                    modifier = Modifier.width(16.dp),
                                    textAlign = TextAlign.Center
                                )

                                // Icon
                                Box(
                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                                        Text(
                                            formatMoney(amount.toLong()),
                                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                            color = if (selectedTab == 0) ExpenseRed else IncomeGreen
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(5.dp)
                                            .clip(RoundedCornerShape(3.dp)).background(color.copy(0.12f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(ratio)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(color)
                                        )
                                    }
                                }
                            }

                            if (index < topCategories.size - 1) {
                                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            /* ===== Giao dịch gần đây ===== */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardColor)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text("Giao dịch gần đây", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(top = 12.dp))

                    val recent = monthFiltered.sortedByDescending { it.date }.take(5)

                    if (recent.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Chưa có giao dịch", color = TextSecondary, fontSize = 13.sp)
                        }
                    } else {
                        recent.forEach { tx ->
                            val isExpense = tx.type == "expense"
                            val color = categoryColors[tx.categoryId] ?: Color.Gray
                            val icon  = categoryIcons[tx.categoryId] ?: Icons.Default.AttachMoney

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                                }
                                Text(tx.note, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.weight(1f), maxLines = 1)
                                Text(
                                    "${if (isExpense) "-" else "+"}${formatMoney(tx.amount.toLong())}₫",
                                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                    color = if (isExpense) ExpenseRed else IncomeGreen
                                )
                            }
                            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }

    // Month picker
    if (showMonthPicker) {
        ReportMonthPickerDialog(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onSelect = { month, year ->
                selectedMonth = month
                selectedYear = year
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false }
        )
    }
}

/* ================= SUMMARY CARD ================= */

@Composable
private fun SummaryCard(label: String, value: Long, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(0.08f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontSize = 11.sp, color = color.copy(0.8f), fontWeight = FontWeight.Medium)
        Text(
            formatShort(value),
            fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1
        )
    }
}

/* ================= MONTH PICKER ================= */

@Composable
private fun ReportMonthPickerDialog(
    currentMonth: Int,
    currentYear: Int,
    onSelect: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var year by remember { mutableIntStateOf(currentYear) }
    val maxYear = Calendar.getInstance().get(Calendar.YEAR)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardColor,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { year-- }) { Icon(Icons.Default.ChevronLeft, null, tint = Primary) }
                Text("$year", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
                IconButton(onClick = { if (year < maxYear) year++ }) {
                    Icon(Icons.Default.ChevronRight, null, tint = if (year < maxYear) Primary else Color.LightGray)
                }
            }
        },
        text = {
            Column {
                HorizontalDivider(color = DividerColor)
                Spacer(Modifier.height(12.dp))
                (1..12).chunked(4).forEach { rowMonths ->
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowMonths.forEach { month ->
                            val isSel = month == currentMonth && year == currentYear
                            Box(
                                modifier = Modifier
                                    .weight(1f).clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) Primary else BgColor)
                                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelect(month, year) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Thg $month", fontSize = 13.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Đóng", color = TextSecondary) } }
    )
}

/* ================= UTILS ================= */

private fun formatMoney(value: Long): String =
    "%,d".format(abs(value)).replace(",", ".")

private fun formatShort(value: Long): String {
    val sign = if (value < 0) "-" else ""
    val abs = abs(value)
    return sign + when {
        abs >= 1_000_000_000 -> String.format("%.1f", abs / 1_000_000_000.0) + "tỷ"
        abs >= 1_000_000     -> String.format("%.1f", abs / 1_000_000.0) + "tr"
        abs >= 1_000         -> String.format("%.0f", abs / 1_000.0) + "k"
        else                 -> "%,d".format(abs).replace(",", ".")
    }
}