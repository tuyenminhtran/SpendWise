package com.darling.spendwise.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    val currentUser get() = auth.currentUser

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { _authState.value = AuthState.Success(it) }
                    ?: run { _authState.value = AuthState.Error("Đăng nhập thất bại") }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(parseError(e.message))
            }
        }
    }

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    // Update display name
                    val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName).build()
                    user.updateProfile(profileUpdate).await()
                    _authState.value = AuthState.Success(user)
                } ?: run { _authState.value = AuthState.Error("Đăng ký thất bại") }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(parseError(e.message))
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                result.user?.let { _authState.value = AuthState.Success(it) }
                    ?: run { _authState.value = AuthState.Error("Đăng nhập Google thất bại") }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(parseError(e.message))
            }
        }
    }

    fun resetPassword(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                onResult(true, "Email đặt lại mật khẩu đã được gửi!")
            } catch (e: Exception) {
                onResult(false, parseError(e.message))
            }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    private fun parseError(message: String?): String {
        return when {
            message == null -> "Đã xảy ra lỗi"
            message.contains("email") -> "Email không hợp lệ"
            message.contains("password") -> "Mật khẩu phải ít nhất 6 ký tự"
            message.contains("already in use") -> "Email đã được sử dụng"
            message.contains("no user") || message.contains("invalid") -> "Email hoặc mật khẩu không đúng"
            message.contains("network") -> "Lỗi kết nối mạng"
            else -> "Đã xảy ra lỗi, thử lại sau"
        }
    }
}