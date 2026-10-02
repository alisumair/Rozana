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

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showGithubGuideDialog by remember { mutableStateOf(false) }

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
