package com.darling.spendwise.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.darling.spendwise.R
import com.darling.spendwise.viewModel.AuthState
import com.darling.spendwise.viewModel.AuthViewModel
import com.darling.spendwise.viewModel.TransactionViewModel

private val Primary       = Color(0xFF1565C0)
private val PrimaryLight  = Color(0xFF1E88E5)
private val ExpenseRed    = Color(0xFFE53935)
private val BgColor       = Color(0xFFF0F4F8)
private val CardColor     = Color.White
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)

private val HeaderGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1565C0), Color(0xFF1E88E5), Color(0xFF42A5F5))
)

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    viewModel: TransactionViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotResult by remember { mutableStateOf<String?>(null) }

    // Google Sign In launcher
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
                account.idToken?.let { authViewModel.signInWithGoogle(it) }
            } catch (e: ApiException) {
                // handle error
            }
        }
    }

    // Observe auth state
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            viewModel.syncFromFirestore()
            onLoginSuccess()
            authViewModel.resetState()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(BgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = HeaderGradient)
                    .statusBarsPadding()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalance, null, tint = Color.White, modifier = Modifier.size(44.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("SpendWise", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Quản lý chi tiêu thông minh", fontSize = 14.sp, color = Color.White.copy(0.8f))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardColor)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Đăng nhập", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Primary, modifier = Modifier.size(20.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Primary, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null, tint = TextSecondary, modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quên mật khẩu
                Text(
                    "Quên mật khẩu?",
                    fontSize = 13.sp, color = Primary, fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            showForgotDialog = true
                        }
                )

                // Error
                if (authState is AuthState.Error) {
                    Text(
                        (authState as AuthState.Error).message,
                        color = ExpenseRed, fontSize = 13.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Nút đăng nhập
                Button(
                    onClick = { authViewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = email.isNotBlank() && password.isNotBlank() && authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Đăng nhập", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Divider
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEF2F7))
                    Text("hoặc", fontSize = 13.sp, color = TextSecondary)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEF2F7))
                }

                // Google Sign In
                OutlinedButton(
                    onClick = { googleLauncher.launch(googleSignInClient.signInIntent) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Google icon
                        Box(
                            modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(0xFFEA4335)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text("Đăng nhập bằng Google", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Đăng ký
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Chưa có tài khoản?", fontSize = 14.sp, color = TextSecondary)
                Text(
                    "Đăng ký",
                    fontSize = 14.sp, color = Primary, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        onNavigateToRegister()
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            // Skip
            TextButton(onClick = onSkip) {
                Text("Dùng không cần đăng nhập →", fontSize = 13.sp, color = TextSecondary)
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // Forgot password dialog
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false; forgotResult = null },
            containerColor = CardColor,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Primary.copy(0.1f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LockReset, null, tint = Primary, modifier = Modifier.size(24.dp))
                }
            },
            title = { Text("Quên mật khẩu", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Nhập email để nhận link đặt lại mật khẩu", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary),
                        shape = RoundedCornerShape(12.dp)
                    )
                    forgotResult?.let {
                        Text(it, fontSize = 13.sp, color = if (it.contains("gửi")) Primary else ExpenseRed, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.resetPassword(forgotEmail) { success, msg ->
                            forgotResult = msg
                            if (success) forgotEmail = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(10.dp),
                    enabled = forgotEmail.isNotBlank()
                ) { Text("Gửi", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false; forgotResult = null }) {
                    Text("Đóng", color = TextSecondary)
                }
            }
        )
    }
}