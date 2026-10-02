package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.WarningGold
import com.example.ui.viewmodel.RozanaViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.FormatUtils
import java.util.Calendar

@Composable
fun DashboardScreen(
    viewModel: RozanaViewModel,
    onOpenAddTransaction: (TransactionType) -> Unit,
    onOpenAddBill: () -> Unit,
    onOpenAddTask: () -> Unit,
    onOpenSetBudget: () -> Unit,
    onOpenVoiceDialog: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val bills by viewModel.allBills.collectAsState()
    val tasks by viewModel.allTasks.collectAsState()
    val budget by viewModel.currentBudget.collectAsState()

    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val greetingKey = when {
        hour < 12 -> "greeting_morning"
        hour < 17 -> "greeting_afternoon"
        else -> "greeting_evening"
    }

    // Today's boundaries
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

    // Exact calculations
    val todayIncome = transactions
        .filter { it.type == TransactionType.INCOME && it.timestamp >= todayStart }
        .sumOf { it.amount }

    val todayExpense = transactions
        .filter { it.type == TransactionType.EXPENSE && it.timestamp >= todayStart }
        .sumOf { it.amount }

    val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val remainingBalance = totalIncome - totalExpense

    val thisMonthExpense = transactions
        .filter { it.type == TransactionType.EXPENSE && it.timestamp >= monthStart }
        .sumOf { it.amount }

    val budgetAmount = budget?.monthlyBudget ?: 0.0
    val budgetProgress = if (budgetAmount > 0) (thisMonthExpense / budgetAmount).toFloat().coerceIn(0f, 1.2f) else 0f
    val isBudget80 = budgetAmount > 0 && thisMonthExpense >= (budgetAmount * 0.8) && thisMonthExpense <= budgetAmount
    val isBudgetExceeded = budgetAmount > 0 && thisMonthExpense > budgetAmount

    val pendingBills = bills.filter { !it.isPaid }.sortedBy { it.dueDate }
    val todaysTasks = tasks.filter { !it.isCompleted }.take(5)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // 1. Header Greeting & Date
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = RozanaStrings.get(greetingKey, language),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = FormatUtils.formatDate(System.currentTimeMillis(), language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(
                        onClick = onOpenVoiceDialog,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("voice_input_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // 2. Financial Summary Card (Remaining Balance, Today's Income & Expense)
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("financial_summary_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = RozanaStrings.get("remaining_balance", language),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = FormatUtils.formatPKR(remainingBalance, language),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (remainingBalance >= 0) MaterialTheme.colorScheme.primary else ExpenseRed
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Today Income
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(IncomeGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = IncomeGreen)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = RozanaStrings.get("today_income", language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = FormatUtils.formatPKR(todayIncome, language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IncomeGreen
                                )
                            }
                        }

                        // Today Expense
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ExpenseRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = ExpenseRed)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = RozanaStrings.get("today_expense", language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = FormatUtils.formatPKR(todayExpense, language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Monthly Budget Progress Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenSetBudget() }
                    .testTag("budget_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = RozanaStrings.get("monthly_budget", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (budgetAmount > 0) FormatUtils.formatPKR(budgetAmount, language) else "Tap to set",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { budgetProgress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = when {
                            isBudgetExceeded -> ExpenseRed
                            isBudget80 -> WarningGold
                            else -> MaterialTheme.colorScheme.primary
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${RozanaStrings.get("spent", language)}: ${FormatUtils.formatPKR(thisMonthExpense, language)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (budgetAmount > 0) {
                            val remaining = (budgetAmount - thisMonthExpense).coerceAtLeast(0.0)
                            Text(
                                text = "${RozanaStrings.get("remaining", language)}: ${FormatUtils.formatPKR(remaining, language)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (isBudgetExceeded) {
                        Spacer(modifier = Modifier.height(6.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text(RozanaStrings.get("budget_exceeded", language), color = ExpenseRed) },
                            leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = ExpenseRed) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = ExpenseRed.copy(alpha = 0.1f))
                        )
                    } else if (isBudget80) {
                        Spacer(modifier = Modifier.height(6.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text(RozanaStrings.get("budget_alert_80", language), color = WarningGold) },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = WarningGold) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = WarningGold.copy(alpha = 0.1f))
                        )
                    }
                }
            }
        }

        // 4. Quick Action Buttons (6 required actions)
        item {
            Text(
                text = RozanaStrings.get("quick_actions", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        title = RozanaStrings.get("add_income", language),
                        icon = Icons.Default.AddCircle,
                        color = IncomeGreen,
                        modifier = Modifier.weight(1f).testTag("action_add_income"),
                        onClick = { onOpenAddTransaction(TransactionType.INCOME) }
                    )
                    QuickActionButton(
                        title = RozanaStrings.get("add_expense", language),
                        icon = Icons.Default.RemoveCircle,
                        color = ExpenseRed,
                        modifier = Modifier.weight(1f).testTag("action_add_expense"),
                        onClick = { onOpenAddTransaction(TransactionType.EXPENSE) }
                    )
                    QuickActionButton(
                        title = RozanaStrings.get("add_bill", language),
                        icon = Icons.Default.Receipt,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f).testTag("action_add_bill"),
                        onClick = onOpenAddBill
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        title = RozanaStrings.get("add_task", language),
                        icon = Icons.Default.Checklist,
                        color = Color(0xFF7B1FA2),
                        modifier = Modifier.weight(1f).testTag("action_add_task"),
                        onClick = onOpenAddTask
                    )
                    QuickActionButton(
                        title = RozanaStrings.get("open_calculator", language),
                        icon = Icons.Default.Calculate,
                        color = Color(0xFF00796B),
                        modifier = Modifier.weight(1f).testTag("action_quick_calc"),
                        onClick = { viewModel.navigateTo(Screen.Calculator) }
                    )
                    QuickActionButton(
                        title = RozanaStrings.get("open_ai", language),
                        icon = Icons.Default.AutoAwesome,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f).testTag("action_quick_ai"),
                        onClick = { viewModel.navigateTo(Screen.AiAssistant) }
                    )
                }
            }
        }

        // 5. Upcoming Bill Reminders Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = RozanaStrings.get("upcoming_bills", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { viewModel.navigateTo(Screen.Bills) }) {
                    Text(RozanaStrings.get("all", language))
                }
            }

            if (pendingBills.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = RozanaStrings.get("no_upcoming_bills", language),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pendingBills.take(3).forEach { bill ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = bill.title, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "${RozanaStrings.get("due_on", language)}: ${FormatUtils.formatDate(bill.dueDate, language)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = FormatUtils.formatPKR(bill.amount, language),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    FilledTonalButton(
                                        onClick = { viewModel.toggleBillPaid(bill) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(RozanaStrings.get("mark_paid", language), style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Today's Tasks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = RozanaStrings.get("todays_tasks", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { viewModel.navigateTo(Screen.Tasks) }) {
                    Text(RozanaStrings.get("all", language))
                }
            }

            if (todaysTasks.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = RozanaStrings.get("no_tasks_today", language),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    todaysTasks.forEach { task ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { viewModel.toggleTaskComplete(task) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = task.title, fontWeight = FontWeight.SemiBold)
                                    if (task.timeString.isNotEmpty()) {
                                        Text(text = task.timeString, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}
