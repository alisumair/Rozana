package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.RozanaDatabase
import com.example.data.model.*
import com.example.data.repository.RozanaRepository
import com.example.util.FormatUtils
import com.example.util.ParsedCandidate
import com.example.util.VoiceParser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class AiChatMessage(
    val id: Long = System.currentTimeMillis(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Finance : Screen("finance")
    data object Bills : Screen("bills")
    data object Tasks : Screen("tasks")
    data object Documents : Screen("documents")
    data object Calculator : Screen("calculator")
    data object AiAssistant : Screen("ai_assistant")
    data object Reports : Screen("reports")
    data object Settings : Screen("settings")
}

class RozanaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RozanaRepository(RozanaDatabase.getDatabase(application))

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    val allTransactions = repository.allTransactions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allBills = repository.allBills.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allTasks = repository.allTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allDocuments = repository.allDocuments.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val currentMonthYear = FormatUtils.getCurrentMonthYear()
    val currentBudget = repository.getBudgetForMonth(currentMonthYear).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    // AI Messages
    private val _aiMessages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                isUser = false,
                text = "السلام علیکم! میں روزانہ کا اسمارٹ اسسٹنٹ ہوں۔ آپ مجھ سے اپنے اخراجات، آمدنی، بجٹ اور بلز کے بارے میں اردو، رومن اردو یا انگلش میں پوچھ سکتے ہیں۔\n\nHi! I'm your Rozana Assistant. Ask me about your income, expenses, budget, or bills."
            )
        )
    )
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    // Voice Dialog parsed candidates
    private val _pendingVoiceCandidates = MutableStateFlow<List<ParsedCandidate>>(emptyList())
    val pendingVoiceCandidates: StateFlow<List<ParsedCandidate>> = _pendingVoiceCandidates.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.URDU else AppLanguage.ENGLISH
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun setDarkMode(dark: Boolean?) {
        _isDarkMode.value = dark
    }

    // Transaction CRUD
    fun addTransaction(type: TransactionType, amount: Double, category: String, description: String, timestamp: Long, method: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    type = type,
                    amount = amount,
                    category = category,
                    description = description,
                    timestamp = timestamp,
                    paymentMethod = method
                )
            )
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // Budget
    fun setMonthlyBudget(amount: Double) {
        viewModelScope.launch {
            repository.setBudget(
                BudgetEntity(
                    monthYear = currentMonthYear,
                    monthlyBudget = amount
                )
            )
        }
    }

    // Bills
    fun addBill(title: String, amount: Double, dueDate: Long, category: String, notes: String) {
        viewModelScope.launch {
            repository.insertBill(
                BillReminderEntity(
                    title = title,
                    amount = amount,
                    dueDate = dueDate,
                    category = category,
                    notes = notes,
                    isPaid = false
                )
            )
        }
    }

    fun toggleBillPaid(bill: BillReminderEntity) {
        viewModelScope.launch {
            val updated = bill.copy(
                isPaid = !bill.isPaid,
                paidDate = if (!bill.isPaid) System.currentTimeMillis() else null
            )
            repository.updateBill(updated)
        }
    }

    fun deleteBill(bill: BillReminderEntity) {
        viewModelScope.launch {
            repository.deleteBill(bill)
        }
    }

    // Tasks
    fun addTask(title: String, dueDate: Long, timeString: String, notes: String, priority: TaskPriority) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title,
                    dueDate = dueDate,
                    timeString = timeString,
                    notes = notes,
                    isCompleted = false,
                    priority = priority
                )
            )
        }
    }

    fun toggleTaskComplete(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Documents
    fun addDocument(title: String, docType: DocumentType, docNumber: String, expiryDate: Long, notes: String) {
        viewModelScope.launch {
            repository.insertDocument(
                DocumentReminderEntity(
                    title = title,
                    docType = docType,
                    docNumber = docNumber,
                    expiryDate = expiryDate,
                    notes = notes
                )
            )
        }
    }

    fun deleteDocument(doc: DocumentReminderEntity) {
        viewModelScope.launch {
            repository.deleteDocument(doc)
        }
    }

    // Sample Data & Reset
    fun loadSampleData() {
        viewModelScope.launch {
            repository.loadSampleData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    // Voice & Natural Text Parser
    fun parseVoiceInput(text: String) {
        val candidates = VoiceParser.parseInput(text)
        _pendingVoiceCandidates.value = candidates
    }

    fun confirmVoiceCandidates(candidates: List<ParsedCandidate>) {
        viewModelScope.launch {
            candidates.forEach { candidate ->
                repository.insertTransaction(
                    TransactionEntity(
                        type = candidate.type,
                        amount = candidate.amount,
                        category = candidate.category,
                        description = candidate.description,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            _pendingVoiceCandidates.value = emptyList()
        }
    }

    fun clearVoiceCandidates() {
        _pendingVoiceCandidates.value = emptyList()
    }

    // AI Query Processor based on actual user data
    fun askAiAssistant(query: String) {
        val userMsg = AiChatMessage(isUser = true, text = query)
        _aiMessages.value = _aiMessages.value + userMsg

        val transactions = allTransactions.value
        val bills = allBills.value
        val tasks = allTasks.value
        val budget = currentBudget.value

        val now = Calendar.getInstance()
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val monthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE && it.timestamp >= todayStart }
            .sumOf { it.amount }

        val todayIncomes = transactions
            .filter { it.type == TransactionType.INCOME && it.timestamp >= todayStart }
            .sumOf { it.amount }

        val monthExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE && it.timestamp >= monthStart }
            .sumOf { it.amount }

        val monthIncomes = transactions
            .filter { it.type == TransactionType.INCOME && it.timestamp >= monthStart }
            .sumOf { it.amount }

        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        val pendingBills = bills.filter { !it.isPaid }
        val pendingTasks = tasks.filter { !it.isCompleted }

        val lowerQuery = query.lowercase().trim()
        val lang = _language.value

        val responseText = when {
            // Expenses today
            lowerQuery.contains("aj") && (lowerQuery.contains("kharch") || lowerQuery.contains("expense") || lowerQuery.contains("spent")) -> {
                if (lang == AppLanguage.URDU) {
                    "آج آپ کا کل خرچہ ${FormatUtils.formatPKR(todayExpenses, lang)} ہے۔"
                } else {
                    "Today's total expense is ${FormatUtils.formatPKR(todayExpenses, lang)}."
                }
            }
            // Income today
            lowerQuery.contains("aj") && (lowerQuery.contains("income") || lowerQuery.contains("kamay") || lowerQuery.contains("kamai")) -> {
                if (lang == AppLanguage.URDU) {
                    "آج آپ کی کل آمدنی ${FormatUtils.formatPKR(todayIncomes, lang)} ہے۔"
                } else {
                    "Today's total income is ${FormatUtils.formatPKR(todayIncomes, lang)}."
                }
            }
            // Month expense
            (lowerQuery.contains("month") || lowerQuery.contains("mah") || lowerQuery.contains("mahina")) &&
                    (lowerQuery.contains("kharch") || lowerQuery.contains("expense") || lowerQuery.contains("total")) -> {
                if (lang == AppLanguage.URDU) {
                    "اس مہینے کا کل خرچہ ${FormatUtils.formatPKR(monthExpenses, lang)} ہے اور آمدنی ${FormatUtils.formatPKR(monthIncomes, lang)} ہے۔"
                } else {
                    "This month's total expense is ${FormatUtils.formatPKR(monthExpenses, lang)} against an income of ${FormatUtils.formatPKR(monthIncomes, lang)}."
                }
            }
            // Budget inquiry
            lowerQuery.contains("budget") || lowerQuery.contains("بجٹ") -> {
                val budgetAmount = budget?.monthlyBudget ?: 0.0
                if (budgetAmount <= 0.0) {
                    if (lang == AppLanguage.URDU) "آپ نے ابھی اس ماہ کا بجٹ مقرر نہیں کیا۔ آپ سیٹنگز یا ڈیش بورڈ سے بجٹ سیٹ کر سکتے ہیں۔"
                    else "You haven't set a budget for this month yet. You can set it from the Dashboard."
                } else {
                    val remaining = budgetAmount - monthExpenses
                    if (remaining >= 0) {
                        if (lang == AppLanguage.URDU)
                            "اس ماہ کا بجٹ ${FormatUtils.formatPKR(budgetAmount, lang)} ہے۔ خرچ شدہ: ${FormatUtils.formatPKR(monthExpenses, lang)}، باقی بجٹ: ${FormatUtils.formatPKR(remaining, lang)}۔"
                        else
                            "Monthly budget: ${FormatUtils.formatPKR(budgetAmount, lang)}. Spent: ${FormatUtils.formatPKR(monthExpenses, lang)}. Remaining: ${FormatUtils.formatPKR(remaining, lang)}."
                    } else {
                        if (lang == AppLanguage.URDU)
                            "انتباہ! آپ کا بجٹ ختم ہوچکا ہے اور آپ ${FormatUtils.formatPKR(-remaining, lang)} زیادہ خرچ کر چکے ہیں!"
                        else
                            "Warning! Budget exceeded by ${FormatUtils.formatPKR(-remaining, lang)}!"
                    }
                }
            }
            // Balance inquiry
            lowerQuery.contains("balance") || lowerQuery.contains("baqi") || lowerQuery.contains("باقی") -> {
                if (lang == AppLanguage.URDU) {
                    "آپ کا کل موجودہ بیلنس ${FormatUtils.formatPKR(balance, lang)} ہے۔ (کل آمدنی: ${FormatUtils.formatPKR(totalIncome, lang)}، کل اخراجات: ${FormatUtils.formatPKR(totalExpense, lang)})"
                } else {
                    "Your current remaining balance is ${FormatUtils.formatPKR(balance, lang)} (Total Income: ${FormatUtils.formatPKR(totalIncome, lang)}, Total Expenses: ${FormatUtils.formatPKR(totalExpense, lang)})."
                }
            }
            // Bills inquiry
            lowerQuery.contains("bill") || lowerQuery.contains("بل") -> {
                if (pendingBills.isEmpty()) {
                    if (lang == AppLanguage.URDU) "ماشاءاللہ، آپ کے تمام بلز ادا شدہ ہیں!"
                    else "All your bills are paid! No pending bills."
                } else {
                    val listStr = pendingBills.joinToString(", ") { "${it.title}: ${FormatUtils.formatPKR(it.amount, lang)}" }
                    if (lang == AppLanguage.URDU) "آپ کے ${pendingBills.size} غیر ادا شدہ بلز ہیں: $listStr"
                    else "You have ${pendingBills.size} pending bill(s): $listStr"
                }
            }
            // Tasks inquiry
            lowerQuery.contains("task") || lowerQuery.contains("kam") || lowerQuery.contains("کام") -> {
                if (pendingTasks.isEmpty()) {
                    if (lang == AppLanguage.URDU) "آج کے تمام کام مکمل ہیں!"
                    else "All tasks are complete! Great job."
                } else {
                    val listStr = pendingTasks.take(3).joinToString(", ") { it.title }
                    if (lang == AppLanguage.URDU) "آپ کے ${pendingTasks.size} کام باقی ہیں، مثلاً: $listStr"
                    else "You have ${pendingTasks.size} pending task(s): $listStr"
                }
            }
            else -> {
                if (lang == AppLanguage.URDU) {
                    "میں نے آپ کا سوال نوٹ کر لیا ہے۔ آپ مجھ سے آج کے خرچے، آمدنی، ماہانہ بجٹ، غیر ادا شدہ بلز یا کاموں کے بارے میں پوچھ سکتے ہیں، مثلاً: 'آج کتنا خرچہ ہوا؟' یا 'میرا بجٹ کتنا باقی ہے؟'"
                } else {
                    "I am tracking your actual records. You can ask me questions like: 'Aj kitny paisay kharch kiye?', 'What is my remaining budget?', 'Are my bills due?', or 'Today's tasks'."
                }
            }
        }

        val aiReply = AiChatMessage(isUser = false, text = responseText)
        _aiMessages.value = _aiMessages.value + aiReply
    }
}
