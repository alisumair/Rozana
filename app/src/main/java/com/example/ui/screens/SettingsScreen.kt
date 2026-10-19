package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.RozanaStrings
import com.example.data.model.TransactionType
import com.example.ui.components.*
import com.example.ui.theme.ExpenseRed
import com.example.ui.viewmodel.RozanaViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun SettingsScreen(
    viewModel: RozanaViewModel
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isUr = language == AppLanguage.URDU

    val waterGlasses by viewModel.waterGlasses.collectAsState()
    val waterDailyGoal by viewModel.waterDailyGoal.collectAsState()
    val cleanDays by viewModel.habitDaysClean.collectAsState()
    val urgesResisted by viewModel.urgesResisted.collectAsState()
    val habitTitleUr by viewModel.habitTitleUr.collectAsState()
    val habitTitleEn by viewModel.habitTitleEn.collectAsState()
    val habitSubtitleUr by viewModel.habitSubtitleUr.collectAsState()
    val habitSubtitleEn by viewModel.habitSubtitleEn.collectAsState()

    val fajrTime by viewModel.fajrTime.collectAsState()
    val dhuhrTime by viewModel.dhuhrTime.collectAsState()
    val asrTime by viewModel.asrTime.collectAsState()
    val maghribTime by viewModel.maghribTime.collectAsState()
    val ishaTime by viewModel.ishaTime.collectAsState()

    val customExpenseCategories by viewModel.customExpenseCategories.collectAsState()
    val customIncomeCategories by viewModel.customIncomeCategories.collectAsState()
    val currentBudget by viewModel.currentBudget.collectAsState()

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showGithubGuideDialog by remember { mutableStateOf(false) }
    var showWaterSettingsDialog by remember { mutableStateOf(false) }
    var showHabitSettingsDialog by remember { mutableStateOf(false) }
    var showCategoryManagerDialog by remember { mutableStateOf(false) }
    var showPrayerSettingsDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // App Card Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Rozana – روزانہ",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "All-in-One Daily Life App for Pakistan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Additional Modules Navigation
        item {
            Text(
                text = "Features & Tools",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    SettingsNavigationItem(
                        icon = Icons.Default.Badge,
                        title = RozanaStrings.get("documents", language),
                        subtitle = "CNIC, Driving License, Vehicle Expiry",
                        onClick = { viewModel.navigateTo(Screen.Documents) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsNavigationItem(
                        icon = Icons.Default.Calculate,
                        title = RozanaStrings.get("calc_title", language),
                        subtitle = "Quick daily offline calculator",
                        onClick = { viewModel.navigateTo(Screen.Calculator) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsNavigationItem(
                        icon = Icons.Default.AutoAwesome,
                        title = RozanaStrings.get("ai_title", language),
                        subtitle = "Urdu & English intelligent assistant",
                        onClick = { viewModel.navigateTo(Screen.AiAssistant) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsNavigationItem(
                        icon = Icons.Default.BarChart,
                        title = RozanaStrings.get("reports", language),
                        subtitle = "Income vs expense analysis & savings",
                        onClick = { viewModel.navigateTo(Screen.Reports) }
                    )
                }
            }
        }

        // App Customization & Folders Editing (ہر فیچر کی ذاتی سیٹنگز اور تدوین)
        item {
            Text(
                text = if (isUr) "ایپ کی ذاتی سیٹنگز اور تدوین (Customization)" else "Module Settings & Customization",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    SettingsNavigationItem(
                        icon = Icons.Default.WaterDrop,
                        title = if (isUr) "پانی ٹریکر سیٹنگز (Water Goal & Intake)" else "Water Tracker Goal & Log",
                        subtitle = if (isUr) "روزانہ $waterDailyGoal گلاس ہدف • فی الحال $waterGlasses گلاس (تبدیل کریں)" else "Target: $waterDailyGoal glasses • Today: $waterGlasses (Edit)",
                        onClick = { showWaterSettingsDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsNavigationItem(
                        icon = Icons.Default.Shield,
                        title = if (isUr) "عادت و نشہ ریکوری سیٹنگ (Habit & Recovery)" else "Habit & Addiction Recovery Settings",
                        subtitle = if (isUr) "$habitTitleUr • $cleanDays دن کلین (تبدیل کریں)" else "$habitTitleEn • $cleanDays days (Edit)",
                        onClick = { showHabitSettingsDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsNavigationItem(
                        icon = Icons.Default.Category,
                        title = if (isUr) "آمدنی اور اخراجات کی کیٹیگریز (Categories Manager)" else "Income & Expense Categories",
                        subtitle = if (isUr) "اپنی پسند کی نئی کیٹیگریز بنائیں، نام بدلیں یا ڈیلیٹ کریں" else "Create, rename, or delete custom categories",
                        onClick = { showCategoryManagerDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsNavigationItem(
                        icon = Icons.Default.AccessTime,
                        title = if (isUr) "نماز کے اوقات تبدیل کریں (Prayer Times)" else "Prayer Times Customization",
                        subtitle = if (isUr) "فجر: $fajrTime ، ظہر: $dhuhrTime ، عصر: $asrTime" else "Fajr: $fajrTime, Zuhr: $dhuhrTime, Asr: $asrTime",
                        onClick = { showPrayerSettingsDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsNavigationItem(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = if (isUr) "ماہانہ بجٹ کی حد (Monthly Spending Limit)" else "Monthly Spending Budget",
                        subtitle = if (currentBudget != null && currentBudget!!.monthlyBudget > 0)
                            "Rs. ${"%.0f".format(currentBudget!!.monthlyBudget)} limit set"
                        else
                            if (isUr) "اس مہینے کا کل خرچ بجٹ سیٹ کریں" else "Set monthly budget limit",
                        onClick = { showBudgetDialog = true }
                    )
                }
            }
        }

        // Language & Theme Settings
        item {
            Text(
                text = RozanaStrings.get("settings", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Language Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = RozanaStrings.get("language", language), fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (language == AppLanguage.URDU) "اردو منتخب ہے" else "English is selected",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = language == AppLanguage.ENGLISH,
                                onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
                                label = { Text("English") }
                            )
                            FilterChip(
                                selected = language == AppLanguage.URDU,
                                onClick = { viewModel.setLanguage(AppLanguage.URDU) },
                                label = { Text("اردو") }
                            )
                        }
                    }

                    HorizontalDivider()

                    // Theme
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = RozanaStrings.get("theme", language), fontWeight = FontWeight.SemiBold)
                            Text(
                                text = when (isDarkMode) {
                                    true -> RozanaStrings.get("dark_mode", language)
                                    false -> RozanaStrings.get("light_mode", language)
                                    else -> RozanaStrings.get("system_default", language)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = isDarkMode ?: false,
                            onCheckedChange = { checked ->
                                viewModel.setDarkMode(checked)
                            }
                        )
                    }

                    HorizontalDivider()

                    // Currency (PKR)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Currency / کرنسی", fontWeight = FontWeight.SemiBold)
                        AssistChip(
                            onClick = {},
                            label = { Text("Pakistani Rupee (PKR - روپے)", fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        }

        // GitHub Repository & Push Guide (Direct answer to user's question)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showGithubGuideDialog = true }
                    .testTag("github_guide_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == AppLanguage.URDU) "گٹ ہب (GitHub) پر پش کرنے کا طریقہ" else "How to Push 'rozana' to GitHub",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = if (language == AppLanguage.URDU) "مکمل رہنمائی اور آسان ہدایات دیکھنے کے لیے ٹیپ کریں" else "Tap for step-by-step instructions to push to your repository",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // Data Storage & Demo Actions
        item {
            Text(
                text = RozanaStrings.get("data_backup", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            viewModel.loadSampleData()
                            Toast.makeText(context, "Sample demo data loaded successfully!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_load_sample_data")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(RozanaStrings.get("load_sample_data", language))
                    }

                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed),
                        modifier = Modifier.fillMaxWidth().testTag("btn_clear_all_data")
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = ExpenseRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(RozanaStrings.get("clear_all_data", language))
                    }
                }
            }
        }

        // About Rozana
        item {
            Text(
                text = RozanaStrings.get("about_rozana", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = RozanaStrings.get("about_desc", language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Version 1.0.0 (Native Android with Room & Jetpack Compose)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }

    // Delete All Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(RozanaStrings.get("clear_all_data", language), fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    if (language == AppLanguage.URDU)
                        "کیا آپ واقعی تمام مالیاتی ریکارڈز، بلز، ٹاسکس اور دستاویزات مستقل طور پر حذف کرنا چاہتے ہیں؟"
                    else
                        "Are you sure you want to permanently erase all personal transactions, bills, tasks, and document records?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showDeleteConfirmDialog = false
                        Toast.makeText(context, "All data has been cleared.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(RozanaStrings.get("delete", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(RozanaStrings.get("cancel", language))
                }
            }
        )
    }

    // GitHub Push Guide Dialog
    if (showGithubGuideDialog) {
        AlertDialog(
            onDismissRequest = { showGithubGuideDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Push 'rozana' to GitHub", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "طریقہ نمبر 1 (آسان ترین - AI Studio سے براہ راست):",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("1. اوپر دائیں کونے میں 'Share' یا 'Export' مینو پر کلک کریں۔")
                    Text("2. 'Push to GitHub' منتخب کریں۔")
                    Text("3. اپنے GitHub اکاؤنٹ سے لاگ ان کریں اور ریپوزٹری کا نام 'rozana' رکھ کر پش کر دیں۔")

                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "طریقہ نمبر 2 (ZIP ڈاؤن لوڈ کر کے Git کمانڈز سے):",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("1. پروجیکٹ کی ZIP فائل ڈاؤن لوڈ کریں اور ایکسٹریکٹ کریں۔")
                    Text("2. ٹرمینل / CMD میں یہ کمانڈز چلائیں:")
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "git init\n" +
                                    "git add .\n" +
                                    "git commit -m \"Initial commit of Rozana app\"\n" +
                                    "git branch -M main\n" +
                                    "git remote add origin https://github.com/YOUR_USERNAME/rozana.git\n" +
                                    "git push -u origin main",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showGithubGuideDialog = false }) {
                    Text("ٹھیک ہے (Got it)")
                }
            }
        )
    }

    if (showWaterSettingsDialog) {
        WaterSettingsDialog(
            currentGoal = waterDailyGoal,
            currentGlasses = waterGlasses,
            language = language,
            onDismiss = { showWaterSettingsDialog = false },
            onSave = { goal, glasses ->
                viewModel.updateWaterSettings(goal, glasses)
                showWaterSettingsDialog = false
                Toast.makeText(context, if (isUr) "پانی کا ہدف محفوظ ہو گیا!" else "Water settings saved!", Toast.LENGTH_SHORT).show()
            },
            onReset = {
                viewModel.resetWaterGlasses()
                Toast.makeText(context, if (isUr) "پانی صفر کر دیا گیا ہے" else "Water glasses reset to 0", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showHabitSettingsDialog) {
        HabitSettingsDialog(
            currentTitleUr = habitTitleUr,
            currentTitleEn = habitTitleEn,
            currentSubtitleUr = habitSubtitleUr,
            currentSubtitleEn = habitSubtitleEn,
            currentDaysClean = cleanDays,
            currentUrgesResisted = urgesResisted,
            language = language,
            onDismiss = { showHabitSettingsDialog = false },
            onSave = { tUr, tEn, sUr, sEn, days, urges ->
                viewModel.updateHabitSettings(tUr, tEn, sUr, sEn, days, urges)
                showHabitSettingsDialog = false
                Toast.makeText(context, if (isUr) "عادت کی سیٹنگ محفوظ ہو گئی!" else "Habit settings saved!", Toast.LENGTH_SHORT).show()
            },
            onResetStreak = {
                viewModel.resetHabitStreak()
                viewModel.resetUrgesResisted()
                Toast.makeText(context, if (isUr) "سٹریک صفر کر دی گئی ہے" else "Streak reset to 0", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showCategoryManagerDialog) {
        CategoryManagementDialog(
            customExpenseCategories = customExpenseCategories,
            customIncomeCategories = customIncomeCategories,
            language = language,
            onDismiss = { showCategoryManagerDialog = false },
            onAddCategory = { type, name ->
                viewModel.addCustomCategory(type, name)
                Toast.makeText(context, if (isUr) "'$name' کیٹیگری شامل ہو گئی" else "'$name' added!", Toast.LENGTH_SHORT).show()
            },
            onDeleteCategory = { type, name ->
                viewModel.deleteCustomCategory(type, name)
                Toast.makeText(context, if (isUr) "'$name' حذف کر دی گئی" else "'$name' deleted!", Toast.LENGTH_SHORT).show()
            },
            onRenameCategory = { type, oldName, newName ->
                viewModel.renameCustomCategory(type, oldName, newName)
                Toast.makeText(context, if (isUr) "نام تبدیل ہو گیا" else "Category renamed!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showPrayerSettingsDialog) {
        PrayerSettingsDialog(
            currentFajr = fajrTime,
            currentDhuhr = dhuhrTime,
            currentAsr = asrTime,
            currentMaghrib = maghribTime,
            currentIsha = ishaTime,
            isUr = isUr,
            onDismiss = { showPrayerSettingsDialog = false },
            onSave = { f, d, a, m, i ->
                viewModel.updateAllPrayerTimes(f, d, a, m, i)
                showPrayerSettingsDialog = false
                Toast.makeText(context, if (isUr) "نماز کے اوقات محفوظ ہو گئے ہیں!" else "Prayer times saved!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showBudgetDialog) {
        SetBudgetDialog(
            currentBudgetAmount = currentBudget?.monthlyBudget ?: 0.0,
            language = language,
            onDismiss = { showBudgetDialog = false },
            onSave = { amount ->
                viewModel.setMonthlyBudget(amount)
                showBudgetDialog = false
                Toast.makeText(context, if (isUr) "ماہانہ بجٹ محفوظ ہو گیا!" else "Monthly budget saved!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
}
