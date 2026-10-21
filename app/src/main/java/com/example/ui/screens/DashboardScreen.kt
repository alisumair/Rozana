package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RozanaViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.FormatUtils
import com.example.util.ImageUtils
import com.example.util.NotificationHelper
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
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val isUr = language == AppLanguage.URDU
    val transactions by viewModel.allTransactions.collectAsState()
    val bills by viewModel.allBills.collectAsState()
    val tasks by viewModel.allTasks.collectAsState()
    val budget by viewModel.currentBudget.collectAsState()

    val userName by viewModel.userName.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val profileBitmap = ImageUtils.rememberUriBitmap(profileImageUri)

    val cleanDays by viewModel.habitDaysClean.collectAsState()
    val urgesResisted by viewModel.urgesResisted.collectAsState()
    val habitTitleUr by viewModel.habitTitleUr.collectAsState()
    val habitTitleEn by viewModel.habitTitleEn.collectAsState()
    val habitSubtitleUr by viewModel.habitSubtitleUr.collectAsState()
    val habitSubtitleEn by viewModel.habitSubtitleEn.collectAsState()

    val waterGlasses by viewModel.waterGlasses.collectAsState()
    val waterDailyGoal by viewModel.waterDailyGoal.collectAsState()

    val customExpenseCategories by viewModel.customExpenseCategories.collectAsState()
    val customIncomeCategories by viewModel.customIncomeCategories.collectAsState()

    val fajrAlert by viewModel.fajrAlert.collectAsState()
    val dhuhrAlert by viewModel.dhuhrAlert.collectAsState()
    val asrAlert by viewModel.asrAlert.collectAsState()
    val maghribAlert by viewModel.maghribAlert.collectAsState()
    val ishaAlert by viewModel.ishaAlert.collectAsState()

    val fajrTime by viewModel.fajrTime.collectAsState()
    val dhuhrTime by viewModel.dhuhrTime.collectAsState()
    val asrTime by viewModel.asrTime.collectAsState()
    val maghribTime by viewModel.maghribTime.collectAsState()
    val ishaTime by viewModel.ishaTime.collectAsState()

    val breakfastAlert by viewModel.breakfastAlert.collectAsState()
    val lunchAlert by viewModel.lunchAlert.collectAsState()
    val dinnerAlert by viewModel.dinnerAlert.collectAsState()

    val startingBalance by viewModel.startingBalance.collectAsState()
    val isBalanceVisible by viewModel.isBalanceVisible.collectAsState()

    val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val netBalance = startingBalance + totalIncome - totalExpense

    val pendingBills = bills.filter { !it.isPaid }.sortedBy { it.dueDate }
    val todaysTasks = tasks.filter { !it.isCompleted }.take(4)

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showShareAppDialog by remember { mutableStateOf(false) }
    var showPrayerSettingsDialog by remember { mutableStateOf(false) }
    var showWaterSettingsDialog by remember { mutableStateOf(false) }
    var showHabitSettingsDialog by remember { mutableStateOf(false) }
    var showCategoryManagerDialog by remember { mutableStateOf(false) }
    var showEditBalanceDialog by remember { mutableStateOf(false) }

    // Android Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.updateProfileImage(uri.toString())
                Toast.makeText(context, if (isUr) "پروفائل تصویر کامیابی سے لگ گئی!" else "Profile photo updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // 1. User Greeting & Avatar Row (Mohsin 👋)
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
                        text = if (isUr) "صبح بخیر،" else "Good Morning,",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isUr) "$userName صاحب 👋" else "$userName 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Voice Input Button
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

                    // Mohsin Profile Photo / Avatar (Tappable to pick image from gallery)
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(VioletBrand, VioletBrandDark)))
                            .clickable { showEditProfileDialog = true }
                            .testTag("profile_avatar_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileBitmap != null) {
                            Image(
                                bitmap = profileBitmap,
                                contentDescription = "Mohsin's Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = userName.take(1).ifEmpty { "M" },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // 1b. Direct Action Quick Bar: [📸 تصویر لگائیں / Profile Photo] & [📲 ایپ شیئر و انسٹال کریں / Share APK]
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.weight(1f).testTag("btn_quick_pick_photo"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUr) "اپنی تصویر لگائیں" else "Add Profile Pic",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showShareAppDialog = true },
                    modifier = Modifier.weight(1f).testTag("btn_quick_share_apk"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C48C)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUr) "ایپ شیئر و انسٹال" else "Share & Install",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 1c. Top Quick Access Hub (Floating Modern Cards with 5-Star Ratings matching reference image)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUr) "✨ سب کچھ ڈسپلے پر (Quick Access Hub)" else "✨ All Features Display Hub",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IceBlueContainer
                    ) {
                        Text(
                            text = if (isUr) "100% مفت ✓" else "FREE ✓",
                            color = RoyalElectricBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Quran Floating Card
                    FloatingFeatureCard(
                        titleUr = "قرآن مجید",
                        titleEn = "Holy Quran",
                        subtitle = "114 سورتیں و ترجمہ",
                        icon = Icons.Default.MenuBook,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Quran) }
                    )

                    // 2. Azkar Floating Card
                    FloatingFeatureCard(
                        titleUr = "صبح کے اذکار",
                        titleEn = "Masnoon Azkar",
                        subtitle = "حصن المسلم و تسبیح",
                        icon = Icons.Default.Star,
                        iconBg = Color(0xFFE0F2FE),
                        iconTint = RoyalElectricBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Azkar) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 3. User Guide Floating Card
                    FloatingFeatureCard(
                        titleUr = "یوزر گائیڈ",
                        titleEn = "User Guide",
                        subtitle = "ایپ کے تمام فیچرز",
                        icon = Icons.Default.HelpOutline,
                        iconBg = IceBlueContainer,
                        iconTint = RoyalElectricBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Guide) }
                    )

                    // 4. Bills & Fees Floating Card
                    FloatingFeatureCard(
                        titleUr = "بلز و سکول فیس",
                        titleEn = "Bills & Fees",
                        subtitle = "بجلی، گیس، فیس",
                        icon = Icons.Default.ReceiptLong,
                        iconBg = Color(0xFFE0E7FF),
                        iconTint = Color(0xFF4338CA),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Bills) }
                    )
                }
            }
        }

        // 2. TransactFlow Hero Card (Matching Reference Banner: "DID YOU KNOW? 90% Start Online" & Cyan SALE Tag)
        item {
            Card(
                shape = RoundedCornerShape(26.dp),
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
                                colors = listOf(RoyalBlueDeepNavy, RoyalBlueDark, RoyalElectricBlue)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        // Top Header with "DID YOU KNOW?" headline and floating cyan SALE tag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isUr) "کیا آپ جانتے ہیں؟" else "DID YOU KNOW?",
                                    color = ElectricCyan,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isUr) "90% روزمرہ معمولات، نماز، قرآن و بلز ایک جگہ" else "90% of Daily Needs Managed Online",
                                    color = Color.White.copy(alpha = 0.95f),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Cyan SALE-Style Badge Tag (exactly like the blue tag in the reference image)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SaleTagBlue,
                                shadowElevation = 4.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isUr) "فعال ✓" else "ACTIVE ✓",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Balance Header & Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isUr) "کل موجودہ بیلنس / نقد رقم" else "Total Cash & Balance",
                                color = Color.White.copy(alpha = 0.88f),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Privacy Eye Toggle (Show/Hide Balance)
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.22f),
                                    modifier = Modifier.clickable { viewModel.toggleBalanceVisibility() }
                                ) {
                                    Icon(
                                        imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Balance Visibility",
                                        tint = Color.White,
                                        modifier = Modifier.padding(6.dp).size(16.dp)
                                    )
                                }

                                // Edit Balance Button
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.25f),
                                    modifier = Modifier.clickable { showEditBalanceDialog = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Balance",
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isUr) "بیلنس ایڈٹ کریں" else "Edit Balance",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (isBalanceVisible) {
                            Text(
                                text = "PKR ${FormatUtils.formatPKR(netBalance, language).replace("Rs. ", "").replace(" روپے", "")}",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                        } else {
                            Text(
                                text = "PKR ••••••",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                        }

                        // Breakdown row
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isUr) "ابتدائی نقد: Rs. ${"%.0f".format(startingBalance)}" else "Starting: Rs. ${"%.0f".format(startingBalance)}",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 11.sp
                            )
                            Text(text = "•", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            Text(
                                text = if (isUr) "آمدنی: +Rs. ${"%.0f".format(totalIncome)}" else "Income: +Rs. ${"%.0f".format(totalIncome)}",
                                color = Color(0xFFA7F3D0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(text = "•", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            Text(
                                text = if (isUr) "اخراجات: -Rs. ${"%.0f".format(totalExpense)}" else "Expense: -Rs. ${"%.0f".format(totalExpense)}",
                                color = Color(0xFFFECDD3),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.22f)
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
                                        text = "+12.5% vs Yesterday",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Wave Sparkline Canvas in pure white
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

        // 2b. Namaz & Prayer Times Widget with Azan notification toggles
        item {
            val prayerNamesUr = listOf("فجر", "ظہر", "عصر", "مغرب", "عشاء")
            val prayerNamesEn = listOf("Fajr", "Zuhr", "Asr", "Maghrib", "Isha")
            val prayerTimesList = listOf(fajrTime, dhuhrTime, asrTime, maghribTime, ishaTime)
            val prayerAlertsList = listOf(fajrAlert, dhuhrAlert, asrAlert, maghribAlert, ishaAlert)

            val nowCal = Calendar.getInstance()
            val nowMinutes = nowCal.get(Calendar.HOUR_OF_DAY) * 60 + nowCal.get(Calendar.MINUTE)

            val prayerMinutes = remember(fajrTime, dhuhrTime, asrTime, maghribTime, ishaTime) {
                prayerTimesList.map { parseTimeToMinutes(it) }
            }

            val nextPrayerIdx = remember(nowMinutes, prayerMinutes) {
                var found = -1
                for (i in prayerMinutes.indices) {
                    if (nowMinutes < prayerMinutes[i]) {
                        found = i
                        break
                    }
                }
                if (found != -1) found else 0
            }

            val nextPrayerName = if (language == AppLanguage.URDU) prayerNamesUr[nextPrayerIdx] else prayerNamesEn[nextPrayerIdx]
            val nextPrayerTimeStr = prayerTimesList[nextPrayerIdx]
            val nextPrayerSubtitle = if (language == AppLanguage.URDU) "اگلی نماز: $nextPrayerName ($nextPrayerTimeStr)" else "Next: $nextPrayerName ($nextPrayerTimeStr)"

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(RoyalBlueDark, RoyalBlueDeepNavy)
                            )
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == AppLanguage.URDU) "🕌 اوقاتِ نماز و اذان نوٹیفیکیشنز" else "🕌 Prayer Times & Azan Alerts",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = nextPrayerSubtitle,
                                    color = Color.White.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Settings button to adjust prayer times
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.25f),
                                    modifier = Modifier.clickable { showPrayerSettingsDialog = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Prayer Settings",
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (language == AppLanguage.URDU) "سیٹنگز" else "Settings",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.URDU) "فعال ✓" else "Active ✓",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Triple(prayerNamesUr[0], fajrTime, fajrAlert),
                                Triple(prayerNamesUr[1], dhuhrTime, dhuhrAlert),
                                Triple(prayerNamesUr[2], asrTime, asrAlert),
                                Triple(prayerNamesUr[3], maghribTime, maghribAlert),
                                Triple(prayerNamesUr[4], ishaTime, ishaAlert)
                            ).forEachIndexed { idx, (pName, pTime, isAlert) ->
                                val isCurrent = idx == nextPrayerIdx
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.14f),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            viewModel.togglePrayerAlert(idx)
                                            NotificationHelper.showNamazAlertNotification(context, pName, pTime)
                                            Toast.makeText(context, if (isUr) "$pName اذان الرٹ تبدیل ہو گیا" else "$pName Azan alert updated", Toast.LENGTH_SHORT).show()
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = pName,
                                            color = if (isCurrent) RoyalElectricBlue else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = pTime.replace(" ", "\n"),
                                            color = if (isCurrent) RoyalElectricBlue else Color.White.copy(alpha = 0.9f),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            lineHeight = 12.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Icon(
                                            imageVector = if (isAlert) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                            contentDescription = null,
                                            tint = if (isCurrent) RoyalElectricBlue else Color.White.copy(alpha = 0.8f),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2c. Meals (Lunch & Dinner) Notification Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.URDU) "🍽️ کھانے اور لنچ / ڈینر کے نوٹیفیکیشنز" else "🍽️ Lunch & Dinner Meal Reminders",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "Auto Alerts",
                                color = Color(0xFFD97706),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple(if (language == AppLanguage.URDU) "ناشتہ" else "Breakfast", "08:00 AM", breakfastAlert),
                            Triple(if (language == AppLanguage.URDU) "دوپہر لنچ" else "Lunch", "01:30 PM", lunchAlert),
                            Triple(if (language == AppLanguage.URDU) "رات ڈینر" else "Dinner", "08:30 PM", dinnerAlert)
                        ).forEachIndexed { idx, (mName, mTime, isAlert) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.toggleMealAlert(idx)
                                        NotificationHelper.showMealAlertNotification(context, mName, mTime)
                                        Toast.makeText(context, if (isUr) "$mName ریمائنڈر الرٹ تبدیل ہو گیا" else "$mName reminder updated", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restaurant,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = mName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = mTime,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = if (isAlert) "آن ✓" else "بند ✕",
                                        color = if (isAlert) Color(0xFF059669) else Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2d. Quran Majeed & Subah k Azkar Folders (1-Tap direct display cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quran Majeed Folder
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD97706)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(Screen.Quran) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.22f)
                            ) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp).size(20.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "114 سورتیں",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (language == AppLanguage.URDU) "قرآن مجید" else "Holy Quran",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == AppLanguage.URDU) "سورۃ یٰسین، الملک، الفاتحہ و ترجمہ" else "Surah Ya-Sin, Mulk & Urdu",
                            color = Color.White.copy(alpha = 0.88f),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }
                }

                // Subah k Azkar Folder
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(Screen.Azkar) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.22f)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp).size(20.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "حصن المسلم",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (language == AppLanguage.URDU) "مسنون دعائیں و اذکار" else "Masnoon Duas & Azkar",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == AppLanguage.URDU) "۲۷+ مسنون دعائیں، صبح شام کے اذکار" else "27+ Masnoon Duas & Azkar",
                            color = Color.White.copy(alpha = 0.88f),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 2d-2. Today's Featured Masnoon Dua Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(Screen.Azkar) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF2563EB).copy(alpha = 0.15f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.URDU) "آج کی منتخب مسنون دعا" else "Daily Featured Masnoon Dua",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        TextButton(
                            onClick = { viewModel.navigateTo(Screen.Azkar) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.URDU) "تمام ۲۷+ دعائیں →" else "View All 27+ →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
                            fontSize = 17.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (language == AppLanguage.URDU)
                            "اے اللہ! میں غم و فکر، کمزوری و سستی، اور قرض کے بوجھ اور لوگوں کے دباؤ سے تیری پناہ مانگتا ہوں۔ (صحیح بخاری)"
                        else
                            "O Allah! I seek refuge in You from grief and worry, weakness, laziness, burden of debts and overpowering of people. (Sahih Bukhari)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        textAlign = if (language == AppLanguage.URDU) androidx.compose.ui.text.style.TextAlign.End else androidx.compose.ui.text.style.TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 2e. User Guide Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VioletBrandContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(Screen.Guide) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VioletBrand,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == AppLanguage.URDU) "ایپ استعمال کی مکمل گائیڈ (User Guide)" else "Rozana App User Guide",
                            fontWeight = FontWeight.Bold,
                            color = VioletBrandOnContainer,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = if (language == AppLanguage.URDU) "نماز، قرآن، اذکار، فیس اور بلز کا طریقہ کار" else "Tap here to learn how to use all features",
                            color = VioletBrandOnContainer.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = VioletBrand)
                }
            }
        }

        // 2f. 8-Chip Quick Grid for School Fee, Food, Electric, Gas, Water, Internet, Other
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.URDU) "فوری اخراجات و بلز (Quick Entries)" else "Quick Expenses & Bills",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(
                        onClick = { showCategoryManagerDialog = true },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == AppLanguage.URDU) "کیٹیگریز سیٹنگ" else "Categories ⚙", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickEntryChip("سکول فیس", "School Fee", Icons.Default.School, Color(0xFF6028E4), Color(0xFFECE6FD), Modifier.weight(1f)) {
                        onOpenAddTransaction(TransactionType.EXPENSE)
                    }
                    QuickEntryChip("کھانا راشن", "Food", Icons.Default.Restaurant, Color(0xFF00C48C), Color(0xFFE6F9F3), Modifier.weight(1f)) {
                        onOpenAddTransaction(TransactionType.EXPENSE)
                    }
                    QuickEntryChip("بجلی بل", "Electric", Icons.Default.ElectricBolt, Color(0xFFF59E0B), Color(0xFFFEF6E7), Modifier.weight(1f)) {
                        onOpenAddBill()
                    }
                    QuickEntryChip("گیس بل", "Gas Bill", Icons.Default.LocalFireDepartment, Color(0xFFEF4444), Color(0xFFFEECEB), Modifier.weight(1f)) {
                        onOpenAddBill()
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickEntryChip("انٹرنیٹ", "Internet", Icons.Default.Wifi, Color(0xFF2563EB), Color(0xFFEFF6FF), Modifier.weight(1f)) {
                        onOpenAddBill()
                    }
                    QuickEntryChip("پانی بل", "Water", Icons.Default.WaterDrop, Color(0xFF0284C7), Color(0xFFE0F2FE), Modifier.weight(1f)) {
                        onOpenAddBill()
                    }
                    QuickEntryChip("دیگر خرچ", "Other Exp", Icons.Default.Receipt, Color(0xFF7C3AED), Color(0xFFF5F3FF), Modifier.weight(1f)) {
                        onOpenAddTransaction(TransactionType.EXPENSE)
                    }
                    QuickEntryChip("دیگر بلز", "Other Bills", Icons.Default.FolderOpen, Color(0xFFE11D48), Color(0xFFFFF1F2), Modifier.weight(1f)) {
                        onOpenAddBill()
                    }
                }
            }
        }

        // 2g. Bad Addiction Recovery Card (عادات و نشہ سے نجات کا ٹریکر اور تدوین)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.URDU) habitTitleUr else habitTitleEn,
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { showHabitSettingsDialog = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Tune,
                                    contentDescription = "Edit Habit Settings",
                                    tint = Color.White.copy(alpha = 0.95f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = if (language == AppLanguage.URDU) habitSubtitleUr else habitSubtitleEn,
                            color = Color.White.copy(alpha = 0.88f),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    viewModel.incrementUrgesResisted()
                                    Toast.makeText(context, if (isUr) "ماشاءاللہ! وسوسہ کامیابی سے روکا گیا" else "Urge resisted successfully!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White, contentColor = Color(0xFF065F46)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isUr) "وسوسہ روکا! ($urgesResisted)" else "Resisted ($urgesResisted)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.clickable {
                            showHabitSettingsDialog = true
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$cleanDays",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (isUr) "دن کلین (تدوین ⚙)" else "Days (Edit ⚙)",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2h. Daily Water & Exercise Tracker (پانی اور صحت کا روزانہ ٹریکر و سیٹنگ)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isUr) "💧 روزانہ پانی ($waterDailyGoal گلاس)" else "💧 Daily Water ($waterDailyGoal Glasses)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { showWaterSettingsDialog = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Tune,
                                    contentDescription = "Water Settings",
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "$waterGlasses / $waterDailyGoal Glasses",
                            color = Color(0xFF0284C7),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val displayGoal = waterDailyGoal.coerceIn(4, 12)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 1..displayGoal) {
                            val filled = i <= waterGlasses
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (filled) Color(0xFF0284C7) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clickable {
                                        if (filled && i == waterGlasses) {
                                            viewModel.removeWaterGlass()
                                        } else {
                                            viewModel.setWaterGlasses(i)
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = if (filled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUr) "گلاس پر ٹیپ کریں یا سیٹنگ سے ہدف بدلیں" else "Tap glass to log or change target",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (waterGlasses > 0) {
                                OutlinedIconButton(
                                    onClick = { viewModel.removeWaterGlass() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Undo glass", modifier = Modifier.size(14.dp))
                                }
                            }
                            FilledTonalButton(
                                onClick = {
                                    viewModel.addWaterGlass()
                                    Toast.makeText(context, if (isUr) "شاباش! ایک اور گلاس پانی پیا" else "+1 Glass logged!", Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isUr) "پانی پیا" else "+1 Glass", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = if (isUr) "پروفائل اور تصویر سیٹ کریں" else "Edit Profile & Photo",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(VioletBrand),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileBitmap != null) {
                            Image(
                                bitmap = profileBitmap,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = tempName.take(1).ifEmpty { "M" },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isUr) "گیلری سے تصویر منتخب کریں" else "Pick Photo from Gallery")
                    }

                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text(if (isUr) "آپ کا نام" else "Your Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserName(tempName)
                        showEditProfileDialog = false
                        Toast.makeText(context, if (isUr) "نام محفوظ ہو گیا" else "Profile saved!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(if (isUr) "محفوظ کریں" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text(if (isUr) "منسوخ" else "Cancel")
                }
            }
        )
    }

    if (showShareAppDialog) {
        AlertDialog(
            onDismissRequest = { showShareAppDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF00C48C))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUr) "ایپ انسٹال اور شیئر کریں" else "Install & Share Rozana App",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isUr)
                            "1. اپنے موبائل میں انسٹال کرنے کے لیے APK فائل ڈائریکٹ حاصل کریں یا ڈاؤنلوڈ کریں۔\n2. واٹس ایپ پر کسی بھی دوست یا فیملی کو بغیر کسی پابندی کے شیئر کریں۔"
                        else
                            "1. Download Rozana.apk directly to install on any Android phone.\n2. Share the APK file or download link via WhatsApp with anyone!"
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Rozana.apk (21 MB) - 100% Offline, Fast & Free",
                            modifier = Modifier.padding(10.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showShareAppDialog = false
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Rozana – روزانہ (All-in-One Daily Life, Namaz, Quran, Azkar & Bills App for Pakistan)\nDownload & Install directly: https://ais-pre-csznmc4fckb7mu6v5pcya6-896921133693.asia-southeast1.run.app/rozana.apk"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Rozana App via"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C48C))
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isUr) "شیئر کریں (Share)" else "Share Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareAppDialog = false }) {
                    Text(if (isUr) "بند کریں" else "Close")
                }
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
                Toast.makeText(context, if (isUr) "نماز کے اوقات محفوظ ہو گئے ہیں!" else "Prayer times saved successfully!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, if (isUr) "پانی کا ہدف اور گلاسز محفوظ ہو گئے!" else "Water settings saved successfully!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, if (isUr) "عادت کی سیٹنگ محفوظ ہو گئی!" else "Habit settings saved successfully!", Toast.LENGTH_SHORT).show()
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

    if (showEditBalanceDialog) {
        EditBalanceDialog(
            currentStartingBalance = startingBalance,
            language = language,
            onDismiss = { showEditBalanceDialog = false },
            onSave = { newBal ->
                viewModel.updateStartingBalance(newBal)
                showEditBalanceDialog = false
                Toast.makeText(
                    context,
                    if (isUr) "ابتدائی نقد بیلنس کامیابی سے محفوظ ہو گیا!" else "Starting balance saved successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}

private fun parseTimeToMinutes(timeStr: String): Int {
    return try {
        val parts = timeStr.trim().split(" ")
        val hm = parts[0].split(":")
        var h = hm[0].toInt()
        val m = hm[1].toInt()
        val amPm = parts.getOrNull(1)?.uppercase() ?: "AM"
        if (amPm == "PM" && h < 12) h += 12
        if (amPm == "AM" && h == 12) h = 0
        h * 60 + m
    } catch (e: Exception) {
        0
    }
}

@Composable
fun PrayerSettingsDialog(
    currentFajr: String,
    currentDhuhr: String,
    currentAsr: String,
    currentMaghrib: String,
    currentIsha: String,
    isUr: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var fajr by remember { mutableStateOf(currentFajr) }
    var dhuhr by remember { mutableStateOf(currentDhuhr) }
    var asr by remember { mutableStateOf(currentAsr) }
    var maghrib by remember { mutableStateOf(currentMaghrib) }
    var isha by remember { mutableStateOf(currentIsha) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = RoyalElectricBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUr) "اوقاتِ نماز سیٹنگز (وقت تبدیل کریں)" else "Prayer Times Settings",
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
                        "اپنے علاقے یا مسجد کے مطابق نماز کے اوقات سیٹ کریں یا نیچے دیے گئے بٹن سے پری سیٹ منتخب کریں:"
                    else
                        "Adjust prayer times according to your local mosque or select a preset:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quick Presets Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Standard Pakistan
                    OutlinedButton(
                        onClick = {
                            fajr = "05:00 AM"
                            dhuhr = "01:15 PM"
                            asr = "04:45 PM"
                            maghrib = "06:05 PM"
                            isha = "07:45 PM"
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(if (isUr) "معیاری" else "Standard", fontSize = 11.sp)
                    }

                    // Summer (گرما)
                    OutlinedButton(
                        onClick = {
                            fajr = "04:30 AM"
                            dhuhr = "01:30 PM"
                            asr = "05:15 PM"
                            maghrib = "07:10 PM"
                            isha = "08:45 PM"
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(if (isUr) "گرما" else "Summer", fontSize = 11.sp)
                    }

                    // Winter (سرما)
                    OutlinedButton(
                        onClick = {
                            fajr = "05:30 AM"
                            dhuhr = "01:00 PM"
                            asr = "04:00 PM"
                            maghrib = "05:25 PM"
                            isha = "07:00 PM"
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(if (isUr) "سرما" else "Winter", fontSize = 11.sp)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                PrayerTimeInputField(label = if (isUr) "فجر (Fajr)" else "Fajr", value = fajr, onValueChange = { fajr = it })
                PrayerTimeInputField(label = if (isUr) "ظہر (Zuhr)" else "Zuhr", value = dhuhr, onValueChange = { dhuhr = it })
                PrayerTimeInputField(label = if (isUr) "عصر (Asr)" else "Asr", value = asr, onValueChange = { asr = it })
                PrayerTimeInputField(label = if (isUr) "مغرب (Maghrib)" else "Maghrib", value = maghrib, onValueChange = { maghrib = it })
                PrayerTimeInputField(label = if (isUr) "عشاء (Isha)" else "Isha", value = isha, onValueChange = { isha = it })
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(fajr, dhuhr, asr, maghrib, isha) },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalElectricBlue)
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
fun PrayerTimeInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.width(135.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
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

@Composable
fun QuickEntryChip(
    titleUr: String,
    titleEn: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp).fillMaxWidth(),
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
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = titleUr,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                fontSize = 10.sp
            )
            Text(
                text = titleEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun FloatingFeatureCard(
    titleUr: String,
    titleEn: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                }

                // 5-Star Rating (★★★★★) matching reference product card
                Row {
                    repeat(5) {
                        Text(
                            text = "★",
                            color = VibrantSky,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = titleUr,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = titleEn,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = RoyalElectricBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
