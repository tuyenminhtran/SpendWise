package com.darling.spendwise.data.local.dao

import androidx.room.*
import com.darling.spendwise.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import com.darling.spendwise.data.local.database.AppDatabase


@Dao
interface TransactionDao{
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}