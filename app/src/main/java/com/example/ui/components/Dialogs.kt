package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.util.FormatUtils
import com.example.util.ParsedCandidate
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddEditTransactionDialog(
    initialType: TransactionType = TransactionType.EXPENSE,
    transactionToEdit: TransactionEntity? = null,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (type: TransactionType, amount: Double, category: String, description: String, timestamp: Long, method: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(transactionToEdit?.type ?: initialType) }
    var amountText by remember { mutableStateOf(transactionToEdit?.let { "%.0f".format(it.amount) } ?: "") }
    var description by remember { mutableStateOf(transactionToEdit?.description ?: "") }
    var paymentMethod by remember { mutableStateOf(transactionToEdit?.paymentMethod ?: "Cash") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val expenseCategories = ExpenseCategory.entries
    val incomeCategories = IncomeCategory.entries

    var selectedCategory by remember {
        mutableStateOf(
            transactionToEdit?.category ?: if (selectedType == TransactionType.EXPENSE) ExpenseCategory.FOOD.name else IncomeCategory.SALARY.name
        )
    }

    val paymentMethods = listOf("Cash", "EasyPaisa", "JazzCash", "Bank Transfer", "Raast")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (transactionToEdit == null) {
                    if (selectedType == TransactionType.INCOME) RozanaStrings.get("add_income", language)
                    else RozanaStrings.get("add_expense", language)
                } else RozanaStrings.get("edit", language),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Type selector row
                if (transactionToEdit == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedType == TransactionType.EXPENSE,
                            onClick = {
                                selectedType = TransactionType.EXPENSE
                                selectedCategory = ExpenseCategory.FOOD.name
                            },
                            label = { Text(RozanaStrings.get("expense", language)) },
                            leadingIcon = { Icon(Icons.Default.TrendingDown, contentDescription = null) },
                            modifier = Modifier.weight(1f).testTag("select_expense_chip")
                        )
                        FilterChip(
                            selected = selectedType == TransactionType.INCOME,
                            onClick = {
                                selectedType = TransactionType.INCOME
                                selectedCategory = IncomeCategory.SALARY.name
                            },
                            label = { Text(RozanaStrings.get("income", language)) },
                            leadingIcon = { Icon(Icons.Default.TrendingUp, contentDescription = null) },
                            modifier = Modifier.weight(1f).testTag("select_income_chip")
                        )
                    }
                }

                // Amount input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { char -> char.isDigit() || char == '.' }
                        errorMessage = null
                    },
                    label = { Text(RozanaStrings.get("amount", language)) },
                    placeholder = { Text("e.g. 1500") },
                    prefix = { Text("Rs. ", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("amount_input"),
                    singleLine = true,
                    isError = errorMessage != null
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Category selector
                Text(
                    text = RozanaStrings.get("category", language),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                // Category grid chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (selectedType == TransactionType.EXPENSE) {
                        expenseCategories.chunked(2).forEach { rowCategories ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (cat in rowCategories) {
                                    val isSelected = selectedCategory == cat.name
                                    val catName = if (language == AppLanguage.URDU) cat.displayNameUr else cat.displayNameEn
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCategory = cat.name },
                                        label = { Text(catName, maxLines = 1) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    } else {
                        incomeCategories.chunked(2).forEach { rowCategories ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (cat in rowCategories) {
                                    val isSelected = selectedCategory == cat.name
                                    val catName = if (language == AppLanguage.URDU) cat.displayNameUr else cat.displayNameEn
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCategory = cat.name },
                                        label = { Text(catName, maxLines = 1) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(RozanaStrings.get("description", language)) },
                    placeholder = { Text(if (language == AppLanguage.URDU) "تفصیل لکھیں (اختیاری)" else "Description (optional)") },
                    modifier = Modifier.fillMaxWidth().testTag("description_input"),
                    maxLines = 2
                )

                // Payment method
                Text(
                    text = RozanaStrings.get("payment_method", language),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    paymentMethods.take(3).forEach { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            label = { Text(method) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        errorMessage = if (language == AppLanguage.URDU) "براہ کرم درست رقم درج کریں" else "Please enter a valid positive amount"
                        return@Button
                    }
                    onSave(
                        selectedType,
                        amount,
                        selectedCategory,
                        description.trim().ifEmpty { selectedCategory },
                        transactionToEdit?.timestamp ?: System.currentTimeMillis(),
                        paymentMethod
                    )
                },
                modifier = Modifier.testTag("save_transaction_button")
            ) {
                Text(RozanaStrings.get("save", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(RozanaStrings.get("cancel", language))
            }
        }
    )
}

@Composable
fun AddEditBillDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, dueDate: Long, category: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Electricity") }
    var daysFromNow by remember { mutableIntStateOf(5) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "Electricity" to ("بجلی" to "Electricity"),
        "Gas" to ("گیس" to "Gas"),
        "Internet" to ("انٹرنیٹ" to "Internet"),
        "Mobile" to ("موبائل لوڈ/بل" to "Mobile"),
        "Rent" to ("کرایہ" to "Rent"),
        "School Fee" to ("اسکول فیس" to "School Fee"),
        "Loan" to ("قرض / کمیٹی" to "Loan"),
        "Other" to ("دیگر" to "Other")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(RozanaStrings.get("add_bill", language), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Bill Name / نام") },
                    placeholder = { Text("e.g. LESCO Electricity Bill") },
                    modifier = Modifier.fillMaxWidth().testTag("bill_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { char -> char.isDigit() || char == '.' }
                        errorMessage = null
                    },
                    label = { Text(RozanaStrings.get("amount", language)) },
                    placeholder = { Text("e.g. 5000") },
                    prefix = { Text("Rs. ", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("bill_amount_input"),
                    singleLine = true
                )

                Text(
                    text = "Category / قسم",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for ((catKey, names) in row) {
                                FilterChip(
                                    selected = selectedCategory == catKey,
                                    onClick = {
                                        selectedCategory = catKey
                                        if (title.isEmpty()) {
                                            title = if (language == AppLanguage.URDU) names.first else names.second
                                        }
                                    },
                                    label = { Text(if (language == AppLanguage.URDU) names.first else names.second) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Due In / آخری تاریخ",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1 to "1 Day", 3 to "3 Days", 7 to "7 Days", 15 to "15 Days").forEach { (d, label) ->
                        FilterChip(
                            selected = daysFromNow == d,
                            onClick = { daysFromNow = d },
                            label = { Text(label) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Consumer #") },
                    placeholder = { Text("Reference # or extra notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (title.isBlank() || amount == null || amount <= 0.0) {
                        errorMessage = "Please enter valid bill name and amount"
                        return@Button
                    }
                    val dueTimestamp = System.currentTimeMillis() + (daysFromNow * 86400000L)
                    onSave(title.trim(), amount, dueTimestamp, selectedCategory, notes.trim())
                },
                modifier = Modifier.testTag("save_bill_button")
            ) {
                Text(RozanaStrings.get("save", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(RozanaStrings.get("cancel", language)) }
        }
    )
}

@Composable
fun AddEditTaskDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (title: String, dueDate: Long, timeString: String, notes: String, priority: TaskPriority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var timeString by remember { mutableStateOf("05:00 PM") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(RozanaStrings.get("add_task", language), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title / کام کی تفصیل") },
                    placeholder = { Text("e.g. Deposit electricity bill") },
                    modifier = Modifier.fillMaxWidth().testTag("task_title_input")
                )

                OutlinedTextField(
                    value = timeString,
                    onValueChange = { timeString = it },
                    label = { Text("Time / وقت") },
                    placeholder = { Text("e.g. 05:00 PM") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Priority / ترجیح",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.entries.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.name) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / تفصیل (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title.trim(), System.currentTimeMillis(), timeString, notes.trim(), priority)
                    }
                },
                modifier = Modifier.testTag("save_task_button")
            ) {
                Text(RozanaStrings.get("save", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(RozanaStrings.get("cancel", language)) }
        }
    )
}

@Composable
fun AddEditDocumentDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (title: String, docType: DocumentType, docNumber: String, expiryDate: Long, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var docNumber by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(DocumentType.CNIC) }
    var monthsAhead by remember { mutableIntStateOf(6) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(RozanaStrings.get("add_document", language), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Document Title / نام") },
                    placeholder = { Text("e.g. My Smart CNIC") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = docNumber,
                    onValueChange = { docNumber = it },
                    label = { Text(RozanaStrings.get("doc_number", language)) },
                    placeholder = { Text("e.g. 35201-XXXXXXX-1") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Document Type / قسم", fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DocumentType.entries.take(4).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                if (title.isEmpty()) title = if (language == AppLanguage.URDU) type.displayNameUr else type.displayNameEn
                            },
                            label = { Text(if (language == AppLanguage.URDU) type.displayNameUr else type.displayNameEn) }
                        )
                    }
                }

                Text("Expires In / میعاد", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1 to "1 Month", 3 to "3 Months", 6 to "6 Months", 12 to "1 Year").forEach { (m, lbl) ->
                        FilterChip(
                            selected = monthsAhead == m,
                            onClick = { monthsAhead = m },
                            label = { Text(lbl) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / ہدایات") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val expiryTimestamp = System.currentTimeMillis() + (monthsAhead * 30L * 86400000L)
                        onSave(title.trim(), selectedType, docNumber.trim(), expiryTimestamp, notes.trim())
                    }
                }
            ) {
                Text(RozanaStrings.get("save", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(RozanaStrings.get("cancel", language)) }
        }
    )
}

@Composable
fun SetBudgetDialog(
    currentBudgetAmount: Double,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var budgetText by remember { mutableStateOf(if (currentBudgetAmount > 0) "%.0f".format(currentBudgetAmount) else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(RozanaStrings.get("set_budget", language), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.URDU)
                        "اس مہینے کے کل اخراجات کے لیے بجٹ رقم درج کریں۔ 80% خرچ ہونے پر ایپ آپ کو خبردار کرے گی۔"
                    else
                        "Set your total spending limit for this month. You will receive alert warnings when reaching 80% and 100%."
                )
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it.filter { c -> c.isDigit() } },
                    label = { Text(RozanaStrings.get("monthly_budget_amount", language)) },
                    prefix = { Text("Rs. ", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("budget_amount_input"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = budgetText.toDoubleOrNull() ?: 0.0
                    onSave(amount)
                },
                modifier = Modifier.testTag("save_budget_button")
            ) {
                Text(RozanaStrings.get("save", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(RozanaStrings.get("cancel", language)) }
        }
    )
}

@Composable
fun VoiceInputDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onParseText: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val samplePhrases = listOf(
        "Aj 1500 rupay kamaye aur 300 rupay petrol par kharch kiye",
        "Salary 65000 aayi",
        "450 chai samosa kharcha",
        "Gas bill 1200 pay kiya"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(RozanaStrings.get("voice_assistant", language), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = RozanaStrings.get("voice_instruction", language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("بولیں یا لکھیں (مثلاً: 500 پیٹرول خرچ کیا)") },
                    modifier = Modifier.fillMaxWidth().testTag("voice_text_input"),
                    maxLines = 3
                )

                Text(
                    text = "Sample phrases / نمونہ جملے:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )

                samplePhrases.take(3).forEach { phrase ->
                    SuggestionChip(
                        onClick = { inputText = phrase },
                        label = { Text(phrase, maxLines = 1, style = MaterialTheme.typography.bodySmall) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onParseText(inputText.trim())
                    }
                },
                modifier = Modifier.testTag("voice_parse_button")
            ) {
                Text("Review / تصدیق کریں")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(RozanaStrings.get("cancel", language)) }
        }
    )
}

@Composable
fun VoiceConfirmationDialog(
    candidates: List<ParsedCandidate>,
    language: AppLanguage,
    onConfirm: (List<ParsedCandidate>) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(RozanaStrings.get("confirm_transaction", language), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.URDU)
                        "ہم نے آپ کی آواز سے یہ اندراجات تیار کیے ہیں۔ برائے مہربانی تصدیق کریں:"
                    else
                        "We extracted these transactions. Please confirm before saving to your records:"
                )

                candidates.forEach { c ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (c.type == TransactionType.INCOME) "Income (+)" else "Expense (-)",
                                    color = if (c.type == TransactionType.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Category: ${c.category}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                text = FormatUtils.formatPKR(c.amount, language),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(candidates) }) {
                Text(RozanaStrings.get("save", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(RozanaStrings.get("cancel", language)) }
        }
    )
}
