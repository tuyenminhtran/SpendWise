package com.darling.spendwise.ui.screens.home

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darling.spendwise.data.local.entity.TransactionEntity
import com.darling.spendwise.viewModel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: TransactionViewModel) {

    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    var selectedMonth by remember { mutableIntStateOf(currentMonth) }
    var selectedYear by remember { mutableIntStateOf(currentYear) }
    var showMonthPicker by remember { mutableStateOf(false) }

    val transactions by viewModel.transactions.collectAsState()

    val filteredTransactions = transactions.filter { transaction ->
        val cal = Calendar.getInstance().apply { timeInMillis = transaction.date }
        cal.get(Calendar.MONTH) + 1 == selectedMonth &&
                cal.get(Calendar.YEAR) == selectedYear
    }

    val totalExpense = filteredTransactions
        .filter { it.type == "expense" }
        .sumOf { it.amount }
    val totalIncome = filteredTransactions
        .filter { it.type == "income" }
        .sumOf { it.amount }
    val balance = totalIncome - totalExpense

    // Group theo ngày, ngày mới nhất lên trên
    val groupedTransactions = filteredTransactions
        .groupBy { transaction ->
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(Date(transaction.date))
        }
        .toSortedMap(compareByDescending { it })

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E88E5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Menu, null, tint = Color.White)
                    Text(
                        "SpendWise",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Search, null, tint = Color.White)
                        Icon(Icons.Default.CalendarMonth, null, tint = Color.White)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    Text("$selectedYear", color = Color.White.copy(0.8f),
                        fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                    Text("Chi tiêu", color = Color.White.copy(0.8f),
                        fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text("Thu nhập", color = Color.White.copy(0.8f),
                        fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text("Số dư", color = Color.White.copy(0.8f),
                        fontSize = 12.sp, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showMonthPicker = true }
                    ) {
                        Text(
                            "Tháng $selectedMonth",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(Icons.Default.ArrowDropDown,
                            null, tint = Color.White)
                    }

                    Text(
                        text = formatShort(totalExpense.toLong()),
                        color = Color(0xFFFFCDD2),
                        fontSize = 11.sp,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatShort(totalIncome.toLong()),
                        color = Color(0xFFC8E6C9),
                        fontSize = 11.sp,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatShort(balance.toLong()),
                        color = Color.White,
                        fontSize = 11.sp,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
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
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD))
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Info,
                        null, tint = Color(0xFF1565C0))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Sau khi đăng nhập, bạn có thể sao lưu dữ liệu của mình trong thời gian thực!",
                        color = Color(0xFF1565C0),
                        fontSize = 13.sp
                    )
                }
            }

            if (filteredTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Receipt,
                                null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text("Chưa có giao dịch nào", color = Color.Gray, fontSize = 16.sp)
                            Text(
                                "trong tháng $selectedMonth/$selectedYear",
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                groupedTransactions.forEach { (date, transactionsOfDay) ->

                    // Header ngày + tổng trong ngày
                    item(key = "header_$date") {
                        val dayTotal = transactionsOfDay.sumOf {
                            if (it.type == "expense") -it.amount else it.amount
                        }
                        val isPositive = dayTotal >= 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = date,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = "${if (isPositive) "+" else "-"}${formatMoney(abs(dayTotal.toLong()))}₫",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isPositive) Color(0xFF43A047) else Color(0xFFE53935)
                            )
                        }
                    }

                    // Transactions của ngày
                    items(
                        items = transactionsOfDay,
                        key = { it.id }
                    ) { transaction ->
                        SwipeToDeleteItem(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color(0xFFE0E0E0),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }

    if (showMonthPicker) {
        MonthPickerSheet(
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

/* ================= MONTH PICKER ================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthPickerSheet(
    currentMonth: Int,
    currentYear: Int,
    onSelect: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var year by remember { mutableIntStateOf(currentYear) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { year-- }) {
                    Icon(Icons.Default.ChevronLeft, null, tint = Color(0xFF1E88E5))
                }
                Text("$year", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { if (year < currentYear) year++ }) {
                    Icon(
                        Icons.Default.ChevronRight,
                        null,
                        tint = if (year < currentYear) Color(0xFF1E88E5) else Color.LightGray
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(Modifier.height(16.dp))

            (1..12).toList().chunked(4).forEach { rowMonths ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowMonths.forEach { month ->
                        val isSelected = month == currentMonth && year == currentYear
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) Color(0xFF1E88E5) else Color(0xFFF5F5F5)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onSelect(month, year) }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Thg $month",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ================= SWIPE TO DELETE ================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteItem(
    transaction: TransactionEntity,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        },
        positionalThreshold = { it * 0.4f }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE53935))
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Xóa", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    ) {
        TransactionItem(transaction)
    }
}

/* ================= TRANSACTION ITEM ================= */

@Composable
private fun TransactionItem(transaction: TransactionEntity) {

    val categoryIcons = mapOf(
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

    val categoryColors = mapOf(
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

    val icon = categoryIcons[transaction.categoryId] ?: Icons.Default.AttachMoney
    val color = categoryColors[transaction.categoryId] ?: Color(0xFFFFA726)
    val isExpense = transaction.type == "expense"

    val timeFormatted = remember(transaction.date) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(transaction.date))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color.White)
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.note,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpense) "Chi tiêu" else "Thu nhập",
                    fontSize = 12.sp,
                    color = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)
                )
                Text("•", fontSize = 12.sp, color = Color.LightGray)
                // Chỉ hiện giờ vì đã có header ngày rồi
                Text(text = timeFormatted, fontSize = 12.sp, color = Color.Gray)
            }
        }

        Text(
            text = "${if (isExpense) "-" else "+"}${formatMoney(transaction.amount.toLong())}₫",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)
        )
    }
}

/* ================= FORMAT MONEY ================= */

// Số đầy đủ cho transaction item
private fun formatMoney(value: Long): String {
    return "%,d".format(abs(value)).replace(",", ".")
}

// Số rút gọn cho header
private fun formatShort(value: Long): String {

    val sign = if (value < 0) "-" else ""
    val absValue = abs(value)

    val formatted = when {
        absValue >= 1_000_000_000 -> String.format("%.1f", absValue / 1_000_000_000.0) + "tỷ"
        absValue >= 1_000_000     -> String.format("%.1f", absValue / 1_000_000.0) + "tr"
        absValue >= 1_000         -> String.format("%.0f", absValue / 1_000.0) + "k"
        else -> "%,d".format(absValue).replace(",", ".")
    }

    return sign + formatted
}