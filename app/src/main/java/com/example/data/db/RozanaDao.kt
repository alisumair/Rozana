package com.example.data.db

import androidx.room.*
import com.example.data.model.BillReminderEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.DocumentReminderEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear LIMIT 1")
    fun getBudgetForMonth(monthYear: String): Flow<BudgetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets")
    suspend fun deleteAllBudgets()
}

@Dao
interface BillReminderDao {
    @Query("SELECT * FROM bill_reminders ORDER BY dueDate ASC")
    fun getAllBills(): Flow<List<BillReminderEntity>>

    @Query("SELECT * FROM bill_reminders WHERE isPaid = 0 ORDER BY dueDate ASC")
    fun getPendingBills(): Flow<List<BillReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillReminderEntity): Long

    @Update
    suspend fun updateBill(bill: BillReminderEntity)

    @Delete
    suspend fun deleteBill(bill: BillReminderEntity)

    @Query("DELETE FROM bill_reminders")
    suspend fun deleteAllBills()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM daily_tasks ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM daily_tasks")
    suspend fun deleteAllTasks()
}

@Dao
interface DocumentReminderDao {
    @Query("SELECT * FROM document_reminders ORDER BY expiryDate ASC")
    fun getAllDocuments(): Flow<List<DocumentReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentReminderEntity): Long

    @Update
    suspend fun updateDocument(doc: DocumentReminderEntity)

    @Delete
    suspend fun deleteDocument(doc: DocumentReminderEntity)

    @Query("DELETE FROM document_reminders")
    suspend fun deleteAllDocuments()
}
