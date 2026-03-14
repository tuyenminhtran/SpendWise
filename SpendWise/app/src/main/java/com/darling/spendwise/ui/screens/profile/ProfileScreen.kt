package com.darling.spendwise.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darling.spendwise.viewModel.TransactionViewModel

/* =======================
   FAKE DATA
   ======================= */

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val subtitle: String? = null,
    val showArrow: Boolean = true
)

private val accountMenuItems = listOf(
    MenuItem("Thông tin cá nhân", Icons.Default.Person, "Chỉnh sửa thông tin"),
    MenuItem("Bảo mật", Icons.Default.Lock, "Đổi mật khẩu"),
    MenuItem("Thông báo", Icons.Default.Notifications, "Cài đặt nhắc nhở"),
)

private val dataMenuItems = listOf(
    MenuItem("Sao lưu dữ liệu", Icons.Default.CloudUpload, "Google Drive, Dropbox"),
    MenuItem("Xuất dữ liệu", Icons.Default.FileDownload, "Excel, CSV"),
    MenuItem("Xóa tất cả dữ liệu", Icons.Default.DeleteForever, null, false),
)

private val appMenuItems = listOf(
    MenuItem("Giao diện", Icons.Default.Palette, "Sáng, Tối, Tự động"),
    MenuItem("Ngôn ngữ", Icons.Default.Language, "Tiếng Việt"),
    MenuItem("Tiền tệ", Icons.Default.AttachMoney, "VND (₫)"),
)

private val supportMenuItems = listOf(
    MenuItem("Trung tâm trợ giúp", Icons.Default.HelpOutline),
    MenuItem("Gửi phản hồi", Icons.Default.Feedback),
    MenuItem("Đánh giá ứng dụng", Icons.Default.Star),
    MenuItem("Về chúng tôi", Icons.Default.Info),
)

/* =======================
   COLORS
   ======================= */

private val PrimaryBlue = Color(0xFF1E88E5)
private val LightBlueBg = Color(0xFFE3F2FD)
private val DangerRed = Color(0xFFE53935)

/* =======================
   PROFILE SCREEN
   ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: TransactionViewModel) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .statusBarsPadding()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tôi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            /* ===== Avatar ===== */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Người dùng", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("user@spendwise.com", fontSize = 14.sp, color = Color.Gray)

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Chỉnh sửa hồ sơ")
                    }
                }
            }

            item { SectionHeader("Tài khoản") }
            item { MenuGroup(accountMenuItems) }

            item { SectionHeader("Dữ liệu") }
            item { MenuGroup(dataMenuItems) }

            item { SectionHeader("Ứng dụng") }
            item { MenuGroup(appMenuItems) }

            item { SectionHeader("Hỗ trợ") }
            item { MenuGroup(supportMenuItems) }

            /* ===== Logout ===== */
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Logout, null, tint = DangerRed)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Đăng xuất",
                            color = DangerRed,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Text(
                    text = "SpendWise v1.0.0",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/* =======================
   COMPONENTS
   ======================= */

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        fontSize = 14.sp,
        color = Color.Gray,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun MenuGroup(items: List<MenuItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            items.forEachIndexed { index, item ->
                MenuItemRow(item)
                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color(0xFFEEEEEE)
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuItemRow(item: MenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, fontSize = 15.sp)
            item.subtitle?.let {
                Text(it, fontSize = 12.sp, color = Color.Gray)
            }
        }

        if (item.showArrow) {
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}
