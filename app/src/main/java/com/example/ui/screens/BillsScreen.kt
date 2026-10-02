package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.BillReminderEntity
import com.example.data.model.RozanaStrings
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.WarningGold
import com.example.ui.viewmodel.RozanaViewModel
import com.example.util.FormatUtils
import com.example.util.NotificationHelper

@Composable
fun BillsScreen(
    viewModel: RozanaViewModel,
    onAddBill: () -> Unit
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val bills by viewModel.allBills.collectAsState()

    var showOnlyPending by remember { mutableStateOf(true) }
    var billToDelete by remember { mutableStateOf<BillReminderEntity?>(null) }

    val displayedBills = remember(bills, showOnlyPending) {
        if (showOnlyPending) bills.filter { !it.isPaid } else bills
    }

    val totalPendingAmount = bills.filter { !it.isPaid }.sumOf { it.amount }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBill,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_bill")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Bill")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Pending Total Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.URDU) "کل واجب الادا بلز" else "Total Pending Bills",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = FormatUtils.formatPKR(totalPendingAmount, language),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = showOnlyPending,
                    onClick = { showOnlyPending = true },
                    label = { Text("${RozanaStrings.get("unpaid", language)} (${bills.count { !it.isPaid }})") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = !showOnlyPending,
                    onClick = { showOnlyPending = false },
                    label = { Text("${RozanaStrings.get("all", language)} (${bills.size})") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (displayedBills.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showOnlyPending) RozanaStrings.get("no_upcoming_bills", language) else "No bills added yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(displayedBills, key = { it.id }) { bill ->
                        val daysRemaining = FormatUtils.getDaysDifference(bill.dueDate)
                        val isOverdue = !bill.isPaid && daysRemaining < 0
                        val isDueSoon = !bill.isPaid && daysRemaining in 0..3

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = bill.title,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "${RozanaStrings.get("due_on", language)}: ${FormatUtils.formatDate(bill.dueDate, language)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (bill.notes.isNotEmpty()) {
                                            Text(
                                                text = bill.notes,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = FormatUtils.formatPKR(bill.amount, language),
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (bill.isPaid) IncomeGreen else ExpenseRed
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Status Chip
                                        val statusText = when {
                                            bill.isPaid -> RozanaStrings.get("paid", language)
                                            isOverdue -> "Overdue (${-daysRemaining}d)"
                                            isDueSoon -> "Due in ${daysRemaining}d"
                                            else -> "Upcoming (${daysRemaining}d)"
                                        }
                                        val statusColor = when {
                                            bill.isPaid -> IncomeGreen
                                            isOverdue -> ExpenseRed
                                            isDueSoon -> WarningGold
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(statusText, color = statusColor, style = MaterialTheme.typography.labelSmall) },
                                            colors = AssistChipDefaults.assistChipColors(containerColor = statusColor.copy(alpha = 0.1f))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Notification reminder test button
                                    TextButton(
                                        onClick = {
                                            NotificationHelper.showBillReminderNotification(
                                                context,
                                                bill.id.toInt(),
                                                bill.title,
                                                bill.amount,
                                                FormatUtils.formatDate(bill.dueDate, language)
                                            )
                                            Toast.makeText(context, "Notification sent for ${bill.title}", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Test Alert", style = MaterialTheme.typography.labelSmall)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { billToDelete = bill }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Button(
                                            onClick = { viewModel.toggleBillPaid(bill) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (bill.isPaid) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                                                contentColor = if (bill.isPaid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                                            )
                                        ) {
                                            Text(if (bill.isPaid) "Mark Unpaid" else RozanaStrings.get("mark_paid", language))
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

    if (billToDelete != null) {
        AlertDialog(
            onDismissRequest = { billToDelete = null },
            title = { Text(RozanaStrings.get("delete", language)) },
            text = { Text(RozanaStrings.get("delete_confirm", language)) },
            confirmButton = {
                Button(
                    onClick = {
                        billToDelete?.let { viewModel.deleteBill(it) }
                        billToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(RozanaStrings.get("delete", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { billToDelete = null }) { Text(RozanaStrings.get("cancel", language)) }
            }
        )
    }
}
