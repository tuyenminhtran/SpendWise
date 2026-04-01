package com.darling.spendwise.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darling.spendwise.data.local.entity.TransactionEntity
import com.darling.spendwise.data.repository.AuthRepository
import com.darling.spendwise.data.repository.FirestoreRepository
import com.darling.spendwise.data.repository.TransactionRepository
import com.darling.spendwise.utils.exportCsvToDownloads
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val firestoreRepository = FirestoreRepository()
    private val authRepository = AuthRepository()

    // SyncState để UI biết đang sync hay không
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState

    val transactions = repository.getAllTransactions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.insert(transaction)
            authRepository.currentUser?.uid?.let { userId ->
                firestoreRepository.upsertTransaction(userId, transaction)
            }
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.update(transaction)
            authRepository.currentUser?.uid?.let { userId ->
                firestoreRepository.upsertTransaction(userId, transaction)
            }
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(transaction)
            authRepository.currentUser?.uid?.let { userId ->
                firestoreRepository.deleteTransaction(userId, transaction.id)
            }
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch {
            repository.deleteAll()
            authRepository.currentUser?.uid?.let { userId ->
                firestoreRepository.deleteAllTransactions(userId)
            }
        }
    }

    // Gọi sau khi login thành công
    fun syncFromFirestore() {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch

            _syncState.value = SyncState.Loading

            firestoreRepository.downloadTransactions(userId).fold(
                onSuccess = { cloudTransactions ->
                    val localIds = transactions.value.map { it.id }.toSet()
                    cloudTransactions
                        .filter { it.id !in localIds }
                        .forEach { repository.insert(it) }
                    _syncState.value = SyncState.Success
                },
                onFailure = {
                    _syncState.value = SyncState.Error(it.message ?: "Sync thất bại")
                }
            )
        }
    }

    fun exportToCsv(context: Context) {
        viewModelScope.launch {
            exportCsvToDownloads(context, transactions.value)
        }
    }
}

sealed class SyncState {
    object Idle : SyncState()
    object Loading : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}
