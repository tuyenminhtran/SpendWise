package com.darling.spendwise.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darling.spendwise.viewModel.AuthState
import com.darling.spendwise.viewModel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()

    var displayName by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
            authViewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F4F8))) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.linearGradient(colors = listOf(Color(0xFF1565C0), Color(0xFF1E88E5), Color(0xFF42A5F5))))
                    .statusBarsPadding()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PersonAdd, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Tạo tài khoản", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Đăng ký để sync dữ liệu", fontSize = 13.sp, color = Color.White.copy(0.8f))
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Thông tin tài khoản", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))

                // Tên
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Họ tên") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFF1565C0), modifier = Modifier.size(20.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1565C0), focusedLabelColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp)
                )

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF1565C0), modifier = Modifier.size(20.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1565C0), focusedLabelColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp)
                )

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = "" },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF1565C0), modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color(0xFF6B7280), modifier = Modifier.size(20.dp))
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1565C0), focusedLabelColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp)
                )

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; passwordError = "" },
                    label = { Text("Xác nhận mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Default.LockOpen, null, tint = Color(0xFF1565C0), modifier = Modifier.size(20.dp)) },
                    isError = passwordError.isNotBlank(),
                    supportingText = if (passwordError.isNotBlank()) {{ Text(passwordError, color = Color(0xFFE53935)) }} else null,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1565C0), focusedLabelColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp)
                )

                // Error
                if (authState is AuthState.Error) {
                    Text(
                        (authState as AuthState.Error).message,
                        color = Color(0xFFE53935), fontSize = 13.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        if (password != confirmPassword) {
                            passwordError = "Mật khẩu không khớp"
                        } else if (password.length < 6) {
                            passwordError = "Mật khẩu ít nhất 6 ký tự"
                        } else {
                            authViewModel.register(email, password, displayName)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    enabled = displayName.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Đăng ký", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Đã có tài khoản?", fontSize = 14.sp, color = Color(0xFF6B7280))
                Text(
                    "Đăng nhập",
                    fontSize = 14.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onNavigateToLogin() }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}