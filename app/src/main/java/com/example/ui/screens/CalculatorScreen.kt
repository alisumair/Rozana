package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.RozanaStrings
import com.example.data.model.TransactionType
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.RozanaViewModel
import com.example.ui.viewmodel.Screen
import java.text.DecimalFormat

@Composable
fun CalculatorScreen(
    viewModel: RozanaViewModel,
    onSendToTransaction: (type: TransactionType, amount: Double) -> Unit
) {
    val language by viewModel.language.collectAsState()

    var expression by remember { mutableStateOf("") }
    var currentInput by remember { mutableStateOf("0") }
    var previousValue by remember { mutableDoubleStateOf(0.0) }
    var pendingOp by remember { mutableStateOf<String?>(null) }
    var isNewNumber by remember { mutableStateOf(true) }

    fun calculateResult(): Double {
        val current = currentInput.toDoubleOrNull() ?: 0.0
        return when (pendingOp) {
            "+" -> previousValue + current
            "-" -> previousValue - current
            "×" -> previousValue * current
            "÷" -> if (current != 0.0) previousValue / current else 0.0
            "%" -> previousValue * (current / 100.0)
            else -> current
        }
    }

    val displayFormatter = DecimalFormat("#,##0.##")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Back Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = RozanaStrings.get("calc_title", language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Display Screen
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                if (expression.isNotEmpty()) {
                    Text(
                        text = expression,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = currentInput,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.testTag("calc_display")
                )
            }
        }

        // Direct "Send to Accounts" Shortcut Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val amount = currentInput.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onSendToTransaction(TransactionType.EXPENSE, amount)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                modifier = Modifier.weight(1f).testTag("calc_send_expense")
            ) {
                Text(RozanaStrings.get("calc_send_expense", language), style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = {
                    val amount = currentInput.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onSendToTransaction(TransactionType.INCOME, amount)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                modifier = Modifier.weight(1f).testTag("calc_send_income")
            ) {
                Text(RozanaStrings.get("calc_send_income", language), style = MaterialTheme.typography.labelSmall)
            }
        }

        // Keypad Grid (5 rows of 4 buttons)
        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("⌫", "0", ".", "=")
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { btn ->
                        val isOp = btn in listOf("÷", "×", "-", "+", "=")
                        val isSpecial = btn in listOf("C", "±", "%", "⌫")

                        FilledTonalButton(
                            onClick = {
                                when (btn) {
                                    "C" -> {
                                        currentInput = "0"
                                        expression = ""
                                        previousValue = 0.0
                                        pendingOp = null
                                        isNewNumber = true
                                    }
                                    "⌫" -> {
                                        currentInput = if (currentInput.length > 1) currentInput.dropLast(1) else "0"
                                    }
                                    "±" -> {
                                        val v = currentInput.toDoubleOrNull() ?: 0.0
                                        currentInput = displayFormatter.format(-v)
                                    }
                                    "%" -> {
                                        val v = (currentInput.toDoubleOrNull() ?: 0.0) / 100.0
                                        currentInput = displayFormatter.format(v)
                                    }
                                    "+", "-", "×", "÷" -> {
                                        val currentVal = currentInput.toDoubleOrNull() ?: 0.0
                                        if (pendingOp != null && !isNewNumber) {
                                            val res = calculateResult()
                                            previousValue = res
                                            currentInput = displayFormatter.format(res)
                                        } else {
                                            previousValue = currentVal
                                        }
                                        pendingOp = btn
                                        expression = "${displayFormatter.format(previousValue)} $btn"
                                        isNewNumber = true
                                    }
                                    "=" -> {
                                        if (pendingOp != null) {
                                            val res = calculateResult()
                                            expression = "$expression $currentInput ="
                                            currentInput = displayFormatter.format(res)
                                            pendingOp = null
                                            isNewNumber = true
                                        }
                                    }
                                    "." -> {
                                        if (!currentInput.contains(".")) {
                                            currentInput += "."
                                            isNewNumber = false
                                        }
                                    }
                                    else -> {
                                        // Digits
                                        if (isNewNumber || currentInput == "0") {
                                            currentInput = btn
                                            isNewNumber = false
                                        } else {
                                            currentInput += btn
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = when {
                                    btn == "=" -> MaterialTheme.colorScheme.primary
                                    isOp -> MaterialTheme.colorScheme.secondaryContainer
                                    isSpecial -> MaterialTheme.colorScheme.surfaceVariant
                                    else -> MaterialTheme.colorScheme.surface
                                },
                                contentColor = when {
                                    btn == "=" -> MaterialTheme.colorScheme.onPrimary
                                    isOp -> MaterialTheme.colorScheme.onSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).height(62.dp)
                        ) {
                            if (btn == "⌫") {
                                Icon(Icons.Default.Backspace, contentDescription = "Backspace")
                            } else {
                                Text(
                                    text = btn,
                                    fontSize = 22.sp,
                                    fontWeight = if (isOp || isSpecial) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
