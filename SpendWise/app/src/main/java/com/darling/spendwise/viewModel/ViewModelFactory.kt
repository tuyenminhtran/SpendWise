package com.darling.spendwise.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.darling.spendwise.data.repository.TransactionRepository

class TransactionViewModelFactory(
    private val repository: TransactionRepository
): ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass:Class<T>):T{
        if (TransactionViewModel::class.java.isAssignableFrom(modelClass)){
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknow ViewModel Class")
    }
}