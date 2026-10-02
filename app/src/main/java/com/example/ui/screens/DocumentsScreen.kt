package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.RozanaStrings
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.WarningGold
import com.example.ui.viewmodel.RozanaViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.FormatUtils

@Composable
fun DocumentsScreen(
    viewModel: RozanaViewModel,
    onAddDocument: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    val documents by viewModel.allDocuments.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDocument,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_document")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
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

            // Back Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.Settings) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = RozanaStrings.get("documents", language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (documents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(bottom = 60.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (language == AppLanguage.URDU) "کوئی دستاویز شامل نہیں کی گئی۔" else "No document expiry reminders added yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(documents, key = { it.id }) { doc ->
                        val daysRemaining = FormatUtils.getDaysDifference(doc.expiryDate)
                        val isExpired = daysRemaining < 0
                        val isExpiringSoon = daysRemaining in 0..30

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
                                            text = doc.title,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        if (doc.docNumber.isNotEmpty()) {
                                            Text(
                                                text = doc.docNumber,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Text(
                                            text = "${RozanaStrings.get("expiry_date", language)}: ${FormatUtils.formatDate(doc.expiryDate, language)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(onClick = { viewModel.deleteDocument(doc) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                val badgeText = when {
                                    isExpired -> RozanaStrings.get("expired", language)
                                    else -> "${RozanaStrings.get("expires_in", language)} $daysRemaining ${RozanaStrings.get("days", language)}"
                                }
                                val badgeColor = when {
                                    isExpired -> ExpenseRed
                                    isExpiringSoon -> WarningGold
                                    else -> IncomeGreen
                                }

                                AssistChip(
                                    onClick = {},
                                    label = { Text(badgeText, color = badgeColor, fontWeight = FontWeight.Bold) },
                                    leadingIcon = if (isExpired || isExpiringSoon) {
                                        { Icon(Icons.Default.Warning, contentDescription = null, tint = badgeColor) }
                                    } else null,
                                    colors = AssistChipDefaults.assistChipColors(containerColor = badgeColor.copy(alpha = 0.1f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
