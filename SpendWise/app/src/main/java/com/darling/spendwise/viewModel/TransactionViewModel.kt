package com.darling.spendwise.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darling.spendwise.data.local.entity.TransactionEntity
import com.darling.spendwise.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository
): ViewModel(){

    val transactions = repository.getAllTransactions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTransaction(transaction: TransactionEntity){
        viewModelScope.launch {
            repository.insert(transaction)
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.update(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}