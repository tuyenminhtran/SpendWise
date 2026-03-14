package com.darling.spendwise.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: TransactionViewModel) {

    var selectedMonth by remember { mutableStateOf("Tháng 1") }
    val transactions by viewModel.transactions.collectAsState()

    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

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
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("2026", color = Color.White.copy(0.8f))
                    Text("Chi tiêu", color = Color.White.copy(0.8f))
                    Text("Thu nhập", color = Color.White.copy(0.8f))
                    Text("Số dư", color = Color.White.copy(0.8f))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            selectedMonth,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                    }
                    Text(formatMoney(totalExpense.toLong()), color = Color(0xFFFFCDD2))
                    Text(formatMoney(totalIncome.toLong()), color = Color(0xFFC8E6C9))
                    Text(formatMoney(balance.toLong()), color = Color.White)
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
                    Icon(Icons.Default.Info, null, tint = Color(0xFF1565C0))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Sau khi đăng nhập, bạn có thể sao lưu dữ liệu của mình trong thời gian thực!",
                        color = Color(0xFF1565C0),
                        fontSize = 13.sp
                    )
                }
            }

            items(
                items = transactions,
                key = { it.id }  // quan trọng để animation swipe hoạt động đúng
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
                        imageVector = Icons.Default.Delete,
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
            Text(transaction.note, fontSize = 16.sp)
            Text(
                text = if (isExpense) "Chi tiêu" else "Thu nhập",
                fontSize = 12.sp,
                color = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)
            )
        }

        Text(
            text = "${if (isExpense) "-" else "+"}${formatMoney(transaction.amount.toLong())}₫",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)
        )
    }
}

/* ================= FORMAT MONEY ================= */

private fun formatMoney(value: Long): String {
    return "%,d".format(abs(value)).replace(",", ".")
}