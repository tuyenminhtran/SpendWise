package com.darling.spendwise.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.darling.spendwise.utils.UserPreferences
import com.darling.spendwise.utils.exportCsvToDownloads
import com.darling.spendwise.viewModel.AuthViewModel
import com.darling.spendwise.viewModel.TransactionViewModel
import kotlinx.coroutines.launch

private val Primary     = Color(0xFF1565C0)
private val ExpenseRed  = Color(0xFFE53935)
private val IncomeGreen = Color(0xFF43A047)
private val HeaderGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1565C0), Color(0xFF1E88E5), Color(0xFF42A5F5))
)

data class ProfileMenuItem(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val iconBg: Color = Primary.copy(0.1f),
    val iconTint: Color = Primary,
    val showArrow: Boolean = true,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: TransactionViewModel,
    authViewModel: AuthViewModel,
    onNavigate: (String) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    val transactions by viewModel.transactions.collectAsState()
    val userPrefs = remember { UserPreferences(context) }

    // Firebase user
    val firebaseUser = authViewModel.currentUser
    val isLoggedIn = firebaseUser != null

    val bgColor       = if (isDarkMode) Color(0xFF0F1923) else Color(0xFFF0F4F8)
    val cardColor     = if (isDarkMode) Color(0xFF1A2633) else Color.White
    val textPrimary   = if (isDarkMode) Color(0xFFE8EDF1) else Color(0xFF1A1A2E)
    val textSecondary = if (isDarkMode) Color(0xFF9BABBF) else Color(0xFF6B7280)
    val dividerColor  = if (isDarkMode) Color(0xFF2D3F52) else Color(0xFFEEF2F7)

    // Ưu tiên dùng tên từ Firebase nếu đã login, còn không thì SharedPreferences
    var displayName by remember {
        mutableStateOf(firebaseUser?.displayName ?: userPrefs.displayName)
    }
    var phoneNumber by remember { mutableStateOf(userPrefs.phoneNumber) }
    var avatarUri   by remember { mutableStateOf(userPrefs.avatarUri.ifBlank { null }) }

    LaunchedEffect(Unit) {
        displayName = firebaseUser?.displayName ?: userPrefs.displayName
        phoneNumber = userPrefs.phoneNumber
        avatarUri   = userPrefs.avatarUri.ifBlank { null }
    }

    val totalTx = transactions.size
    var showDeleteDialog  by remember { mutableStateOf(false) }
    var showLogoutDialog  by remember { mutableStateOf(false) }
    var showExportResult  by remember { mutableStateOf<String?>(null) }
    var isExporting       by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = HeaderGradient)
                    .statusBarsPadding()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Hồ sơ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            /* ===== Avatar ===== */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = HeaderGradient)
                        .padding(bottom = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White.copy(0.25f))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                onNavigate("edit_profile")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            // Ảnh từ Google account
                            firebaseUser?.photoUrl != null -> {
                                AsyncImage(
                                    model = firebaseUser.photoUrl,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            // Ảnh local
                            avatarUri != null -> {
                                AsyncImage(
                                    model = avatarUri,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(50.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)

                    if (phoneNumber.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(phoneNumber, fontSize = 13.sp, color = Color.White.copy(0.8f))
                    }

                    // Email Firebase nếu đã login
                    firebaseUser?.email?.let { email ->
                        Spacer(Modifier.height(4.dp))
                        Text(email, fontSize = 12.sp, color = Color.White.copy(0.7f))
                    }

                    Spacer(Modifier.height(8.dp))

                    // Badge trạng thái login
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isLoggedIn) IncomeGreen.copy(0.3f)
                                else Color.White.copy(0.2f)
                            )
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                if (!isLoggedIn) onNavigate("login")
                            }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                if (isLoggedIn) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                if (isLoggedIn) "Đã đồng bộ" else "Bấm để đăng nhập",
                                fontSize = 12.sp, color = Color.White.copy(0.9f)
                            )
                        }
                    }
                }
            }

            /* ===== Tài khoản ===== */
            item { SectionHeader("Tài khoản", textSecondary) }
            item {
                ProfileMenuGroup(
                    cardColor = cardColor, textPrimary = textPrimary,
                    textSecondary = textSecondary, dividerColor = dividerColor,
                    items = listOf(
                        ProfileMenuItem(
                            "Thông tin cá nhân", "Tên, số điện thoại, ảnh đại diện",
                            Icons.Default.Person, onClick = { onNavigate("edit_profile") }
                        ),
                        ProfileMenuItem("Bảo mật", "Đổi mật khẩu", Icons.Default.Lock),
                        ProfileMenuItem("Thông báo", "Cài đặt nhắc nhở", Icons.Default.Notifications)
                    )
                )
            }

            /* ===== Dữ liệu ===== */
            item { SectionHeader("Dữ liệu", textSecondary) }
            item {
                ProfileMenuGroup(
                    cardColor = cardColor, textPrimary = textPrimary,
                    textSecondary = textSecondary, dividerColor = dividerColor,
                    items = listOf(
                        ProfileMenuItem(
                            title = "Xuất dữ liệu CSV",
                            subtitle = "Lưu vào thư mục Downloads",
                            icon = Icons.Default.FileDownload,
                            iconBg = IncomeGreen.copy(0.1f), iconTint = IncomeGreen,
                            onClick = {
                                isExporting = true
                                scope.launch {
                                    val result = exportCsvToDownloads(context, transactions)
                                    isExporting = false
                                    showExportResult = result.fold(
                                        onSuccess = { "✓ Đã xuất: $it" },
                                        onFailure = { "✗ Lỗi: ${it.message}" }
                                    )
                                }
                            }
                        ),
                        ProfileMenuItem(
                            title = "Xóa tất cả dữ liệu",
                            subtitle = "$totalTx giao dịch · Không thể hoàn tác",
                            icon = Icons.Default.DeleteForever,
                            iconBg = ExpenseRed.copy(0.1f), iconTint = ExpenseRed,
                            showArrow = false, onClick = { showDeleteDialog = true }
                        )
                    )
                )
            }

            /* ===== Ứng dụng ===== */
            item { SectionHeader("Ứng dụng", textSecondary) }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardColor)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(Primary.copy(0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                null, tint = Primary, modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Giao diện", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textPrimary)
                            Text(
                                if (isDarkMode) "Chế độ tối" else "Chế độ sáng",
                                fontSize = 12.sp, color = textSecondary
                            )
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { onToggleDarkMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Primary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFB0BEC5)
                            )
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
            item {
                ProfileMenuGroup(
                    cardColor = cardColor, textPrimary = textPrimary,
                    textSecondary = textSecondary, dividerColor = dividerColor,
                    items = listOf(
                        ProfileMenuItem("Ngôn ngữ", "Tiếng Việt", Icons.Default.Language),
                        ProfileMenuItem("Tiền tệ", "VND (₫)", Icons.Default.AttachMoney)
                    )
                )
            }

            /* ===== Hỗ trợ ===== */
            item { SectionHeader("Hỗ trợ", textSecondary) }
            item {
                ProfileMenuGroup(
                    cardColor = cardColor, textPrimary = textPrimary,
                    textSecondary = textSecondary, dividerColor = dividerColor,
                    items = listOf(
                        ProfileMenuItem("Trung tâm trợ giúp", null, Icons.Default.HelpOutline),
                        ProfileMenuItem("Gửi phản hồi", null, Icons.Default.Feedback),
                        ProfileMenuItem("Đánh giá ứng dụng", null, Icons.Default.Star),
                        ProfileMenuItem("Về SpendWise", "Phiên bản 1.0.0", Icons.Default.Info)
                    )
                )
            }

            /* ===== Đăng nhập / Đăng xuất ===== */
            item {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardColor)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            if (isLoggedIn) showLogoutDialog = true
                            else onNavigate("login")
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isLoggedIn) ExpenseRed.copy(0.1f) else Primary.copy(0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isLoggedIn) Icons.Default.Logout else Icons.Default.Login,
                                null,
                                tint = if (isLoggedIn) ExpenseRed else Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            if (isLoggedIn) "Đăng xuất" else "Đăng nhập",
                            fontSize = 15.sp,
                            color = if (isLoggedIn) ExpenseRed else Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            item {
                Text(
                    "SpendWise v1.0.0",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    fontSize = 12.sp, color = textSecondary, textAlign = TextAlign.Center
                )
            }
        }
    }

    // Dialog xóa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = cardColor, shape = RoundedCornerShape(20.dp),
            icon = {
                Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(ExpenseRed.copy(0.1f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DeleteForever, null, tint = ExpenseRed, modifier = Modifier.size(28.dp))
                }
            },
            title = { Text("Xóa tất cả dữ liệu?", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = textPrimary) },
            text = { Text("Tất cả $totalTx giao dịch sẽ bị xóa vĩnh viễn. Hành động này không thể hoàn tác!", color = textSecondary, textAlign = TextAlign.Center, fontSize = 14.sp) },
            confirmButton = {
                Button(onClick = { viewModel.deleteAllTransactions(); showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed), shape = RoundedCornerShape(10.dp)) {
                    Text("Xóa hết", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }, shape = RoundedCornerShape(10.dp)) {
                    Text("Hủy", color = textSecondary)
                }
            }
        )
    }

    // Dialog đăng xuất
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = cardColor, shape = RoundedCornerShape(20.dp),
            icon = {
                Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(ExpenseRed.copy(0.1f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Logout, null, tint = ExpenseRed, modifier = Modifier.size(28.dp))
                }
            },
            title = { Text("Đăng xuất?", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = textPrimary) },
            text = { Text("Bạn sẽ không còn đồng bộ dữ liệu lên cloud. Dữ liệu local vẫn được giữ lại.", color = textSecondary, textAlign = TextAlign.Center, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = { authViewModel.logout(); showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Đăng xuất", color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }, shape = RoundedCornerShape(10.dp)) {
                    Text("Hủy", color = textSecondary)
                }
            }
        )
    }

    // Loading export
    if (isExporting) {
        AlertDialog(
            onDismissRequest = {}, containerColor = cardColor, shape = RoundedCornerShape(20.dp), title = null,
            text = {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator(color = Primary, modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                    Text("Đang xuất dữ liệu...", fontSize = 15.sp, color = textPrimary)
                }
            },
            confirmButton = {}
        )
    }

    // Kết quả export
    showExportResult?.let { msg ->
        val isSuccess = msg.startsWith("✓")
        AlertDialog(
            onDismissRequest = { showExportResult = null },
            containerColor = cardColor, shape = RoundedCornerShape(20.dp),
            icon = {
                Box(modifier = Modifier.size(56.dp).clip(CircleShape).background((if (isSuccess) IncomeGreen else ExpenseRed).copy(0.1f)), contentAlignment = Alignment.Center) {
                    Icon(if (isSuccess) Icons.Default.Done else Icons.Default.ErrorOutline, null,
                        tint = if (isSuccess) IncomeGreen else ExpenseRed, modifier = Modifier.size(28.dp))
                }
            },
            title = { Text(if (isSuccess) "Xuất thành công!" else "Xuất thất bại", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = textPrimary) },
            text = { Text(msg.substringAfter(": "), color = textSecondary, textAlign = TextAlign.Center, fontSize = 13.sp) },
            confirmButton = {
                Button(onClick = { showExportResult = null }, colors = ButtonDefaults.buttonColors(containerColor = Primary), shape = RoundedCornerShape(10.dp)) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
}

/* ================= SECTION HEADER ================= */

@Composable
private fun SectionHeader(title: String, color: Color) {
    Text(
        title,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        fontSize = 12.sp, color = color,
        fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp
    )
}

/* ================= MENU GROUP ================= */

@Composable
private fun ProfileMenuGroup(
    cardColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    dividerColor: Color,
    items: List<ProfileMenuItem>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
    ) {
        Column {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { item.onClick() }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(item.iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, null, tint = item.iconTint, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textPrimary)
                        item.subtitle?.let { Text(it, fontSize = 12.sp, color = textSecondary) }
                    }
                    if (item.showArrow) {
                        Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                }
                if (index < items.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = dividerColor)
                }
            }
        }
    }
}