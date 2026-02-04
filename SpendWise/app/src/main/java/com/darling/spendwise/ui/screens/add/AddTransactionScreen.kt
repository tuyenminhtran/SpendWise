package com.darling.spendwise.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    Category(16, "Sức khỏe", Icons.Default.FavoriteBorder),
)

/* =======================
   ADD TRANSACTION SCREEN
   ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryColor)
            ) {
                // Header
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
                        text = "Thêm giao dịch",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                // Tabs
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
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = text,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedTab == index)
                                        PrimaryColor
                                    else Color.White
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
            items(
                items = expenseCategories,
                key = { it.id }   // giảm lag
            ) { category ->
                CategoryItem(category)
            }
        }
    }
}

/* =======================
   CATEGORY ITEM
   ======================= */

@Composable
private fun CategoryItem(category: Category) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
