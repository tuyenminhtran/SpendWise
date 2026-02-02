package com.darling.spendwise.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darling.spendwise.data.local.entity.TransactionEntity
import com.darling.spendwise.ui.theme.TurquoisePrimary
import com.darling.spendwise.viewModel.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(viewModel: TransactionViewModel) {
    val transactions by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = {
            // Header màu xanh ngọc
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TurquoisePrimary)
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SpendWise",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add transaction */ },
                containerColor = TurquoisePrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm giao dịch",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Title
            Text(
                text = "List View thể hiện những chi tiêu mình phân",
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )

            // List giao dịch
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chưa có giao dịch nào",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.type,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                if (transaction.note.isNotEmpty()) {
                    Text(
                        text = transaction.note,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = dateFormat.format(Date(transaction.date)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = currencyFormat.format(transaction.amount),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (transaction.type == "Thu nhập")
                    Color(0xFF4CAF50)
                else
                    Color(0xFFF44336)
            )
        }
    }
}
