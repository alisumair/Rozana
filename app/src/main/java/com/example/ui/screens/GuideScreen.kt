package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.viewmodel.RozanaViewModel
import com.example.ui.viewmodel.Screen

data class GuideItem(
    val titleEn: String,
    val titleUr: String,
    val descEn: String,
    val descUr: String,
    val icon: ImageVector,
    val iconColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(
    viewModel: RozanaViewModel
) {
    val language by viewModel.language.collectAsState()
    val isUr = language == AppLanguage.URDU

    val guides = listOf(
        GuideItem(
            titleEn = "1. Profile Photo & Name Setup",
            titleUr = "1. اپنی تصویر اور نام لگانا",
            descEn = "Tap your avatar at the top right to upload your personal picture from camera or gallery. You can update your name to Mohsin and location.",
            descUr = "اوپر دائیں جانب اواتار پر کلک کر کے کیمرے یا گیلری سے اپنی تصویر لگائیں اور نام و شہر تبدیل کریں۔",
            icon = Icons.Default.AccountCircle,
            iconColor = Color(0xFF6028E4)
        ),
        GuideItem(
            titleEn = "2. Prayer Times & Azan Alerts",
            titleUr = "2. اوقاتِ نماز اور اذان نوٹیفیکیشنز",
            descEn = "The main display shows live prayer times (Fajr, Dhuhr, Asr, Maghrib, Isha). Tap the bell icon on any prayer to turn notifications on or off.",
            descUr = "مین ڈسپلے پر پانچوں نمازوں کے لائیو اوقات موجود ہیں۔ کسی بھی نماز پر گھنٹی کے نشان پر ٹیپ کر کے الرٹ آن یا آف کریں۔",
            icon = Icons.Default.NotificationsActive,
            iconColor = Color(0xFF059669)
        ),
        GuideItem(
            titleEn = "3. Lunch & Dinner Meal Alerts",
            titleUr = "3. لنچ اور ڈینر ریمائنڈرز",
            descEn = "Never miss meals: Breakfast (08:00 AM), Lunch (01:30 PM), and Dinner (08:30 PM) alerts remind you automatically.",
            descUr = "کھانے کے اوقات: صبح ناشتہ (08:00)، دوپہر لنچ (01:30) اور رات ڈینر (08:30) پر وقت پر یاد دہانی۔",
            icon = Icons.Default.Restaurant,
            iconColor = Color(0xFFD97706)
        ),
        GuideItem(
            titleEn = "4. Holy Quran & Subah k Azkar",
            titleUr = "4. قرآن مجید اور صبح کے اذکار",
            descEn = "Tap the golden Quran folder to read Surah Ya-Sin, Al-Mulk, Al-Fatihah, and 4 Quls with Urdu translation. Open the Azkar folder for morning/evening supplications and digital tasbeeh counter.",
            descUr = "گولڈن قرآن فولڈر پر ٹیپ کر کے سورۃ یٰسین، الملک اور 4 قل اردو ترجمہ کے ساتھ پڑھیں۔ اذکار فولڈر سے صبح کے مسنون اذکار اور ڈیجیٹل تسبیح استعمال کریں۔",
            icon = Icons.Default.MenuBook,
            iconColor = Color(0xFFB45309)
        ),
        GuideItem(
            titleEn = "5. School Fee, Food, Electric & Gas Bills",
            titleUr = "5. سکول فیس، بجلی، گیس اور راشن کا اندراج",
            descEn = "Use 1-tap quick buttons on the dashboard for School Fee, Food & Ration, LESCO Electric Bill, SNGPL Gas Bill, and other expenses. Track due dates and mark them Paid.",
            descUr = "ڈیش بورڈ پر موجود 8 فوری چپس کے ذریعے سکول فیس، راشن، بجلی اور گیس کے بلز درج کریں اور ادا شدہ مارک کریں۔",
            icon = Icons.Default.ReceiptLong,
            iconColor = Color(0xFF2563EB)
        ),
        GuideItem(
            titleEn = "6. Bad Addiction Recovery & Healthy Habits",
            titleUr = "6. بد عادتوں سے نجات اور ورزش و پانی",
            descEn = "Track clean streak days from smoking, excessive screen time, and junk food. Tap 'Resisted Urge' whenever you overcome a temptation. Track 8 daily water glasses.",
            descUr = "سگریٹ، غیر ضروری سکرین ٹائم اور فاسٹ فوڈ سے پرہیز کی اسٹریک چلائیں۔ جب بھی وسوسہ روکا، فتح بٹن دبائیں۔ روزانہ 8 گلاس پانی کا ٹریکر استعمال کریں۔",
            icon = Icons.Default.Psychology,
            iconColor = Color(0xFF10B981)
        ),
        GuideItem(
            titleEn = "7. How to Install & Share APK with Anyone",
            titleUr = "7. ایپ کو انسٹال اور لامحدود شیئر کرنا",
            descEn = "Tap the green [APK] button at the top header to download Rozana.apk (21 MB). You can install it on your Android phone and send the APK file via WhatsApp to anyone without restrictions!",
            descUr = "اوپر ہیڈر میں سبز [APK] بٹن دبائیں اور 21 MB کی مکمل ایپ ڈاؤنلوڈ کریں۔ آپ اسے اپنے فون میں انسٹال کر سکتے ہیں اور واٹس ایپ پر جس کے ساتھ چاہیں شیئر کر سکتے ہیں!",
            icon = Icons.Default.Share,
            iconColor = Color(0xFF00C48C)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isUr) "ایپ استعمال کی مکمل گائیڈ" else "Rozana User Guide",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isUr) "خوش آمدید محسن صاحب!" else "Welcome Mohsin!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isUr) "اس گائیڈ میں روزانہ ایپ کے ہر فیچر کو آسان الفاظ میں سمجھایا گیا ہے تاکہ آپ کو کسی اور ایپ کی ضرورت نہ پڑے۔"
                            else "This guide explains every feature of Rozana so you have everything in one place.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            items(guides) { guide ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = guide.iconColor.copy(alpha = 0.12f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = guide.icon,
                                    contentDescription = null,
                                    tint = guide.iconColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isUr) guide.titleUr else guide.titleEn,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isUr) guide.descUr else guide.descEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
