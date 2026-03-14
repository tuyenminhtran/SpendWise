package com.darling.spendwise.ui.screens.report


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darling.spendwise.viewModel.TransactionViewModel

/* =======================
   REPORT SCREEN
   ======================= */

private val PrimaryBlue = Color(0xFF2962FF)
private val PrimaryBlueLight = Color(0xFFE3F2FD)
private val BackgroundGray = Color(0xFFF6F7F9)
private val TextPrimary = Color(0xFF212121)
private val TextSecondary = Color(0xFF757575)

/* =======================
   FAKE DATA
   ======================= */

data class MonthlyStats(
    val month: String,
    val expense: Long,
    val income: Long,
    val balance: Long
)

private val monthlyData = MonthlyStats(
    month = "Thg 2",
    expense = 0,
    income = 0,
    balance = 0
)

/* =======================
   REPORT SCREEN
   ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(viewModel: TransactionViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Báo cáo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
                    listOf("Phân tích", "Tài khoản").forEachIndexed { index, text ->
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
                                color = if (selectedTab == index)
                                    PrimaryBlue
                                else Color.White,
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
            contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp)
        ) {

            /* ===== Thống kê hàng tháng ===== */
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Thống kê hàng tháng",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = monthlyData.month,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                StatRow("Chi tiêu", monthlyData.expense)
                                Spacer(Modifier.height(8.dp))
                                StatRow("Thu nhập", monthlyData.income)
                                Spacer(Modifier.height(8.dp))
                                StatRow("Số dư", monthlyData.balance)
                            }
                        }
                    }
                }
            }

            /* ===== Ngân sách hàng tháng ===== */
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ngân sách hàng tháng",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlueLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "--",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                BudgetRow("Còn lại :", 0)
                                BudgetRow("Ngân sách :", 0)
                                BudgetRow("Chi tiêu :", 0)
                            }
                        }
                    }
                }
            }
        }
    }
}

/* =======================
   HELPER
   ======================= */

@Composable
private fun StatRow(label: String, value: Long) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp
        )
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
private fun BudgetRow(label: String, value: Long) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp
        )
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}


