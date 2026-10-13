package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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

data class ZikrItem(
    val id: String,
    val category: String,
    val arabic: String,
    val urdu: String,
    val target: Int,
    var currentCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarScreen(
    viewModel: RozanaViewModel
) {
    val language by viewModel.language.collectAsState()
    val isUr = language == AppLanguage.URDU
    var selectedTab by remember { mutableStateOf(0) }

    val morningAzkar = remember {
        mutableStateListOf(
            ZikrItem("m1", "صبح کے اذکار", "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ", "ہم نے صبح کی اور اللہ کے سارے ملک نے صبح کی، اور تمام تعریف اللہ ہی کے لیے ہے، اللہ کے سوا کوئی معبود نہیں وہ اکیلا ہے اس کا کوئی شریک نہیں", 1),
            ZikrItem("m2", "صبح کے اذکار", "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ", "اللہ کے نام سے جس کے نام کے ساتھ زمین و آسمان کی کوئی چیز نقصان نہیں پہنچا سکتی اور وہ خوب سننے اور جاننے والا ہے", 3),
            ZikrItem("m3", "صبح کے اذکار", "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ (سید الاستغفار)", "اے اللہ! تو ہی میرا رب ہے، تیرے سوا کوئی معبود نہیں، تو نے مجھے پیدا کیا اور میں تیرا بندہ ہوں", 1),
            ZikrItem("m4", "صبح کے اذکار", "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا", "میں اللہ کے رب ہونے پر، اسلام کے دین ہونے پر، اور محمد ﷺ کے نبی ہونے پر راضی ہوا", 3)
        )
    }

    val eveningAzkar = remember {
        mutableStateListOf(
            ZikrItem("e1", "شام کے اذکار", "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ", "ہم نے شام کی اور اللہ کے سارے ملک نے شام کی، اور سب تعریف اللہ کے لیے ہے", 1),
            ZikrItem("e2", "شام کے اذکار", "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ", "میں اللہ کے کامل کلمات کی پناہ مانگتا ہوں اس کی تمام مخلوقات کے شر سے", 3),
            ZikrItem("e3", "شام کے اذکار", "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ", "اے اللہ! تیرے ہی حکم سے ہم نے شام کی اور تیرے ہی حکم سے صبح کی، اور تیرے ہی حکم سے ہم جیتے اور مرتے ہیں", 1)
        )
    }

    var tasbeehCount by remember { mutableStateOf(0) }
    var tasbeehZikr by remember { mutableStateOf("سُبْحَانَ اللَّهِ") }

    val currentList = if (selectedTab == 0) morningAzkar else eveningAzkar

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = if (isUr) "صبح و شام کے مسنون اذکار" else "Masnoon Daily Azkar",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (isUr) "صبح کے اذکار" else "Morning") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (isUr) "شام کے اذکار" else "Evening") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(if (isUr) "تسبیح کاؤنٹر" else "Tasbeeh") }
                )
            }

            if (selectedTab == 2) {
                // Interactive Digital Tasbeeh
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = tasbeehZikr,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = tasbeehZikr == "سُبْحَانَ اللَّهِ",
                            onClick = { tasbeehZikr = "سُبْحَانَ اللَّهِ" },
                            label = { Text("سبحان الله") }
                        )
                        FilterChip(
                            selected = tasbeehZikr == "الْحَمْدُ لِلَّهِ",
                            onClick = { tasbeehZikr = "الْحَمْدُ لِلَّهِ" },
                            label = { Text("الحمد لله") }
                        )
                        FilterChip(
                            selected = tasbeehZikr == "اللَّهُ أَكْبَرُ",
                            onClick = { tasbeehZikr = "اللَّهُ أَكْبَرُ" },
                            label = { Text("الله أكبر") }
                        )
                        FilterChip(
                            selected = tasbeehZikr == "أَسْتَغْفِرُ اللَّهَ",
                            onClick = { tasbeehZikr = "أَسْتَغْفِرُ اللَّهَ" },
                            label = { Text("استغفر الله") }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Large Digital Circle Button
                    FilledIconButton(
                        onClick = { tasbeehCount++ },
                        modifier = Modifier.size(140.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = VioletBrand)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$tasbeehCount",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (isUr) "ٹیپ کریں (+1)" else "Tap (+1)",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedButton(
                        onClick = { tasbeehCount = 0 },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isUr) "ری سیٹ (0 کریں)" else "Reset Counter")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(currentList, key = { it.id }) { zikr ->
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
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = zikr.category,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Text(
                                        text = "${if (isUr) "ہدف" else "Target"}: ${zikr.target} ${if (isUr) "بار" else "times"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = zikr.arabic,
                                    fontSize = 18.sp,
                                    lineHeight = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = zikr.urdu,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isUr) "پڑھا گیا: " else "Read: ",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${zikr.currentCount} / ${zikr.target}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(
                                            onClick = {
                                                val idx = currentList.indexOfFirst { it.id == zikr.id }
                                                if (idx != -1) {
                                                    currentList[idx] = currentList[idx].copy(currentCount = 0)
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = "Reset")
                                        }

                                        Button(
                                            onClick = {
                                                val idx = currentList.indexOfFirst { it.id == zikr.id }
                                                if (idx != -1) {
                                                    currentList[idx] = currentList[idx].copy(currentCount = zikr.currentCount + 1)
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("+1")
                                        }
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
    }
}
