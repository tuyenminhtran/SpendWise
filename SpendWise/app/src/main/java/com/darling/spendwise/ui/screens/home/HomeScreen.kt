package com.darling.spendwise.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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

private val PrimaryBlue = Color(0xFF1E88E5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: TransactionViewModel) {

    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    var selectedMonth by remember { mutableIntStateOf(currentMonth) }
    var selectedYear by remember { mutableIntStateOf(currentYear) }

    var showMonthPicker by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showCalendar by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<String?>(null) } // "dd/MM/yyyy" hoặc null = tất cả
    var showDrawer by remember { mutableStateOf(false) }

    val transactions by viewModel.transactions.collectAsState()

    // Lọc theo tháng/năm
    val monthFiltered = transactions.filter { transaction ->
        val cal = Calendar.getInstance().apply { timeInMillis = transaction.date }
        cal.get(Calendar.MONTH) + 1 == selectedMonth &&
                cal.get(Calendar.YEAR) == selectedYear
    }

    // Lọc theo ngày cụ thể nếu có
    val dateFiltered = if (selectedDate != null) {
        monthFiltered.filter { tx ->
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(tx.date)) == selectedDate
        }
    } else monthFiltered

    // Lọc theo search
    val filteredTransactions = if (searchQuery.isNotBlank()) {
        dateFiltered.filter { it.note.contains(searchQuery, ignoreCase = true) }
    } else dateFiltered

    val totalExpense = monthFiltered.filter { it.type == "expense" }.sumOf { it.amount }
    val totalIncome = monthFiltered.filter { it.type == "income" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val groupedTransactions = filteredTransactions
        .groupBy { transaction ->
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(transaction.date))
        }
        .toSortedMap(compareByDescending { it })

    // Drawer
    if (showDrawer) {
        MenuDrawer(onDismiss = { showDrawer = false })
    }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(PrimaryBlue)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showDrawer = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Menu, null, tint = Color.White)
                    }
                    Text("SpendWise", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(
                            onClick = {
                                showSearch = !showSearch
                                if (!showSearch) searchQuery = ""
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                if (showSearch) Icons.Default.Close else Icons.Default.Search,
                                null, tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { showCalendar = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                null,
                                tint = if (selectedDate != null) Color(0xFFFFEB3B) else Color.White
                            )
                        }
                    }
                }

                // Search bar
                AnimatedVisibility(visible = showSearch, enter = fadeIn(), exit = fadeOut()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Tìm giao dịch...", color = Color.White.copy(0.7f)) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(0.7f)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, null, tint = Color.White.copy(0.7f))
                                }
                            }
                        }
                    )
                }

                // Filter ngày đang chọn
                if (selectedDate != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(0.2f))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CalendarToday, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Text(selectedDate!!, fontSize = 13.sp, color = Color.White)
                                IconButton(
                                    onClick = { selectedDate = null },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Label row
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text("$selectedYear", color = Color.White.copy(0.8f), fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                    Text("Chi tiêu", color = Color.White.copy(0.8f), fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text("Thu nhập", color = Color.White.copy(0.8f), fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text("Số dư", color = Color.White.copy(0.8f), fontSize = 12.sp, modifier = Modifier.weight(1f))
                }

                // Value row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showMonthPicker = true }
                    ) {
                        Text("Tháng $selectedMonth", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                    }
                    Text(formatShort(totalExpense.toLong()), color = Color(0xFFFFCDD2), fontSize = 11.sp, maxLines = 1, modifier = Modifier.weight(1f))
                    Text(formatShort(totalIncome.toLong()), color = Color(0xFFC8E6C9), fontSize = 11.sp, maxLines = 1, modifier = Modifier.weight(1f))
                    Text(formatShort(balance.toLong()), color = Color.White, fontSize = 11.sp, maxLines = 1, modifier = Modifier.weight(1f))
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFE3F2FD)).padding(16.dp)) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFF1565C0))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Sau khi đăng nhập, bạn có thể sao lưu dữ liệu của mình trong thời gian thực!",
                        color = Color(0xFF1565C0), fontSize = 13.sp
                    )
                }
            }

            if (filteredTransactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 64.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.Receipt,
                                null, tint = Color.LightGray, modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                if (searchQuery.isNotBlank()) "Không tìm thấy \"$searchQuery\""
                                else if (selectedDate != null) "Không có giao dịch ngày $selectedDate"
                                else "Chưa có giao dịch nào",
                                color = Color.Gray, fontSize = 16.sp
                            )
                            if (searchQuery.isBlank() && selectedDate == null) {
                                Text("trong tháng $selectedMonth/$selectedYear", color = Color.LightGray, fontSize = 14.sp)
                            }
                        }
                    }
                }
            } else {
                groupedTransactions.forEach { (date, transactionsOfDay) ->
                    item(key = "header_$date") {
                        val dayTotal = transactionsOfDay.sumOf {
                            if (it.type == "expense") -it.amount else it.amount
                        }
                        val isPositive = dayTotal >= 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    // Bấm vào header ngày để filter theo ngày đó
                                    selectedDate = if (selectedDate == date) null else date
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(date, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                                if (selectedDate == date) {
                                    Box(
                                        modifier = Modifier.size(6.dp).clip(CircleShape).background(PrimaryBlue)
                                    )
                                }
                            }
                            Text(
                                "${if (isPositive) "+" else "-"}${formatMoney(abs(dayTotal.toLong()))}₫",
                                fontSize = 13.sp, fontWeight = FontWeight.Medium,
                                color = if (isPositive) Color(0xFF43A047) else Color(0xFFE53935)
                            )
                        }
                    }

                    items(items = transactionsOfDay, key = { it.id }) { transaction ->
                        SwipeToDeleteItem(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFE0E0E0), thickness = 0.5.dp)
                    }
                }
            }
        }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onSelect = { month, year ->
                selectedMonth = month
                selectedYear = year
                selectedDate = null
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false }
        )
    }

    if (showCalendar) {
        CalendarPickerDialog(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            selectedDate = selectedDate,
            transactions = monthFiltered,
            onSelectDate = { date ->
                selectedDate = if (selectedDate == date) null else date
                showCalendar = false
            },
            onDismiss = { showCalendar = false }
        )
    }
}

/* ================= MENU DRAWER ================= */

@Composable
private fun MenuDrawer(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.75f)
                .background(Color.White)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBlue)
                        .padding(24.dp)
                        .statusBarsPadding()
                ) {
                    Column {
                        Box(
                            modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White.copy(0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Người dùng", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Chưa đăng nhập", fontSize = 13.sp, color = Color.White.copy(0.8f))
                    }
                }

                // Menu items
                val menuItems = listOf(
                    Icons.Default.Home to "Trang chủ",
                    Icons.Default.PieChart to "Biểu đồ",
                    Icons.Default.Description to "Báo cáo",
                    Icons.Default.Person to "Hồ sơ",
                    Icons.Default.Settings to "Cài đặt",
                    Icons.Default.HelpOutline to "Trợ giúp"
                )

                Spacer(Modifier.height(8.dp))
                menuItems.forEach { (icon, title) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDismiss() }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
                        Text(title, fontSize = 15.sp, color = Color(0xFF212121))
                    }
                }

                Spacer(Modifier.weight(1f))

                HorizontalDivider(color = Color(0xFFEEEEEE))

                // Logout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDismiss() }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.Logout, null, tint = Color(0xFFE53935), modifier = Modifier.size(22.dp))
                    Text("Đăng xuất", fontSize = 15.sp, color = Color(0xFFE53935))
                }
            }
        }
    }
}

/* ================= CALENDAR PICKER DIALOG ================= */

@Composable
private fun CalendarPickerDialog(
    currentMonth: Int,
    currentYear: Int,
    selectedDate: String?,
    transactions: List<TransactionEntity>,
    onSelectDate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var month by remember { mutableIntStateOf(currentMonth) }
    var year by remember { mutableIntStateOf(currentYear) }

    // Các ngày có giao dịch trong tháng
    val datesWithTx = transactions
        .map { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.date)) }
        .toSet()

    // Tính ngày đầu tiên trong tháng là thứ mấy
    val firstDayOfMonth = Calendar.getInstance().apply {
        set(year, month - 1, 1)
    }.get(Calendar.DAY_OF_WEEK) - 2 // 0 = Thứ 2

    val daysInMonth = Calendar.getInstance().apply {
        set(year, month - 1, 1)
        add(Calendar.MONTH, 1)
        add(Calendar.DAY_OF_MONTH, -1)
    }.get(Calendar.DAY_OF_MONTH)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (month == 1) { month = 12; year-- } else month--
                }) { Icon(Icons.Default.ChevronLeft, null, tint = PrimaryBlue) }
                Text("$month/$year", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                IconButton(onClick = {
                    if (month == 12) { month = 1; year++ } else month++
                }) { Icon(Icons.Default.ChevronRight, null, tint = PrimaryBlue) }
            }
        },
        text = {
            Column {
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(Modifier.height(8.dp))

                // Header ngày trong tuần
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { day ->
                        Text(
                            day,
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Grid ngày
                val totalCells = firstDayOfMonth.coerceAtLeast(0) + daysInMonth
                val rows = (totalCells + 6) / 7

                repeat(rows) { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        repeat(7) { col ->
                            val cellIndex = row * 7 + col
                            val day = cellIndex - firstDayOfMonth.coerceAtLeast(0) + 1

                            if (day < 1 || day > daysInMonth) {
                                Box(modifier = Modifier.weight(1f).padding(2.dp).aspectRatio(1f))
                            } else {
                                val dateStr = "%02d/%02d/%04d".format(day, month, year)
                                val hasTx = dateStr in datesWithTx
                                val isSelected = selectedDate == dateStr
                                val isToday = Calendar.getInstance().let { cal ->
                                    cal.get(Calendar.DAY_OF_MONTH) == day &&
                                            cal.get(Calendar.MONTH) + 1 == month &&
                                            cal.get(Calendar.YEAR) == year
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> PrimaryBlue
                                                isToday -> PrimaryBlue.copy(0.15f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                            onSelectDate(dateStr)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "$day",
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isSelected -> Color.White
                                                isToday -> PrimaryBlue
                                                else -> Color(0xFF212121)
                                            }
                                        )
                                        if (hasTx && !isSelected) {
                                            Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(PrimaryBlue))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Đóng", color = Color.Gray) }
        }
    )
}

/* ================= MONTH PICKER DIALOG ================= */

@Composable
private fun MonthPickerDialog(
    currentMonth: Int,
    currentYear: Int,
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
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Đóng", color = Color.Gray) } }
    )
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
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true } else false
        },
        positionalThreshold = { it * 0.4f }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFE53935)).padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Delete, "Xóa", tint = Color.White, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("Xóa", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    ) { TransactionItem(transaction) }
}

/* ================= TRANSACTION ITEM ================= */

@Composable
private fun TransactionItem(transaction: TransactionEntity) {

    val categoryIcons = mapOf(
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
        111 to Icons.Default.Replay,           112 to Icons.Default.MoreHoriz,
        201 to Icons.Default.Savings,              202 to Icons.Default.CreditCard,
        203 to Icons.Default.PersonAdd,            204 to Icons.Default.AttachMoney,
        205 to Icons.Default.AccountBalanceWallet, 206 to Icons.Default.LocalAtm,
        207 to Icons.Default.SwapHoriz,            208 to Icons.Default.MoreHoriz
    )

    val categoryColors = mapOf(
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
        111 to Color(0xFF5E35B1), 112 to Color(0xFF757575),
        201 to Color(0xFFFF8F00), 202 to Color(0xFFE53935),
        203 to Color(0xFF1E88E5), 204 to Color(0xFF43A047),
        205 to Color(0xFF8E24AA), 206 to Color(0xFF6D4C41),
        207 to Color(0xFF00ACC1), 208 to Color(0xFF757575)
    )

    val icon = categoryIcons[transaction.categoryId] ?: Icons.Default.AttachMoney
    val color = categoryColors[transaction.categoryId] ?: Color(0xFFFFA726)
    val isExpense = transaction.type == "expense"
    val isTransfer = transaction.type == "transfer"
    val amountColor = when {
        isExpense  -> Color(0xFFE53935)
        isTransfer -> Color(0xFFFF8F00)
        else       -> Color(0xFF43A047)
    }

    val timeFormatted = remember(transaction.date) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(transaction.date))
    }

    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color.White)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.note, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    when (transaction.type) {
                        "expense"  -> "Chi tiêu"
                        "income"   -> "Thu nhập"
                        else       -> "Chuyển khoản"
                    },
                    fontSize = 12.sp, color = amountColor
                )
                Text("•", fontSize = 12.sp, color = Color.LightGray)
                Text(timeFormatted, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Text(
            "${if (isExpense) "-" else if (isTransfer) "" else "+"}${formatMoney(transaction.amount.toLong())}₫",
            fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = amountColor
        )
    }
}

/* ================= FORMAT MONEY ================= */

private fun formatMoney(value: Long): String {
    return "%,d".format(abs(value)).replace(",", ".")
}

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