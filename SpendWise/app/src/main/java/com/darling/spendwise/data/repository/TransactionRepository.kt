package com.darling.spendwise.data.repository

import com.darling.spendwise.data.local.dao.TransactionDao
import com.darling.spendwise.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao
){
    fun getAllTransactions():Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }
    suspend fun insert(transaction: TransactionEntity){
        transactionDao.insert(transaction)
    }
    suspend fun update(transaction: TransactionEntity){
        transactionDao.update(transaction)
    }
    suspend fun delete(transaction: TransactionEntity){
        transactionDao.delete(transaction)
    }

    suspend fun deleteAll() {
        transactionDao.deleteAll()
    }
}