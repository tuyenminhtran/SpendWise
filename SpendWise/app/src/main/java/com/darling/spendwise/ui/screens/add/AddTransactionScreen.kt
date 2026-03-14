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
import androidx.compose.ui.window.Dialog
import com.darling.spendwise.data.local.entity.TransactionEntity
import com.darling.spendwise.viewModel.TransactionViewModel

private val PrimaryColor = Color(0xFF1E88E5)

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

    // State cho dialog nhập tiền
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryColor)
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
                                    color = if (selectedTab == index) PrimaryColor else Color.White
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
            items(expenseCategories, key = { it.id }) { category ->
                CategoryGridItem(category) {
                    selectedCategory = category  // mở dialog
                }
            }
        }
    }

    // Dialog nhập số tiền
    selectedCategory?.let { category ->
        AmountInputDialog(
            category = category,
            type = if (selectedTab == 0) "expense" else "income",
            onConfirm = { amount, note ->
                viewModel.addTransaction(
                    TransactionEntity(
                        amount = amount,
                        type = if (selectedTab == 0) "expense" else "income",
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
   DIALOG NHẬP SỐ TIỀN
   ======================= */

@Composable
private fun AmountInputDialog(
    category: Category,
    type: String,
    onConfirm: (Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val isExpense = type == "expense"
    val accentColor = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Icon danh mục
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    if (isExpense) "Chi tiêu" else "Thu nhập",
                    fontSize = 13.sp,
                    color = accentColor
                )

                Spacer(Modifier.height(20.dp))

                // Input số tiền
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { value ->
                        // Chỉ cho nhập số
                        if (value.all { it.isDigit() }) amountText = value
                    },
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
                    // Hiển thị số có dấu chấm ngăn cách
                    placeholder = { Text("0") }
                )

                // Hiển thị số tiền đã format
                if (amountText.isNotEmpty()) {
                    Text(
                        text = formatPreview(amountText),
                        fontSize = 14.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Input ghi chú
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú (tùy chọn)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(        // ← thêm dòng này
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done

                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        focusedLabelColor = accentColor
                    ),
                    placeholder = { Text(category.name) }
                )

                Spacer(Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Hủy", color = Color.Gray)
                    }

                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            if (amount > 0) onConfirm(amount, note)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        enabled = amountText.isNotEmpty() && (amountText.toDoubleOrNull() ?: 0.0) > 0
                    ) {
                        Text("Lưu", color = Color.White)
                    }
                }
            }
        }
    }
}

/* =======================
   CATEGORY GRID ITEM
   ======================= */

@Composable
private fun CategoryGridItem(
    category: Category,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(PrimaryColor.copy(alpha = 0.1f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = PrimaryColor,
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