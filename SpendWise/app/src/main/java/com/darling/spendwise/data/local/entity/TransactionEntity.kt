package com.darling.spendwise.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)

    val id: Int = 0,

    val amount: Double,     // số tiền
    val type: String,       // thu nhập với chi tiêu
    val categoryId: Int,    // id danh mục
    val walletId: Int,      // id ví
    val note: String,       // ghi chú
    val date: Long          // timestamp
)