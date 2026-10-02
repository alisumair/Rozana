package com.example.data.db

import android.content.Context
import androidx.room.*
import com.example.data.model.*

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = try {
        TransactionType.valueOf(value)
    } catch (_: Exception) {
        TransactionType.EXPENSE
    }

    @TypeConverter
    fun fromTaskPriority(value: TaskPriority): String = value.name

    @TypeConverter
    fun toTaskPriority(value: String): TaskPriority = try {
        TaskPriority.valueOf(value)
    } catch (_: Exception) {
        TaskPriority.MEDIUM
    }

    @TypeConverter
    fun fromDocumentType(value: DocumentType): String = value.name

    @TypeConverter
    fun toDocumentType(value: String): DocumentType = DocumentType.fromString(value)
}

@Database(
    entities = [
        TransactionEntity::class,
        BudgetEntity::class,
        BillReminderEntity::class,
        TaskEntity::class,
        DocumentReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RozanaDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun billReminderDao(): BillReminderDao
    abstract fun taskDao(): TaskDao
    abstract fun documentReminderDao(): DocumentReminderDao

    companion object {
        @Volatile
        private var INSTANCE: RozanaDatabase? = null

        fun getDatabase(context: Context): RozanaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RozanaDatabase::class.java,
                    "rozana_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
