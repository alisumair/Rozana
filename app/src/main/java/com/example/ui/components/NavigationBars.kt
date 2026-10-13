package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.ui.theme.*
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
    onOpenAi: () -> Unit,
    onOpenGuide: () -> Unit = {},
    onOpenQuran: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (language == AppLanguage.URDU) "روزانہ – Rozana" else "Rozana – روزانہ",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            // Direct Quran Action
            IconButton(
                onClick = onOpenQuran,
                modifier = Modifier.testTag("action_quran")
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Quran Majeed",
                    tint = RoyalElectricBlue
                )
            }

            // User Guide Help Action
            IconButton(
                onClick = onOpenGuide,
                modifier = Modifier.testTag("action_guide")
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "User Guide",
                    tint = RoyalElectricBlue
                )
            }

            // Quick Calculator Action
            IconButton(
                onClick = onOpenCalculator,
                modifier = Modifier.testTag("action_calculator")
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = "Calculator",
                    tint = RoyalElectricBlue
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
                    tint = ElectricCyan
                )
            }

            // Language Switcher Badge
            FilledTonalButton(
                onClick = onToggleLanguage,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = IceBlueContainer,
                    contentColor = RoyalElectricBlue
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.padding(horizontal = 2.dp).testTag("action_language")
            ) {
                Text(
                    text = if (language == AppLanguage.ENGLISH) "اردو" else "EN",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
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
    // Modern Floating White Dock (matching the reference phone design)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp,
        tonalElevation = 4.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(66.dp)
        ) {
            val navColors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = RoyalElectricBlue,
                indicatorColor = RoyalElectricBlue,
                unselectedIconColor = TextSecondaryLight,
                unselectedTextColor = TextSecondaryLight
            )

            // 1. Home Dashboard
            NavigationBarItem(
                selected = currentScreen == Screen.Dashboard,
                onClick = { onSelectScreen(Screen.Dashboard) },
                icon = {
                    Icon(
                        if (currentScreen == Screen.Dashboard) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = if (language == AppLanguage.URDU) "ہوم" else "Home"
                    )
                },
                label = { Text(if (language == AppLanguage.URDU) "ہوم" else "Home", fontWeight = FontWeight.Bold) },
                colors = navColors,
                modifier = Modifier.testTag("nav_item_home")
            )

            // 2. Holy Quran
            NavigationBarItem(
                selected = currentScreen == Screen.Quran,
                onClick = { onSelectScreen(Screen.Quran) },
                icon = {
                    Icon(
                        if (currentScreen == Screen.Quran) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                        contentDescription = if (language == AppLanguage.URDU) "قرآن" else "Quran"
                    )
                },
                label = { Text(if (language == AppLanguage.URDU) "قرآن" else "Quran", fontWeight = FontWeight.Bold) },
                colors = navColors,
                modifier = Modifier.testTag("nav_item_quran")
            )

            // 3. Masnoon Azkar
            NavigationBarItem(
                selected = currentScreen == Screen.Azkar,
                onClick = { onSelectScreen(Screen.Azkar) },
                icon = {
                    Icon(
                        if (currentScreen == Screen.Azkar) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (language == AppLanguage.URDU) "اذکار" else "Azkar"
                    )
                },
                label = { Text(if (language == AppLanguage.URDU) "اذکار" else "Azkar", fontWeight = FontWeight.Bold) },
                colors = navColors,
                modifier = Modifier.testTag("nav_item_azkar")
            )

            // 4. Bills & School Fees
            NavigationBarItem(
                selected = currentScreen == Screen.Bills,
                onClick = { onSelectScreen(Screen.Bills) },
                icon = {
                    Icon(
                        if (currentScreen == Screen.Bills) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                        contentDescription = if (language == AppLanguage.URDU) "بلز و فیس" else "Bills"
                    )
                },
                label = { Text(if (language == AppLanguage.URDU) "بلز و فیس" else "Bills", fontWeight = FontWeight.Bold) },
                colors = navColors,
                modifier = Modifier.testTag("nav_item_bills")
            )

            // 5. User Guide (ایپ استعمال کی مکمل گائیڈ)
            NavigationBarItem(
                selected = currentScreen == Screen.Guide,
                onClick = { onSelectScreen(Screen.Guide) },
                icon = {
                    Icon(
                        if (currentScreen == Screen.Guide) Icons.Filled.Help else Icons.Outlined.HelpOutline,
                        contentDescription = if (language == AppLanguage.URDU) "گائیڈ" else "Guide"
                    )
                },
                label = { Text(if (language == AppLanguage.URDU) "گائیڈ" else "Guide", fontWeight = FontWeight.Bold) },
                colors = navColors,
                modifier = Modifier.testTag("nav_item_guide")
            )

            // 6. More (Finance, Tasks, Reports, Settings)
            NavigationBarItem(
                selected = currentScreen == Screen.Settings || currentScreen == Screen.Finance || currentScreen == Screen.Tasks || currentScreen == Screen.Reports || currentScreen == Screen.Documents || currentScreen == Screen.Calculator || currentScreen == Screen.AiAssistant,
                onClick = { onSelectScreen(Screen.Settings) },
                icon = {
                    Icon(
                        if (currentScreen == Screen.Settings) Icons.Filled.GridView else Icons.Outlined.GridView,
                        contentDescription = if (language == AppLanguage.URDU) "مزید" else "More"
                    )
                },
                label = { Text(if (language == AppLanguage.URDU) "مزید" else "More", fontWeight = FontWeight.Bold) },
                colors = navColors,
                modifier = Modifier.testTag("nav_item_more")
            )
        }
    }
}
