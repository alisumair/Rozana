package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.model.AppLanguage
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.RozanaTheme
import com.example.ui.viewmodel.RozanaViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    private val viewModel: RozanaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize local notification channels safely
        try {
            NotificationHelper.initNotificationChannels(this)
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Notification channels init skipped: ${e.message}")
        }

        setContent {
            val language by viewModel.language.collectAsState()
            val userDarkMode by viewModel.isDarkMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val pendingVoiceCandidates by viewModel.pendingVoiceCandidates.collectAsState()
            val budget by viewModel.currentBudget.collectAsState()

            val isDark = userDarkMode ?: isSystemInDarkTheme()
            val layoutDirection = if (language == AppLanguage.URDU) LayoutDirection.Rtl else LayoutDirection.Ltr

            // Dialog states
            var showAddTransactionDialog by remember { mutableStateOf(false) }
            var transactionTypeForDialog by remember { mutableStateOf(TransactionType.EXPENSE) }
            var transactionToEdit by remember { mutableStateOf<TransactionEntity?>(null) }
            var showAddBillDialog by remember { mutableStateOf(false) }
            var showAddTaskDialog by remember { mutableStateOf(false) }
            var showAddDocDialog by remember { mutableStateOf(false) }
            var showSetBudgetDialog by remember { mutableStateOf(false) }
            var showVoiceInputDialog by remember { mutableStateOf(false) }

            // Back navigation handling
            BackHandler(enabled = currentScreen != Screen.Dashboard) {
                viewModel.navigateTo(Screen.Dashboard)
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                RozanaTheme(darkTheme = isDark) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            RozanaTopBar(
                                currentScreen = currentScreen,
                                language = language,
                                isDarkMode = isDark,
                                onToggleTheme = {
                                    viewModel.setDarkMode(!isDark)
                                },
                                onToggleLanguage = {
                                    viewModel.toggleLanguage()
                                },
                                onOpenCalculator = {
                                    viewModel.navigateTo(Screen.Calculator)
                                },
                                onOpenAi = {
                                    viewModel.navigateTo(Screen.AiAssistant)
                                }
                            )
                        },
                        bottomBar = {
                            RozanaBottomBar(
                                currentScreen = currentScreen,
                                language = language,
                                onSelectScreen = { screen ->
                                    viewModel.navigateTo(screen)
                                }
                            )
                        }
                    ) { innerPadding ->
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                Screen.Dashboard -> {
                                    DashboardScreen(
                                        viewModel = viewModel,
                                        onOpenAddTransaction = { type ->
                                            transactionTypeForDialog = type
                                            transactionToEdit = null
                                            showAddTransactionDialog = true
                                        },
                                        onOpenAddBill = { showAddBillDialog = true },
                                        onOpenAddTask = { showAddTaskDialog = true },
                                        onOpenSetBudget = { showSetBudgetDialog = true },
                                        onOpenVoiceDialog = { showVoiceInputDialog = true }
                                    )
                                }
                                Screen.Finance -> {
                                    TransactionsScreen(
                                        viewModel = viewModel,
                                        onAddTransaction = { type ->
                                            transactionTypeForDialog = type
                                            transactionToEdit = null
                                            showAddTransactionDialog = true
                                        },
                                        onEditTransaction = { tx ->
                                            transactionToEdit = tx
                                            transactionTypeForDialog = tx.type
                                            showAddTransactionDialog = true
                                        }
                                    )
                                }
                                Screen.Bills -> {
                                    BillsScreen(
                                        viewModel = viewModel,
                                        onAddBill = { showAddBillDialog = true }
                                    )
                                }
                                Screen.Tasks -> {
                                    TasksScreen(
                                        viewModel = viewModel,
                                        onAddTask = { showAddTaskDialog = true }
                                    )
                                }
                                Screen.Documents -> {
                                    DocumentsScreen(
                                        viewModel = viewModel,
                                        onAddDocument = { showAddDocDialog = true }
                                    )
                                }
                                Screen.Calculator -> {
                                    CalculatorScreen(
                                        viewModel = viewModel,
                                        onSendToTransaction = { type, amount ->
                                            viewModel.addTransaction(
                                                type = type,
                                                amount = amount,
                                                category = if (type == TransactionType.INCOME) "Other Income" else "Other Expense",
                                                description = "From Calculator",
                                                timestamp = System.currentTimeMillis(),
                                                method = "Cash"
                                            )
                                            viewModel.navigateTo(Screen.Finance)
                                        }
                                    )
                                }
                                Screen.AiAssistant -> {
                                    AiAssistantScreen(viewModel = viewModel)
                                }
                                Screen.Reports -> {
                                    ReportsScreen(viewModel = viewModel)
                                }
                                Screen.Settings -> {
                                    SettingsScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }

                    // Dialogs
                    if (showAddTransactionDialog) {
                        AddEditTransactionDialog(
                            initialType = transactionTypeForDialog,
                            transactionToEdit = transactionToEdit,
                            language = language,
                            onDismiss = {
                                showAddTransactionDialog = false
                                transactionToEdit = null
                            },
                            onSave = { type, amount, category, desc, timestamp, method ->
                                if (transactionToEdit == null) {
                                    viewModel.addTransaction(type, amount, category, desc, timestamp, method)
                                } else {
                                    viewModel.updateTransaction(
                                        transactionToEdit!!.copy(
                                            type = type,
                                            amount = amount,
                                            category = category,
                                            description = desc,
                                            timestamp = timestamp,
                                            paymentMethod = method
                                        )
                                    )
                                }
                                showAddTransactionDialog = false
                                transactionToEdit = null
                            }
                        )
                    }

                    if (showAddBillDialog) {
                        AddEditBillDialog(
                            language = language,
                            onDismiss = { showAddBillDialog = false },
                            onSave = { title, amount, dueDate, category, notes ->
                                viewModel.addBill(title, amount, dueDate, category, notes)
                                showAddBillDialog = false
                            }
                        )
                    }

                    if (showAddTaskDialog) {
                        AddEditTaskDialog(
                            language = language,
                            onDismiss = { showAddTaskDialog = false },
                            onSave = { title, dueDate, timeString, notes, priority ->
                                viewModel.addTask(title, dueDate, timeString, notes, priority)
                                showAddTaskDialog = false
                            }
                        )
                    }

                    if (showAddDocDialog) {
                        AddEditDocumentDialog(
                            language = language,
                            onDismiss = { showAddDocDialog = false },
                            onSave = { title, docType, docNumber, expiryDate, notes ->
                                viewModel.addDocument(title, docType, docNumber, expiryDate, notes)
                                showAddDocDialog = false
                            }
                        )
                    }

                    if (showSetBudgetDialog) {
                        SetBudgetDialog(
                            currentBudgetAmount = budget?.monthlyBudget ?: 0.0,
                            language = language,
                            onDismiss = { showSetBudgetDialog = false },
                            onSave = { amount ->
                                viewModel.setMonthlyBudget(amount)
                                showSetBudgetDialog = false
                            }
                        )
                    }

                    if (showVoiceInputDialog) {
                        VoiceInputDialog(
                            language = language,
                            onDismiss = { showVoiceInputDialog = false },
                            onParseText = { text ->
                                showVoiceInputDialog = false
                                viewModel.parseVoiceInput(text)
                            }
                        )
                    }

                    if (pendingVoiceCandidates.isNotEmpty()) {
                        VoiceConfirmationDialog(
                            candidates = pendingVoiceCandidates,
                            language = language,
                            onConfirm = { candidates ->
                                viewModel.confirmVoiceCandidates(candidates)
                            },
                            onDismiss = {
                                viewModel.clearVoiceCandidates()
                            }
                        )
                    }
                }
            }
        }
    }
}
