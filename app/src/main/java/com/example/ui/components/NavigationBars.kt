package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.RozanaStrings
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RozanaTopBar(
    currentScreen: Screen,
    language: AppLanguage,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenCalculator: () -> Unit,
    onOpenAi: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (language == AppLanguage.URDU) "روزانہ – Rozana" else "Rozana – روزانہ",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        actions = {
            // Quick Calculator Action
            IconButton(
                onClick = onOpenCalculator,
                modifier = Modifier.testTag("action_calculator")
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = "Calculator",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Quick AI Action
            IconButton(
                onClick = onOpenAi,
                modifier = Modifier.testTag("action_ai")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Assistant",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Language Switcher Badge
            FilledTonalButton(
                onClick = onToggleLanguage,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.padding(horizontal = 4.dp).testTag("action_language")
            ) {
                Text(
                    text = if (language == AppLanguage.ENGLISH) "اردو" else "English",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Theme Toggle
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.testTag("action_theme")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun RozanaBottomBar(
    currentScreen: Screen,
    language: AppLanguage,
    onSelectScreen: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.Dashboard,
            onClick = { onSelectScreen(Screen.Dashboard) },
            icon = {
                Icon(
                    if (currentScreen == Screen.Dashboard) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = RozanaStrings.get("nav_home", language)
                )
            },
            label = { Text(RozanaStrings.get("nav_home", language)) },
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = currentScreen == Screen.Finance,
            onClick = { onSelectScreen(Screen.Finance) },
            icon = {
                Icon(
                    if (currentScreen == Screen.Finance) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                    contentDescription = RozanaStrings.get("nav_finance", language)
                )
            },
            label = { Text(RozanaStrings.get("nav_finance", language)) },
            modifier = Modifier.testTag("nav_item_finance")
        )

        NavigationBarItem(
            selected = currentScreen == Screen.Bills,
            onClick = { onSelectScreen(Screen.Bills) },
            icon = {
                Icon(
                    if (currentScreen == Screen.Bills) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                    contentDescription = RozanaStrings.get("nav_bills", language)
                )
            },
            label = { Text(RozanaStrings.get("nav_bills", language)) },
            modifier = Modifier.testTag("nav_item_bills")
        )

        NavigationBarItem(
            selected = currentScreen == Screen.Tasks,
            onClick = { onSelectScreen(Screen.Tasks) },
            icon = {
                Icon(
                    if (currentScreen == Screen.Tasks) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = RozanaStrings.get("nav_tasks", language)
                )
            },
            label = { Text(RozanaStrings.get("nav_tasks", language)) },
            modifier = Modifier.testTag("nav_item_tasks")
        )

        NavigationBarItem(
            selected = currentScreen == Screen.Settings || currentScreen == Screen.Reports || currentScreen == Screen.Documents || currentScreen == Screen.Calculator || currentScreen == Screen.AiAssistant,
            onClick = { onSelectScreen(Screen.Settings) },
            icon = {
                Icon(
                    if (currentScreen == Screen.Settings) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = RozanaStrings.get("nav_more", language)
                )
            },
            label = { Text(RozanaStrings.get("nav_more", language)) },
            modifier = Modifier.testTag("nav_item_more")
        )
    }
}
