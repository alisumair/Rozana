package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.VioletBrand
import com.example.ui.viewmodel.RozanaViewModel
import com.example.ui.viewmodel.Screen

data class QuranVerse(val arabic: String, val urdu: String, val verseNum: Int)

data class QuranSurahItem(
    val number: Int,
    val nameAr: String,
    val nameEn: String,
    val versesCount: Int,
    val verses: List<QuranVerse>
)

val sampleSurahs = listOf(
    QuranSurahItem(
        number = 1,
        nameAr = "سُورَةُ الفَاتِحَة",
        nameEn = "Al-Fatihah",
        versesCount = 7,
        verses = listOf(
            QuranVerse("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "شروع اللہ کے نام سے جو بڑا مہربان نہایت رحم والا ہے", 1),
            QuranVerse("الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "سب تعریفیں اللہ کے لیے ہیں جو تمام جہانوں کا پالنے والا ہے", 2),
            QuranVerse("الرَّحْمَٰنِ الرَّحِيمِ", "بڑا مہربان نہایت رحم فرمانے والا", 3),
            QuranVerse("مَالِكِ يَوْمِ الدِّينِ", "روز جزا کا مالک", 4),
            QuranVerse("إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "ہم تیری ہی عبادت کرتے ہیں اور تجھ ہی سے مدد مانگتے ہیں", 5),
            QuranVerse("اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "ہمیں سیدھے راستے پر چلا", 6),
            QuranVerse("صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "ان لوگوں کے راستے پر جن پر تو نے انعام فرمایا، نہ ان کے جن پر غضب کیا گیا اور نہ گمراہوں کے راستے پر", 7)
        )
    ),
    QuranSurahItem(
        number = 36,
        nameAr = "سُورَةُ يس",
        nameEn = "Ya-Sin",
        versesCount = 83,
        verses = listOf(
            QuranVerse("يس ۝ وَالْقُرْآنِ الْحَكِيمِ ۝ إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "یس۔ قسم ہے حکمت والے قرآن کی۔ بیشک آپ ضرور رسولوں میں سے ہیں", 1),
            QuranVerse("عَلَىٰ صِرَاطٍ مُسْتَقِيمٍ ۝ تَنزِيلَ الْعَزِيزِ الرَّحِيمِ", "سیدھے راستے پر۔ یہ زبردست رحم والے کی طرف سے نازل کیا ہوا ہے", 2),
            QuranVerse("لِتُنذِرَ قَوْمًا مَّا أُنذِرَ آبَاؤُهُمْ فَهُمْ غَافِلُونَ", "تاکہ آپ ایسے لوگوں کو ڈرائیں جن کے باپ دادا کو نہیں ڈرایا گیا تھا تو وہ غافل ہیں", 3)
        )
    ),
    QuranSurahItem(
        number = 67,
        nameAr = "سُورَةُ المُلْك",
        nameEn = "Al-Mulk",
        versesCount = 30,
        verses = listOf(
            QuranVerse("تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "نہایت بابرکت ہے وہ ذات جس کے ہاتھ میں ساری بادشاہی ہے اور وہ ہر چیز پر قادر ہے", 1),
            QuranVerse("الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "جس نے موت اور زندگی کو پیدا کیا تاکہ تمہیں آزمائے کہ تم میں سے کون اچھے عمل والا ہے", 2)
        )
    ),
    QuranSurahItem(
        number = 112,
        nameAr = "سُورَةُ الإِخْلَاص",
        nameEn = "Al-Ikhlas",
        versesCount = 4,
        verses = listOf(
            QuranVerse("قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ", "کہہ دیجیے کہ وہ اللہ ایک ہے۔ اللہ بے نیاز ہے", 1),
            QuranVerse("لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "نہ اس سے کوئی پیدا ہوا اور نہ وہ کسی سے پیدا ہوا، اور نہ کوئی اس کا ہمسر ہے", 2)
        )
    ),
    QuranSurahItem(
        number = 113,
        nameAr = "سُورَةُ الفَلَق",
        nameEn = "Al-Falaq",
        versesCount = 5,
        verses = listOf(
            QuranVerse("قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ", "کہہ دیجیے کہ میں صبح کے رب کی پناہ مانگتا ہوں، اس کی تمام مخلوقات کے شر سے", 1),
            QuranVerse("وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "اور اندھیری رات کے شر سے جب وہ چھا جائے، اور گرہوں میں پھونکنے والیوں کے شر سے", 2)
        )
    ),
    QuranSurahItem(
        number = 114,
        nameAr = "سُورَةُ النَّاس",
        nameEn = "An-Nas",
        versesCount = 6,
        verses = listOf(
            QuranVerse("قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ مَلِكِ النَّاسِ ۝ إِلَٰهِ النَّاسِ", "کہہ دیجیے کہ میں تمام انسانوں کے رب، انسانوں کے بادشاہ، انسانوں کے معبود کی پناہ مانگتا ہوں", 1),
            QuranVerse("مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "بار بار وسوسہ ڈال کر پیچھے ہٹ جانے والے کے شر سے، جو لوگوں کے سینوں میں وسوسہ ڈالتا ہے", 2)
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    viewModel: RozanaViewModel
) {
    val language by viewModel.language.collectAsState()
    val isUr = language == AppLanguage.URDU
    var searchQuery by remember { mutableStateOf("") }

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) sampleSurahs
        else sampleSurahs.filter {
            it.nameAr.contains(searchQuery, ignoreCase = true) ||
            it.nameEn.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = if (isUr) "قرآن مجید (القرآن الکریم)" else "Holy Quran – قرآن مجید",
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    placeholder = { Text(if (isUr) "سورت تلاش کریں (مثلاً یٰسین، الملک)..." else "Search Surah...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }

            items(filteredSurahs, key = { it.number }) { surah ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = "${surah.number}. ${surah.nameEn}",
                                    color = Color(0xFFB45309),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = surah.nameAr,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        surah.verses.forEach { verse ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = verse.arabic,
                                    fontSize = 19.sp,
                                    lineHeight = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = verse.urdu,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
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
