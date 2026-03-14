package com.darling.spendwise.data.local.dao

import androidx.room.*
import com.darling.spendwise.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun getAll(): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insert(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)
}