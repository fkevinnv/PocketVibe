package com.pocketvibe.data

import com.pocketvibe.data.local.AppDatabase
import com.pocketvibe.data.local.TransactionDao
import com.pocketvibe.data.local.TransactionEntity
import com.pocketvibe.data.local.WishItemEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TransactionRepository(db: AppDatabase) {

    private val txDao = db.transactionDao()
    private val wishDao = db.wishDao()

    // ---- Transacciones ----
    val allTransactions: Flow<List<TransactionEntity>> = txDao.observeAll()

    fun transactionsFor(year: Int, month: Int): Flow<List<TransactionEntity>> {
        val (start, end) = monthRange(year, month)
        return txDao.observeByMonth(start, end)
    }

    fun monthlyTotals(year: Int, month: Int): Flow<TransactionDao.MonthlyTotals> {
        val (start, end) = monthRange(year, month)
        return txDao.observeMonthlyTotals(start, end)
    }

    suspend fun addTransaction(tx: TransactionEntity) = txDao.upsert(tx)

    suspend fun deleteTransaction(tx: TransactionEntity) = txDao.delete(tx)

    // ---- Wishlist ----
    val wishItems: Flow<List<WishItemEntity>> = wishDao.observeAll()

    suspend fun saveWish(item: WishItemEntity) = wishDao.upsert(item)

    suspend fun deleteWish(item: WishItemEntity) = wishDao.delete(item)

    suspend fun contributeToWish(id: Long, amount: Double) = wishDao.contribute(id, amount)

    private fun monthRange(year: Int, month: Int): Pair<Long, Long> {
        fun cal(m: Int): Long = Calendar.getInstance().apply {
            set(year, m, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return cal(month - 1) to cal(month)
    }
}
