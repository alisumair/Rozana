package com.example.data.repository

import com.example.data.db.RozanaDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class RozanaRepository(private val database: RozanaDatabase) {
    private val transactionDao = database.transactionDao()
    private val budgetDao = database.budgetDao()
    private val billDao = database.billReminderDao()
    private val taskDao = database.taskDao()
    private val docDao = database.documentReminderDao()

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allBills: Flow<List<BillReminderEntity>> = billDao.getAllBills()
    val pendingBills: Flow<List<BillReminderEntity>> = billDao.getPendingBills()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val allDocuments: Flow<List<DocumentReminderEntity>> = docDao.getAllDocuments()

    fun getTransactionsBetween(start: Long, end: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsBetween(start, end)

    fun getBudgetForMonth(monthYear: String): Flow<BudgetEntity?> =
        budgetDao.getBudgetForMonth(monthYear)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        transactionDao.deleteTransaction(transaction)

    suspend fun setBudget(budget: BudgetEntity) =
        budgetDao.insertOrUpdateBudget(budget)

    suspend fun insertBill(bill: BillReminderEntity): Long =
        billDao.insertBill(bill)

    suspend fun updateBill(bill: BillReminderEntity) =
        billDao.updateBill(bill)

    suspend fun deleteBill(bill: BillReminderEntity) =
        billDao.deleteBill(bill)

    suspend fun insertTask(task: TaskEntity): Long =
        taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) =
        taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) =
        taskDao.deleteTask(task)

    suspend fun insertDocument(doc: DocumentReminderEntity): Long =
        docDao.insertDocument(doc)

    suspend fun updateDocument(doc: DocumentReminderEntity) =
        docDao.updateDocument(doc)

    suspend fun deleteDocument(doc: DocumentReminderEntity) =
        docDao.deleteDocument(doc)

    suspend fun clearAllData() {
        transactionDao.deleteAllTransactions()
        budgetDao.deleteAllBudgets()
        billDao.deleteAllBills()
        taskDao.deleteAllTasks()
        docDao.deleteAllDocuments()
    }

    suspend fun loadSampleData() {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        // Sample Transactions
        val sampleTxs = listOf(
            TransactionEntity(
                type = TransactionType.INCOME,
                amount = 75000.0,
                category = IncomeCategory.SALARY.name,
                description = "Monthly Salary (تنخواہ)",
                timestamp = now - (2 * 86400000L),
                paymentMethod = "Bank Transfer"
            ),
            TransactionEntity(
                type = TransactionType.EXPENSE,
                amount = 3500.0,
                category = ExpenseCategory.PETROL.name,
                description = "Bike & Car Petrol (پیٹرول)",
                timestamp = now - (1 * 86400000L),
                paymentMethod = "Cash"
            ),
            TransactionEntity(
                type = TransactionType.EXPENSE,
                amount = 8200.0,
                category = ExpenseCategory.FOOD.name,
                description = "Monthly Grocery & Ration (راشن)",
                timestamp = now - (1 * 86400000L),
                paymentMethod = "EasyPaisa"
            ),
            TransactionEntity(
                type = TransactionType.EXPENSE,
                amount = 450.0,
                category = ExpenseCategory.FOOD.name,
                description = "Chai & Samosa Refreshment",
                timestamp = now - 3600000L,
                paymentMethod = "Cash"
            ),
            TransactionEntity(
                type = TransactionType.INCOME,
                amount = 12000.0,
                category = IncomeCategory.FREELANCING.name,
                description = "Graphic Design Project (فری لانسنگ)",
                timestamp = now - 7200000L,
                paymentMethod = "JazzCash"
            )
        )
        sampleTxs.forEach { insertTransaction(it) }

        // Monthly Budget
        cal.timeInMillis = now
        val currentMonthYear = String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        setBudget(BudgetEntity(monthYear = currentMonthYear, monthlyBudget = 60000.0))

        // Sample Bills
        val sampleBills = listOf(
            BillReminderEntity(
                title = "Electricity Bill (LESCO / K-Electric)",
                amount = 6850.0,
                dueDate = now + (3 * 86400000L),
                category = "Electricity",
                notes = "Reference # 14-12345-67890",
                isPaid = false
            ),
            BillReminderEntity(
                title = "Sui Northern Gas Bill (SNGPL)",
                amount = 1450.0,
                dueDate = now + (7 * 86400000L),
                category = "Gas",
                notes = "Consumer # 9876543",
                isPaid = false
            ),
            BillReminderEntity(
                title = "Nayatel / PTCL Fiber Internet",
                amount = 3200.0,
                dueDate = now + (10 * 86400000L),
                category = "Internet",
                notes = "50 Mbps Package",
                isPaid = true,
                paidDate = now - (5 * 86400000L)
            )
        )
        sampleBills.forEach { insertBill(it) }

        // Sample Tasks
        val sampleTasks = listOf(
            TaskEntity(
                title = "Buy groceries & medicine from medical store",
                dueDate = now,
                timeString = "05:00 PM",
                notes = "Panadol, milk, bread, sugar",
                isCompleted = false,
                priority = TaskPriority.HIGH
            ),
            TaskEntity(
                title = "Submit vehicle token tax online on e-Pay Punjab / Sindh",
                dueDate = now + 86400000L,
                timeString = "11:00 AM",
                notes = "Challan # 839201",
                isCompleted = false,
                priority = TaskPriority.MEDIUM
            ),
            TaskEntity(
                title = "Deposit school fee via JazzCash / EasyPaisa",
                dueDate = now - 86400000L,
                timeString = "02:00 PM",
                notes = "Challan paid",
                isCompleted = true,
                priority = TaskPriority.HIGH
            )
        )
        sampleTasks.forEach { insertTask(it) }

        // Sample Documents
        val sampleDocs = listOf(
            DocumentReminderEntity(
                title = "NADRA Smart CNIC",
                docType = DocumentType.CNIC,
                docNumber = "35201-XXXXXXX-1",
                expiryDate = now + (180 * 86400000L),
                notes = "Renew before expiry at NADRA Mega Center"
            ),
            DocumentReminderEntity(
                title = "City Traffic Police Driving License",
                docType = DocumentType.DRIVING_LICENSE,
                docNumber = "DL-LHR-2019-8472",
                expiryDate = now + (45 * 86400000L),
                notes = "Medical certificate needed for renewal"
            )
        )
        sampleDocs.forEach { insertDocument(it) }
    }
}
