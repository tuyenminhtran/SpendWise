package com.darling.spendwise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darling.spendwise.data.local.dao.TransactionDao
import com.darling.spendwise.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase(){
    abstract fun transactionDao(): TransactionDao
}

