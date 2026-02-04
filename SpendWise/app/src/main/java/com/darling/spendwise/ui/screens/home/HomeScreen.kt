package com.darling.spendwise.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
   FAKE DATA (DEMO)
   ======================= */

data class Transaction(
    val title: String,
    val amount: Long,
    val icon: ImageVector,
    val iconBg: Color
)

private val demoTransactions = listOf(
    Transaction("Mua iQOO 13", -16_000_000, Icons.Default.ShoppingCart, Color(0xFFFFC107)),
    Transaction("Mua tay cầm", -700_000, Icons.Default.SportsEsports, Color(0xFF4CAF50)),
    Transaction("Vé số", -100_000, Icons.Default.ConfirmationNumber, Color(0xFF64B5F6)),
    Transaction("Mua sắm", -70_000, Icons.Default.Store, Color(0xFFBA68C8))
)

/* =======================
   HOME SCREEN (DEMO)
   ======================= */

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
    ) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
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

        // Balance Card
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Tổng số dư", color = Color.Gray)

                Text(
                    text = "₫1.250.000",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Thu: ₫2.000.000", color = Color(0xFF2E7D32))
                    Text("Chi: ₫750.000", color = Color(0xFFC62828))
                }
            }
        }

        Text(
            text = "Giao dịch gần đây",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(demoTransactions) {
                TransactionItem(it)
            }
        }
    }
}

/* =======================
   ITEM
   ======================= */

@Composable
private fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(transaction.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = transaction.icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = transaction.title,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = formatMoney(transaction.amount),
                color = Color.Red
            )
        }
    }
}

/* =======================
   UTIL
   ======================= */

private fun formatMoney(value: Long): String {
    val formatted = "%,d".format(abs(value)).replace(",", ".")
    return if (value < 0) "-$formatted đ" else "$formatted đ"
}
