package com.darling.spendwise.data.local.dao

import androidx.room.*
import com.darling.spendwise.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallets")
    fun getAll(): Flow<List<WalletEntity>>

    @Insert
    suspend fun insert(wallet: WalletEntity)

    @Delete
    suspend fun delete(wallet: WalletEntity)
}