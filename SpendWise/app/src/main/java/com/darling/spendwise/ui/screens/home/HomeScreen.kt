package com.darling.spendwise.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

data class Transaction(
    val title: String,
    val date: String,
    val amount: Long,
    val icon: ImageVector,
    val iconBg: Color
)

private val demoTransactions = listOf(
    Transaction("mua iqoo 13", "22 thg 1", -16_000_000, Icons.Default.ShoppingCart, Color(0xFFFFA726)),
    Transaction("mua tay cầm", "22 thg 1", -700_000, Icons.Default.SportsEsports, Color(0xFF66BB6A)),
    Transaction("Vé số", "22 thg 1", -100_000, Icons.Default.ConfirmationNumber, Color(0xFFAB47BC)),
    Transaction("Mua sắm", "22 thg 1", -70_000, Icons.Default.Store, Color(0xFFFFA726))
)

/* =======================
   HOME SCREEN
   ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var selectedMonth by remember { mutableStateOf("Thg 1") }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E88E5)) // PRIMARY
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )

                    Text(
                        text = "SpendWise",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Summary label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("2026", fontSize = 14.sp, color = Color.White.copy(0.8f))
                    Text("Chi tiêu", fontSize = 14.sp, color = Color.White.copy(0.8f))
                    Text("Thu nhập", fontSize = 14.sp, color = Color.White.copy(0.8f))
                    Text("Số dư", fontSize = 14.sp, color = Color.White.copy(0.8f))
                }

                // Values
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
                            text = selectedMonth,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text("16.870.000", color = Color(0xFFFFCDD2))
                    Text("0", color = Color(0xFFC8E6C9))
                    Text("-16.870.000", color = Color.White)
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

            // Info banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Sau khi đăng nhập, bạn có thể sao lưu dữ liệu của mình trong thời gian thực!",
                        color = Color(0xFF1565C0),
                        fontSize = 13.sp
                    )
                }
            }

            // Date header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("22 thg 1   Thứ năm", color = Color(0xFF757575))
                    Text("Chi tiêu: 16.870.000", color = Color(0xFF757575))
                }
            }

            // Transactions
            items(demoTransactions) {
                TransactionItem(it)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

/* =======================
   TRANSACTION ITEM
   ======================= */

@Composable
private fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(transaction.iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = transaction.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        // Title
        Text(
            text = transaction.title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        // Amount
        Text(
            text = formatMoney(transaction.amount),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/* =======================
   UTIL
   ======================= */

private fun formatMoney(value: Long): String {
    val absValue = abs(value)
    val formatted = "%,d".format(absValue).replace(",", ".")
    return if (value < 0) "-$formatted" else formatted
}