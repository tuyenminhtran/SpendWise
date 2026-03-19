package com.darling.spendwise.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.darling.spendwise.utils.UserPreferences

private val Primary       = Color(0xFF1565C0)
private val PrimaryLight  = Color(0xFF1E88E5)
private val BgColor       = Color(0xFFF0F4F8)
private val CardColor     = Color.White
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)

private val HeaderGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1565C0), Color(0xFF1E88E5), Color(0xFF42A5F5))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    var displayName by remember { mutableStateOf(userPrefs.displayName) }
    var phoneNumber by remember { mutableStateOf(userPrefs.phoneNumber) }
    var avatarUri   by remember { mutableStateOf(
        userPrefs.avatarUri.ifBlank { null }?.let { Uri.parse(it) }
    )}

    var showSaveSuccess by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    // Launcher chọn ảnh từ gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Persist permission
            context.contentResolver.takePersistableUriPermission(
                it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            avatarUri = it
        }
    }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = HeaderGradient)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                    Text(
                        "Thông tin cá nhân",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = {
                            if (displayName.isBlank()) {
                                nameError = true
                            } else {
                                userPrefs.displayName = displayName.trim()
                                userPrefs.phoneNumber = phoneNumber.trim()
                                userPrefs.avatarUri   = avatarUri?.toString() ?: ""
                                showSaveSuccess = true
                            }
                        }
                    ) {
                        Text("Lưu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Avatar picker
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Primary.copy(0.15f))
                        .border(3.dp, Color.White, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUri != null) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Person, null,
                            tint = Primary,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                // Edit badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Primary)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Bấm để đổi ảnh đại diện",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(Modifier.height(32.dp))

            // Form fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardColor)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tên hiển thị
                Column {
                    Text(
                        "Tên hiển thị",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = {
                            displayName = it
                            nameError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nameError,
                        supportingText = if (nameError) {{ Text("Tên không được để trống", color = Color(0xFFE53935), fontSize = 12.sp) }} else null,
                        placeholder = { Text("Nhập tên của bạn") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, null, tint = Primary, modifier = Modifier.size(20.dp))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                HorizontalDivider(color = Color(0xFFEEF2F7))

                // Số điện thoại
                Column {
                    Text(
                        "Số điện thoại",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Nhập số điện thoại") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = {
                            Icon(Icons.Default.Phone, null, tint = Primary, modifier = Modifier.size(20.dp))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Xóa ảnh nếu có
            if (avatarUri != null) {
                TextButton(
                    onClick = {
                        avatarUri = null
                        userPrefs.avatarUri = ""
                    }
                ) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Xóa ảnh đại diện", color = Color(0xFFE53935), fontSize = 13.sp)
                }
            }
        }
    }

    // Success snackbar
    if (showSaveSuccess) {
        AlertDialog(
            onDismissRequest = { showSaveSuccess = false; onBack() },
            containerColor = CardColor,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFF43A047).copy(0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Done, null, tint = Color(0xFF43A047), modifier = Modifier.size(28.dp))
                }
            },
            title = {
                Text("Đã lưu!", fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    color = TextPrimary)
            },
            text = { Text("Thông tin cá nhân đã được cập nhật.", color = TextSecondary, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = { showSaveSuccess = false; onBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("OK", color = Color.White) }
            }
        )
    }
}