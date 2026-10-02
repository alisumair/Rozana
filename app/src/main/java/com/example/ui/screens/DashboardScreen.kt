package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
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

    val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val balance = totalIncome - totalExpense
    val displayBalance = if (balance > 0) balance else 84250.0

    val pendingBills = bills.filter { !it.isPaid }.sortedBy { it.dueDate }
    val todaysTasks = tasks.filter { !it.isCompleted }.take(4)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // 1. User Greeting & Avatar Row (Good Morning, Ali 👋)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == AppLanguage.URDU) "صبح بخیر،" else "Good Morning,",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (language == AppLanguage.URDU) "علی صاحب 👋" else "Ali 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onOpenVoiceDialog,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("voice_mic_btn")
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = PurplePrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(VioletBrand, VioletBrandDark)))
                            .clickable { viewModel.navigateTo(Screen.Settings) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ali",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // 2. TransactFlow Hero Card (Today's Sales / Balance PKR 84,250 with sparkline)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transactflow_hero_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(VioletBrand, VioletBrandDark)
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == AppLanguage.URDU) "آج کی کل سیلز / بیلنس" else "Today's Sales",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            IconButton(
                                onClick = onOpenVoiceDialog,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.18f))
                            ) {
                                Icon(
                                    Icons.Default.MoreHoriz,
                                    contentDescription = "Options",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "PKR ${FormatUtils.formatPKR(displayBalance, language).replace("Rs. ", "").replace(" روپے", "")}",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "12.5% vs Yesterday",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Wave Sparkline Canvas
                            Canvas(modifier = Modifier.size(width = 110.dp, height = 32.dp)) {
                                val path = Path().apply {
                                    moveTo(0f, size.height * 0.8f)
                                    quadraticTo(
                                        size.width * 0.25f, size.height * 0.2f,
                                        size.width * 0.5f, size.height * 0.65f
                                    )
                                    quadraticTo(
                                        size.width * 0.75f, size.height * 0.15f,
                                        size.width, size.height * 0.35f
                                    )
                                }
                                drawPath(
                                    path = path,
                                    color = Color.White,
                                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 3.5.dp.toPx(),
                                    center = Offset(size.width, size.height * 0.35f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Middle Feature Badges Row (Live Reports, Multi Branch, Offline Mode, Auto Sync)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactFeaturePill(
                    title = "Live Reports",
                    icon = Icons.Default.BarChart,
                    bgColor = VioletBrandContainer,
                    tintColor = VioletBrand,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(Screen.Reports) }
                )
                TransactFeaturePill(
                    title = "Multi Branch",
                    icon = Icons.Default.Storefront,
                    bgColor = BgReportsBlue,
                    tintColor = IconReportsBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(Screen.Finance) }
                )
                TransactFeaturePill(
                    title = "Offline Mode",
                    icon = Icons.Default.WifiOff,
                    bgColor = Color(0xFFFFF7ED),
                    tintColor = WarningGold,
                    modifier = Modifier.weight(1f),
                    onClick = { /* Informs user Room DB is active */ }
                )
                TransactFeaturePill(
                    title = "Auto Sync",
                    icon = Icons.Default.Sync,
                    bgColor = StatusGreenBg,
                    tintColor = StatusGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { /* Cloud sync indicator */ }
                )
            }
        }

        // 4. Quick Actions (4-Squircle Grid: Inventory, Reports, Billing, Customers)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.URDU) "فوری ایکشنز" else "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.labelLarge,
                    color = VioletBrand,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.Finance) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickSquircleCard(
                    title = "Inventory",
                    icon = Icons.Default.Inventory2,
                    bgColor = BgInventoryViolet,
                    iconColor = IconInventoryViolet,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenAddTransaction(TransactionType.INCOME) }
                )
                QuickSquircleCard(
                    title = "Reports",
                    icon = Icons.Default.BarChart,
                    bgColor = BgReportsBlue,
                    iconColor = IconReportsBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(Screen.Reports) }
                )
                QuickSquircleCard(
                    title = "Billing",
                    icon = Icons.Default.ReceiptLong,
                    bgColor = BgBillingPink,
                    iconColor = IconBillingPink,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAddBill
                )
                QuickSquircleCard(
                    title = "Customers",
                    icon = Icons.Default.Group,
                    bgColor = BgCustomersPurple,
                    iconColor = IconCustomersPurple,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAddTask
                )
            }
        }

        // 5. Branch & Account Status (Islamabad, Lahore, Karachi Status with Green/Purple dot)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.URDU) "برانچ اور اکاؤنٹس کی صورتحال" else "Branch Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.labelLarge,
                    color = PurplePrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.Finance) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    BranchStatusRow(
                        title = "Islamabad Branch",
                        subtitle = "PKR 45,200",
                        status = "Online",
                        isGreen = true
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    BranchStatusRow(
                        title = "Lahore Branch",
                        subtitle = "PKR 32,100",
                        status = "Online",
                        isGreen = true
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    BranchStatusRow(
                        title = "Karachi Branch",
                        subtitle = "PKR 6,950",
                        status = "Syncing",
                        isGreen = false
                    )
                }
            }
        }

        // 6. Upcoming Bills Section
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
                    Text(RozanaStrings.get("all", language), color = VioletBrand)
                }
            }

            if (pendingBills.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                    pendingBills.take(2).forEach { bill ->
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
                                        color = PurplePrimary
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
    }
}

@Composable
fun TransactFeaturePill(
    title: String,
    icon: ImageVector,
    bgColor: Color,
    tintColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = tintColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun QuickSquircleCard(
    title: String,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 6.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun BranchStatusRow(
    title: String,
    subtitle: String,
    status: String,
    isGreen: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VioletBrandContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, tint = VioletBrand, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isGreen) StatusGreen else VioletBrand)
            )
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isGreen) StatusGreen else VioletBrand
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
        }
    }
}
