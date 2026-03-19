package com.darling.spendwise.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.darling.spendwise.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // ViewModel chỉ biết Repository, không biết Firebase
    private val repository = AuthRepository(application.applicationContext)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)

    val authState: StateFlow<AuthState> = _authState

    val currentUser get() = repository.currentUser

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.login(email, password).fold(
                onSuccess = { _authState.value = AuthState.Success(it) },
                onFailure = { _authState.value = AuthState.Error(parseError(it.message)) }
            )
        }
    }

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.register(email, password, displayName).fold(
                onSuccess = { _authState.value = AuthState.Success(it) },
                onFailure = { _authState.value = AuthState.Error(parseError(it.message)) }
            )
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.signInWithGoogle(idToken).fold(
                onSuccess = { _authState.value = AuthState.Success(it) },
                onFailure = { _authState.value = AuthState.Error(parseError(it.message)) }
            )
        }
    }

    fun resetPassword(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            repository.resetPassword(email).fold(
                onSuccess = { onResult(true, "Email đặt lại mật khẩu đã được gửi!") },
                onFailure = { onResult(false, parseError(it.message)) }
            )
        }
    }

    fun logout() {
        repository.logout()
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
