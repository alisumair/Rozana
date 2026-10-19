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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
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
    customExpenseCategories: List<String> = emptyList(),
    customIncomeCategories: List<String> = emptyList(),
    onAddCustomCategory: (TransactionType, String) -> Unit = { _, _ -> },
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

    var showAddNewCategoryField by remember { mutableStateOf(false) }
    var newCustomCategoryName by remember { mutableStateOf("") }

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
                                showAddNewCategoryField = false
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
                                showAddNewCategoryField = false
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

                // Category selector header + Add Custom button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${RozanaStrings.get("category", language)}: $selectedCategory",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(
                        onClick = { showAddNewCategoryField = !showAddNewCategoryField },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == AppLanguage.URDU) "نئی کیٹیگری" else "+ Add Category", fontSize = 12.sp)
                    }
                }

                // Inline New Category Input if toggled
                if (showAddNewCategoryField) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (language == AppLanguage.URDU) "اپنی پسند کی نئی کیٹیگری درج کریں:" else "Enter custom category name:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newCustomCategoryName,
                                    onValueChange = { newCustomCategoryName = it },
                                    placeholder = { Text(if (language == AppLanguage.URDU) "مثلاً: دودھ، جم، بائیک خرچ" else "e.g. Milk, Gym, Bike") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        val trimmed = newCustomCategoryName.trim()
                                        if (trimmed.isNotEmpty()) {
                                            onAddCustomCategory(selectedType, trimmed)
                                            selectedCategory = trimmed
                                            newCustomCategoryName = ""
                                            showAddNewCategoryField = false
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(if (language == AppLanguage.URDU) "شامل کریں" else "Add")
                                }
                            }
                        }
                    }
                }

                // Category chips (Custom categories first, then defaults)
                val activeCustomCategories = if (selectedType == TransactionType.EXPENSE) customExpenseCategories else customIncomeCategories
                if (activeCustomCategories.isNotEmpty()) {
                    Text(
                        text = if (language == AppLanguage.URDU) "آپ کی ذاتی کیٹیگریز (Custom Categories):" else "Your Custom Categories:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    activeCustomCategories.chunked(2).forEach { rowCats ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (cat in rowCats) {
                                val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = cat },
                                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                    label = { Text(cat, maxLines = 1, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowCats.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Standard Built-in Categories
                Text(
                    text = if (language == AppLanguage.URDU) "عام کیٹیگریز (Standard):" else "Standard Categories:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
        "School Fee" to ("سکول فیس" to "School Fee"),
        "Electricity" to ("بجلی بل" to "Electricity Bill"),
        "Gas" to ("گیس بل" to "Gas Bill"),
        "Food" to ("کھانا راشن" to "Food & Ration"),
        "Water" to ("پانی بل" to "Water Bill"),
        "Internet" to ("انٹرنیٹ" to "Internet"),
        "Mobile" to ("موبائل لوڈ/بل" to "Mobile"),
        "Rent" to ("کرایہ" to "Rent"),
        "Loan" to ("قرض / کمیٹی" to "Loan"),
        "Other" to ("دیگر بلز" to "Other Bills")
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

@Composable
fun WaterSettingsDialog(
    currentGoal: Int,
    currentGlasses: Int,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (goal: Int, currentGlasses: Int) -> Unit,
    onReset: () -> Unit
) {
    val isUr = language == AppLanguage.URDU
    var goal by remember { mutableStateOf(currentGoal) }
    var glasses by remember { mutableStateOf(currentGlasses) }
    val goalPresets = listOf(6, 8, 10, 12, 14, 16)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0284C7))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUr) "پانی ٹریکر سیٹنگز (ہدف و مقدار)" else "Water Intake Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isUr)
                        "اپنے جسم اور موسم کے مطابق روزانہ پانی پینے کا ہدف مقرر کریں اور اب تک پیے گئے گلاسز ایڈجسٹ کریں:"
                    else
                        "Customize your daily hydration goal and adjust today's logged water glasses:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Goal Selection
                Text(
                    text = if (isUr) "روزانہ ہدف: $goal گلاس" else "Daily Target: $goal Glasses",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    goalPresets.take(3).forEach { g ->
                        FilterChip(
                            selected = goal == g,
                            onClick = { goal = g },
                            label = { Text("$g ${if (isUr) "گلاس" else "Gl"}") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    goalPresets.drop(3).forEach { g ->
                        FilterChip(
                            selected = goal == g,
                            onClick = { goal = g },
                            label = { Text("$g ${if (isUr) "گلاس" else "Gl"}") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider()

                // Current Glasses Stepper
                Text(
                    text = if (isUr) "آج پیے گئے گلاسز (تدوین کریں):" else "Glasses Consumed Today (Edit):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilledIconButton(
                        onClick = { glasses = (glasses - 1).coerceAtLeast(0) },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "$glasses",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0284C7)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    FilledIconButton(
                        onClick = { glasses = (glasses + 1).coerceAtMost(30) },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF0284C7), contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }

                // Reset button
                OutlinedButton(
                    onClick = {
                        glasses = 0
                        onReset()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isUr) "آج کا پانی صفر کریں (Reset)" else "Reset Today's Water")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(goal, glasses) }
            ) {
                Text(if (isUr) "محفوظ کریں (Save)" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isUr) "منسوخ" else "Cancel")
            }
        }
    )
}

@Composable
fun HabitSettingsDialog(
    currentTitleUr: String,
    currentTitleEn: String,
    currentSubtitleUr: String,
    currentSubtitleEn: String,
    currentDaysClean: Int,
    currentUrgesResisted: Int,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (titleUr: String, titleEn: String, subUr: String, subEn: String, daysClean: Int, urgesResisted: Int) -> Unit,
    onResetStreak: () -> Unit
) {
    val isUr = language == AppLanguage.URDU
    var titleUr by remember { mutableStateOf(currentTitleUr) }
    var titleEn by remember { mutableStateOf(currentTitleEn) }
    var subUr by remember { mutableStateOf(currentSubtitleUr) }
    var subEn by remember { mutableStateOf(currentSubtitleEn) }
    var daysClean by remember { mutableStateOf(currentDaysClean) }
    var urgesResisted by remember { mutableStateOf(currentUrgesResisted) }

    val presets = listOf(
        Triple("سگریٹ نوشی سے نجات", "Smoking Free Streak", "سگریٹ اور بیڑی سے مکمل پرہیز"),
        Triple("سوشل میڈیا اور اسکرین کنٹرول", "Screen Time & Digital Detox", "موبائل کا غیر ضروری استعمال ترک"),
        Triple("فاسٹ فوڈ و چینی پرہیز", "Junk Food & Sugar Free", "صحت مند کھانا اور ورزش کا عزم"),
        Triple("چائے و کیفین کنٹرول", "Tea & Caffeine Control", "دن میں صرف ایک بار چائے کا نظم"),
        Triple("فضول خرچی سے پرہیز", "Overspending & Impulse Control", "بجٹ کا سخت پابند اور فضول خرچ بند"),
        Triple("کسٹم عادت", "Custom Habit", "میری اپنی ذاتی عادت")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF10B981))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUr) "عادت و نشہ ریکوری سیٹنگز" else "Habit Recovery Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isUr)
                        "آپ جس بری عادت، نشہ یا سکرین ٹائم کو چھوڑنا چاہتے ہیں اس کی ذاتی سیٹنگ اور دن مقرر کریں:"
                    else
                        "Select or type the addiction / habit you are quitting and set your streak days:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Presets
                Text(
                    text = if (isUr) "تیار عادات (Quick Presets):" else "Popular Presets:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )

                presets.chunked(2).forEach { rowPresets ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (p in rowPresets) {
                            val isSelected = titleUr == p.first
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    titleUr = p.first
                                    titleEn = p.second
                                    subUr = p.third
                                    subEn = "${p.second} & clean living"
                                },
                                label = { Text(if (isUr) p.first else p.second, maxLines = 1, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title Input
                OutlinedTextField(
                    value = if (isUr) titleUr else titleEn,
                    onValueChange = {
                        if (isUr) titleUr = it else titleEn = it
                    },
                    label = { Text(if (isUr) "عادت کا نام (عنوان)" else "Habit / Streak Title") },
                    placeholder = { Text("e.g. Quit Smoking / سگریٹ نوشی") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Subtitle Input
                OutlinedTextField(
                    value = if (isUr) subUr else subEn,
                    onValueChange = {
                        if (isUr) subUr = it else subEn = it
                    },
                    label = { Text(if (isUr) "وضاحت یا مقصد (Subtitle)" else "Goal / Subtitle") },
                    placeholder = { Text("e.g. سگریٹ، سکرین ٹائم پرہیز") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                HorizontalDivider()

                // Clean Days Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isUr) "کتنے دن صاف رہے؟" else "Clean Days Streak",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isUr) "اپنے اصل دن درج کریں" else "Set exact days",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledIconButton(
                            onClick = { daysClean = (daysClean - 1).coerceAtLeast(0) },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "$daysClean",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        FilledIconButton(
                            onClick = { daysClean += 1 },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF10B981), contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Resisted Urges Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isUr) "وسوسہ کتنی بار روکا؟" else "Urges Resisted Count",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isUr) "کامیاب مزاحمت" else "Temptations resisted",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledIconButton(
                            onClick = { urgesResisted = (urgesResisted - 1).coerceAtLeast(0) },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "$urgesResisted",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        FilledIconButton(
                            onClick = { urgesResisted += 1 },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Reset Streak button
                OutlinedButton(
                    onClick = {
                        daysClean = 0
                        urgesResisted = 0
                        onResetStreak()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isUr) "سٹریک صفر کریں (Reset Streak)" else "Reset Streak to Zero")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(titleUr, titleEn, subUr, subEn, daysClean, urgesResisted)
                }
            ) {
                Text(if (isUr) "محفوظ کریں (Save)" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isUr) "منسوخ" else "Cancel")
            }
        }
    )
}

@Composable
fun CategoryManagementDialog(
    customExpenseCategories: List<String>,
    customIncomeCategories: List<String>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onAddCategory: (TransactionType, String) -> Unit,
    onDeleteCategory: (TransactionType, String) -> Unit,
    onRenameCategory: (TransactionType, String, String) -> Unit
) {
    val isUr = language == AppLanguage.URDU
    var selectedTab by remember { mutableStateOf(TransactionType.EXPENSE) }
    var newCategoryName by remember { mutableStateOf("") }
    var categoryToRename by remember { mutableStateOf<String?>(null) }
    var renameText by remember { mutableStateOf("") }

    val standardExpenses = ExpenseCategory.entries
    val standardIncomes = IncomeCategory.entries
    val currentCustomList = if (selectedTab == TransactionType.EXPENSE) customExpenseCategories else customIncomeCategories

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUr) "کیٹیگریز مینیجر (سیٹنگ اور تدوین)" else "Category Manager & Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isUr)
                        "آپ اپنی مرضی کی کوئی بھی آمدنی یا خرچ کی کیٹیگری شامل، تبدیل یا حذف کر سکتے ہیں:"
                    else
                        "Add, rename, or remove custom income & expense categories to fit your lifestyle:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Tab Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == TransactionType.EXPENSE,
                        onClick = { selectedTab = TransactionType.EXPENSE },
                        label = { Text(if (isUr) "اخراجات کیٹیگریز" else "Expenses") },
                        leadingIcon = { Icon(Icons.Default.TrendingDown, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedTab == TransactionType.INCOME,
                        onClick = { selectedTab = TransactionType.INCOME },
                        label = { Text(if (isUr) "آمدنی کیٹیگریز" else "Income") },
                        leadingIcon = { Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF10B981)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Add New Category Input
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isUr) "نئی کیٹیگری شامل کریں:" else "Add New Category:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newCategoryName,
                                onValueChange = { newCategoryName = it },
                                placeholder = { Text(if (isUr) "مثلاً: جم، دودھ، دکان خرچ" else "e.g. Gym, Milk, Shop") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    val trimmed = newCategoryName.trim()
                                    if (trimmed.isNotEmpty()) {
                                        onAddCategory(selectedTab, trimmed)
                                        newCategoryName = ""
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isUr) "شامل" else "Add")
                            }
                        }
                    }
                }

                // Rename Modal/Row if active
                categoryToRename?.let { oldName ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "${if (isUr) "نام تبدیل کریں" else "Rename"}: $oldName",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = renameText,
                                    onValueChange = { renameText = it },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        if (renameText.isNotBlank()) {
                                            onRenameCategory(selectedTab, oldName, renameText.trim())
                                            categoryToRename = null
                                            renameText = ""
                                        }
                                    }
                                ) {
                                    Text(if (isUr) "تبدیل" else "Save")
                                }
                                TextButton(onClick = { categoryToRename = null }) {
                                    Text(if (isUr) "منسوخ" else "Cancel")
                                }
                            }
                        }
                    }
                }

                // Custom Categories List
                Text(
                    text = if (isUr) "آپ کی ذاتی کیٹیگریز (${currentCustomList.size}):" else "Your Custom Categories (${currentCustomList.size}):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (currentCustomList.isEmpty()) {
                    Text(
                        text = if (isUr) "ابھی تک کوئی ذاتی کیٹیگری نہیں ہے۔ اوپر نام لکھ کر شامل کریں۔" else "No custom categories yet. Type above to add one.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    currentCustomList.forEach { cat ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = cat, fontWeight = FontWeight.SemiBold)
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            categoryToRename = cat
                                            renameText = cat
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { onDeleteCategory(selectedTab, cat) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Standard Built-in Categories
                Text(
                    text = if (isUr) "بنیادی سسٹم کیٹیگریز (Standard):" else "Standard Built-in Categories:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val standardNames = if (selectedTab == TransactionType.EXPENSE) {
                    standardExpenses.map { if (isUr) it.displayNameUr else it.displayNameEn }
                } else {
                    standardIncomes.map { if (isUr) it.displayNameUr else it.displayNameEn }
                }

                Text(
                    text = standardNames.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(if (isUr) "مکمل (Done)" else "Done")
            }
        }
    )
}

