package com.darling.spendwise.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darling.spendwise.data.local.entity.TransactionEntity
import com.darling.spendwise.viewModel.TransactionViewModel

private val PrimaryColor = Color(0xFF1E88E5)
private val IncomeColor = Color(0xFF43A047)
private val ExpenseColor = Color(0xFFE53935)
private val TransferColor = Color(0xFFFF8F00)

/* =======================
   CATEGORY DATA
   ======================= */

data class Category(
    val id: Int,
    val name: String,
    val icon: ImageVector
)

private val expenseCategories = listOf(
    Category(1, "Mua sắm", Icons.Default.ShoppingCart),
    Category(2, "Đồ ăn", Icons.Default.Restaurant),
    Category(3, "Điện thoại", Icons.Default.Smartphone),
    Category(4, "Giải trí", Icons.Default.SportsEsports),
    Category(5, "Giáo dục", Icons.Default.School),
    Category(6, "Sắc đẹp", Icons.Default.ContentCut),
    Category(7, "Thể thao", Icons.Default.FitnessCenter),
    Category(8, "Xã hội", Icons.Default.People),
    Category(9, "Đi lại", Icons.Default.DirectionsBus),
    Category(10, "Quần áo", Icons.Default.Checkroom),
    Category(11, "Ô tô", Icons.Default.DirectionsCar),
    Category(12, "Rượu", Icons.Default.LocalBar),
    Category(13, "Thuốc lá", Icons.Default.SmokingRooms),
    Category(14, "Thiết bị", Icons.Default.Computer),
    Category(15, "Du lịch", Icons.Default.Flight),
    Category(16, "Sức khỏe", Icons.Default.FavoriteBorder)
)

private val incomeCategories = listOf(
    Category(101, "Lương", Icons.Default.AccountBalance),
    Category(102, "Thưởng", Icons.Default.EmojiEvents),
    Category(103, "Đầu tư", Icons.Default.TrendingUp),
    Category(104, "Bán hàng", Icons.Default.Storefront),
    Category(105, "Cho vay", Icons.Default.VolunteerActivism),
    Category(106, "Học bổng", Icons.Default.School),
    Category(107, "Phụ cấp", Icons.Default.CardGiftcard),
    Category(108, "Freelance", Icons.Default.Laptop),
    Category(109, "Cổ tức", Icons.Default.ShowChart),
    Category(110, "Tiền thuê", Icons.Default.Home),
    Category(111, "Hoàn tiền", Icons.Default.Replay),
    Category(112, "Khác", Icons.Default.MoreHoriz)
)

private val transferCategories = listOf(
    Category(201, "Tiết kiệm", Icons.Default.Savings),
    Category(202, "Trả nợ", Icons.Default.CreditCard),
    Category(203, "Cho mượn", Icons.Default.PersonAdd),
    Category(204, "Thu nợ", Icons.Default.AttachMoney),
    Category(205, "Nạp ví", Icons.Default.AccountBalanceWallet),
    Category(206, "Rút tiền", Icons.Default.LocalAtm),
    Category(207, "Chuyển khoản", Icons.Default.SwapHoriz),
    Category(208, "Khác", Icons.Default.MoreHoriz)
)

/* =======================
   ADD TRANSACTION SCREEN
   ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    val accentColor = when (selectedTab) {
        0 -> ExpenseColor
        1 -> IncomeColor
        else -> TransferColor
    }

    val currentCategories = when (selectedTab) {
        0 -> expenseCategories
        1 -> incomeCategories
        else -> transferCategories
    }

    val currentType = when (selectedTab) {
        0 -> "expense"
        1 -> "income"
        else -> "transfer"
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onBack) {
                        Text("Hủy", color = Color.White)
                    }
                    Text(
                        "Thêm giao dịch",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(Icons.Default.CalendarToday, null, tint = Color.White)
                }

                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    listOf("Chi tiêu", "Thu nhập", "Chuyển khoản")
                        .forEachIndexed { index, text ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (selectedTab == index) Color.White
                                        else Color.Transparent
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { selectedTab = index }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = text,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedTab == index) accentColor else Color.White
                                )
                            }
                        }
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(currentCategories, key = { "${selectedTab}_${it.id}" }) { category ->
                CategoryGridItem(
                    category = category,
                    accentColor = accentColor
                ) {
                    selectedCategory = category
                }
            }
        }
    }

    selectedCategory?.let { category ->
        AmountInputDialog(
            category = category,
            type = currentType,
            accentColor = accentColor,
            onConfirm = { amount, note ->
                viewModel.addTransaction(
                    TransactionEntity(
                        amount = amount,
                        type = currentType,
                        categoryId = category.id,
                        walletId = 1,
                        note = note.ifBlank { category.name },
                        date = System.currentTimeMillis()
                    )
                )
                selectedCategory = null
                onBack()
            },
            onDismiss = { selectedCategory = null }
        )
    }
}

/* =======================
   ALERT DIALOG NHẬP SỐ TIỀN
   ======================= */

@Composable
private fun AmountInputDialog(
    category: Category,
    type: String,
    accentColor: Color,
    onConfirm: (Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(category.name, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = when (type) {
                        "expense" -> "Chi tiêu"
                        "income" -> "Thu nhập"
                        else -> "Chuyển khoản"
                    },
                    fontSize = 13.sp,
                    color = accentColor
                )
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { c -> c.isDigit() }) amountText = it },
                    label = { Text("Số tiền (₫)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        focusedLabelColor = accentColor
                    ),
                    trailingIcon = {
                        Text("₫", fontWeight = FontWeight.Bold, color = Color.Gray)
                    },
                    placeholder = { Text("0") }
                )

                if (amountText.isNotEmpty()) {
                    Text(
                        text = formatPreview(amountText),
                        fontSize = 13.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú (tùy chọn)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        focusedLabelColor = accentColor
                    ),
                    placeholder = { Text(category.name) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) onConfirm(amount, note)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                enabled = amountText.isNotEmpty() && (amountText.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Lưu", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Hủy", color = Color.Gray)
            }
        }
    )
}

/* =======================
   CATEGORY GRID ITEM
   ======================= */

@Composable
private fun CategoryGridItem(
    category: Category,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.1f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = category.name,
            fontSize = 12.sp,
            color = Color.DarkGray,
            maxLines = 1
        )
    }
}

/* =======================
   UTIL
   ======================= */

private fun formatPreview(raw: String): String {
    val number = raw.toLongOrNull() ?: return raw
    return "%,d₫".format(number).replace(",", ".")
}