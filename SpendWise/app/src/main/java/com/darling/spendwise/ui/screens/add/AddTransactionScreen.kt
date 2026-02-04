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
import com.darling.spendwise.ui.theme.AppColors

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
    Category(14, "Thiết bị điện tử", Icons.Default.Computer),
    Category(15, "Du lịch", Icons.Default.Flight),
    Category(16, "Sức khỏe", Icons.Default.FavoriteBorder),
    Category(17, "Thú cưng", Icons.Default.Pets),
    Category(18, "Sửa chữa", Icons.Default.Build),
    Category(19, "Nhà ở", Icons.Default.Home),
    Category(20, "Nhà", Icons.Default.Weekend),
    Category(21, "Quà tặng", Icons.Default.CardGiftcard),
    Category(22, "Quyên góp", Icons.Default.VolunteerActivism),
    Category(23, "Vé số", Icons.Default.ConfirmationNumber),
    Category(24, "Ăn vặt", Icons.Default.Fastfood),
    Category(25, "Trẻ em", Icons.Default.ChildCare),
    Category(26, "Rau quả", Icons.Default.Eco),
    Category(27, "Hoa quả", Icons.Default.LocalFlorist)
)

/* =======================
   ADD TRANSACTION SCREEN
   ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Primary)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "Hủy",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    Text(
                        text = "Thêm giao dịch",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.2f))
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
                                        AppColors.Primary
                                    else Color.White
                                )
                            }
                        }
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(expenseCategories) { category ->
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(AppColors.Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = AppColors.Primary,
                modifier = Modifier.size(26.dp)
            )
        }

        Text(
            text = category.name,
            fontSize = 12.sp,
            color = AppColors.TextPrimary,
            maxLines = 1
        )
    }
}
