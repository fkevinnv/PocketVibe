package com.pocketvibe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query(
        """SELECT * FROM transactions
           WHERE timestamp >= :monthStart AND timestamp < :monthEnd
           ORDER BY timestamp DESC"""
    )
    fun observeByMonth(monthStart: Long, monthEnd: Long): Flow<List<TransactionEntity>>

    @Query(
        """SELECT
             COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount END), 0) AS income,
             COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount END), 0) AS expense
           FROM transactions WHERE timestamp >= :monthStart AND timestamp < :monthEnd"""
    )
    fun observeMonthlyTotals(monthStart: Long, monthEnd: Long): Flow<MonthlyTotals>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tx: TransactionEntity): Long

    @Delete
    suspend fun delete(tx: TransactionEntity)

    data class MonthlyTotals(val income: Double, val expense: Double)
}

@Dao
interface WishDao {

    @Query("SELECT * FROM wish_items ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<WishItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: WishItemEntity): Long

    @Delete
    suspend fun delete(item: WishItemEntity)

    @Query("UPDATE wish_items SET savedAmount = savedAmount + :amount WHERE id = :id")
    suspend fun contribute(id: Long, amount: Double)
}
