package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.VioletBrand
import com.example.ui.viewmodel.RozanaViewModel
import com.example.ui.viewmodel.Screen

data class MasnoonDua(
    val id: String,
    val titleUr: String,
    val titleEn: String,
    val category: String,
    val arabic: String,
    val urdu: String,
    val english: String,
    val reference: String,
    val target: Int = 1,
    var readCount: Int = 0
)

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
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val language by viewModel.language.collectAsState()
    val isUr = language == AppLanguage.URDU

    // 0: Masnoon Duas, 1: Morning, 2: Evening, 3: Tasbeeh
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("سب") }

    val allDuas = remember {
        mutableStateListOf(
            MasnoonDua(
                id = "d1",
                titleUr = "کھانے سے پہلے کی دعا",
                titleEn = "Before Eating",
                category = "کھانے پینے",
                arabic = "بِسْمِ اللَّهِ وَعَلَى بَرَكَةِ اللَّهِ",
                urdu = "اللہ کے نام سے اور اللہ کی برکت پر شروع کرتا ہوں۔",
                english = "In the name of Allah and with the blessings of Allah.",
                reference = "سنن ابوداؤد"
            ),
            MasnoonDua(
                id = "d2",
                titleUr = "کھانے کے بعد کی دعا",
                titleEn = "After Eating",
                category = "کھانے پینے",
                arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
                urdu = "تمام تعریفیں اللہ کے لیے ہیں جس نے ہمیں کھلایا، پلایا اور مسلمان بنایا۔",
                english = "Praise be to Allah Who has fed us and given us drink and made us Muslims.",
                reference = "جامع الترمذی"
            ),
            MasnoonDua(
                id = "d3",
                titleUr = "کھانے کے شروع میں بسم اللہ بھول جانے پر",
                titleEn = "When Forgetting Bismillah",
                category = "کھانے پینے",
                arabic = "بِسْمِ اللَّهِ أَوَّلَهُ وَآخِرَهُ",
                urdu = "اللہ کے نام سے اس کے اول اور آخر میں۔",
                english = "In the name of Allah at its beginning and end.",
                reference = "سنن ابوداؤد و ترمذی"
            ),
            MasnoonDua(
                id = "d4",
                titleUr = "دودھ پینے کے بعد کی دعا",
                titleEn = "After Drinking Milk",
                category = "کھانے پینے",
                arabic = "اللَّهُمَّ بَارِكْ لَنَا فِيهِ وَزِدْنَا مِنْهُ",
                urdu = "اے اللہ! ہمارے لیے اس میں برکت عطا فرما اور ہمیں اس سے زیادہ عنایت فرما۔",
                english = "O Allah, bless it for us and give us more of it.",
                reference = "جامع الترمذی"
            ),
            MasnoonDua(
                id = "d5",
                titleUr = "سونے سے پہلے کی دعا",
                titleEn = "Before Sleeping",
                category = "نیند و بیداری",
                arabic = "اللَّهُمَّ بِاسْمِكَ أَمُوتُ وَأَحْيَا",
                urdu = "اے اللہ! میں تیرے ہی نام کے ساتھ مرتا (سوتا) ہوں اور جیتا (جاگتا) ہوں۔",
                english = "O Allah, in Your name I die and I live.",
                reference = "صحیح بخاری"
            ),
            MasnoonDua(
                id = "d6",
                titleUr = "سو کر بیدار ہونے کی دعا",
                titleEn = "Upon Waking Up",
                category = "نیند و بیداری",
                arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                urdu = "تمام تعریفیں اللہ کے لیے ہیں جس نے ہمیں موت (نیند) کے بعد زندہ کیا اور اسی کی طرف اٹھ کر جانا ہے۔",
                english = "Praise is to Allah Who gave us life after He caused us to die, and unto Him is the resurrection.",
                reference = "صحیح بخاری"
            ),
            MasnoonDua(
                id = "d7",
                titleUr = "گھر سے نکلتے وقت کی دعا",
                titleEn = "Leaving the Home",
                category = "گھر و مسجد",
                arabic = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
                urdu = "اللہ کے نام سے، میں نے اللہ پر بھروسہ کیا، اور گناہوں سے بچنے اور نیکی کرنے کی کوئی طاقت نہیں مگر اللہ ہی کی مدد سے۔",
                english = "In the name of Allah, I trust in Allah; there is no power and no might except with Allah.",
                reference = "سنن ابوداؤد و ترمذی"
            ),
            MasnoonDua(
                id = "d8",
                titleUr = "گھر میں داخل ہونے کی دعا",
                titleEn = "Entering the Home",
                category = "گھر و مسجد",
                arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ الْمَوْلِجِ وَخَيْرَ الْمَخْرَجِ، بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا",
                urdu = "اے اللہ! میں تجھ سے داخل ہونے اور نکلنے کی خیر مانگتا ہوں، اللہ کے نام سے ہم داخل ہوئے اور اللہ کے نام سے ہم نکلے اور اپنے پروردگار اللہ پر ہم نے بھروسہ کیا۔",
                english = "O Allah, I ask You for the best entrance and the best exit. In the name of Allah we enter...",
                reference = "سنن ابوداؤد"
            ),
            MasnoonDua(
                id = "d9",
                titleUr = "مسجد میں داخل ہونے کی دعا",
                titleEn = "Entering the Mosque",
                category = "گھر و مسجد",
                arabic = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
                urdu = "اے اللہ! میرے لیے اپنی رحمت کے دروازے کھول دے۔",
                english = "O Allah, open for me the gates of Your mercy.",
                reference = "صحیح مسلم"
            ),
            MasnoonDua(
                id = "d10",
                titleUr = "مسجد سے نکلنے کی دعا",
                titleEn = "Exiting the Mosque",
                category = "گھر و مسجد",
                arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
                urdu = "اے اللہ! میں تجھ سے تیرے فضل و کرم کا سوال کرتا ہوں۔",
                english = "O Allah, I ask You from Your bounty.",
                reference = "صحیح مسلم"
            ),
            MasnoonDua(
                id = "d11",
                titleUr = "بیت الخلاء (واش روم) جانے کی دعا",
                titleEn = "Entering the Washroom",
                category = "طہارت و عبادات",
                arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ",
                urdu = "اے اللہ! میں ناپاک جنوں اور ناپاک جننیوں (تمام شر) سے تیری پناہ مانگتا ہوں۔",
                english = "O Allah, I seek refuge in You from the male and female evil spirits.",
                reference = "صحیح بخاری و مسلم"
            ),
            MasnoonDua(
                id = "d12",
                titleUr = "بیت الخلاء سے نکلنے کی دعا",
                titleEn = "Exiting the Washroom",
                category = "طہارت و عبادات",
                arabic = "غُفْرَانَكَ، الْحَمْدُ لِلَّهِ الَّذِي أَذْهَبَ عَنِّي الْأَذَى وَعَافَانِي",
                urdu = "اے اللہ! میں تیری بخشش چاہتا ہوں، تمام تعریف اللہ کے لیے ہے جس نے مجھ سے تکلیف دور کی اور مجھے عافیت بخشی۔",
                english = "I seek Your forgiveness. Praise be to Allah who relieved me of suffering and granted me health.",
                reference = "جامع الترمذی و ابن ماجہ"
            ),
            MasnoonDua(
                id = "d13",
                titleUr = "وضو کے بعد کی جامع مسنون دعا",
                titleEn = "After Completing Wudu",
                category = "طہارت و عبادات",
                arabic = "أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ، اللَّهُمَّ اجْعَلْنِي مِنَ التَّوَّابِينَ وَاجْعَلْنِي مِنَ الْمُتَطَهِّرِينَ",
                urdu = "میں گواہی دیتا ہوں کہ اللہ کے سوا کوئی معبود نہیں وہ اکیلا ہے، اور محمد ﷺ اس کے بندے اور رسول ہیں۔ اے اللہ! مجھے خوب توبہ کرنے والوں اور خوب پاک صاف رہنے والوں میں شامل فرما۔",
                english = "I testify that none has the right to be worshipped except Allah alone... O Allah, make me of the repentant and purified.",
                reference = "جامع الترمذی (جنت کے ۸ دروازے کھلتے ہیں)"
            ),
            MasnoonDua(
                id = "d14",
                titleUr = "سواری اور سفر پر روانگی کی دعا",
                titleEn = "Vehicle & Travel Prayer",
                category = "سفر و مارکیٹ",
                arabic = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ، وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ",
                urdu = "پاک ہے وہ ذات جس نے اس سواری کو ہمارے تابع کر دیا حالانکہ ہم اسے قابو میں لانے والے نہ تھے، اور یقیناً ہم سب اپنے رب ہی کی طرف لوٹنے والے ہیں۔",
                english = "Glory to Him Who has subjected this to us, and we could never have it by ourselves. And verily to our Lord we are returning.",
                reference = "صحیح مسلم"
            ),
            MasnoonDua(
                id = "d15",
                titleUr = "بازار اور مارکیٹ میں داخل ہونے کی دعا",
                titleEn = "Entering Marketplace",
                category = "سفر و مارکیٹ",
                arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، يُحْيِي وَيُمِيتُ وَهُوَ حَيٌّ لَا يَمُوتُ، بِيَدِهِ الْخَيْرُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                urdu = "اللہ کے سوا کوئی معبود نہیں وہ اکیلا ہے اس کا کوئی شریک نہیں، اسی کی بادشاہی اور تعریف ہے، وہی زندہ کرتا اور مارتا ہے اور وہ ہمیشہ زندہ رہنے والا ہے، تمام بھلائی اسی کے ہاتھ میں ہے اور وہ ہر چیز پر قادر ہے۔",
                english = "There is no God but Allah alone without partner... in His hand is all good and He has power over everything.",
                reference = "جامع الترمذی (دس لاکھ نیکیاں)"
            ),
            MasnoonDua(
                id = "d16",
                titleUr = "قرض اور مالی تنگی سے نجات کی دعا",
                titleEn = "Relief from Debt & Poverty",
                category = "شفاء و پریشانی",
                arabic = "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ، وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ",
                urdu = "اے اللہ! مجھے حلال رزق کے ذریعے حرام سے محفوظ فرما، اور اپنے فضل و کرم سے مجھے اپنے سوا ہر کسی سے بے نیاز کر دے۔",
                english = "O Allah, suffice me with what is lawful against what is unlawful, and enrich me with Your grace from all besides You.",
                reference = "جامع الترمذی"
            ),
            MasnoonDua(
                id = "d17",
                titleUr = "غم، فکر، سستی اور پریشانی دور کرنے کی دعا",
                titleEn = "Relief from Anxiety & Grief",
                category = "شفاء و پریشانی",
                arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْجُبْنِ وَالْبُخْلِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
                urdu = "اے اللہ! میں غم اور فکر سے، کمزوری اور سستی سے، بزدلی اور کنجوسی سے، اور قرض کے بوجھ اور لوگوں کے دباؤ سے تیری پناہ مانگتا ہوں۔",
                english = "O Allah, I seek refuge in You from grief and worry, weakness and laziness, cowardice and miserliness, and heavy debt.",
                reference = "صحیح بخاری"
            ),
            MasnoonDua(
                id = "d18",
                titleUr = "بیمار کی عیادت اور شفاء کی مسنون دعا",
                titleEn = "For Sickness & Shifa",
                category = "شفاء و پریشانی",
                arabic = "أَسْأَلُ اللَّهَ الْعَظِيمَ رَبَّ الْعَرْشِ الْعَظِيمِ أَنْ يَشْفِيَكَ",
                urdu = "میں عظمت والے اللہ، جو عرشِ عظیم کا رب ہے، سے سوال کرتا ہوں کہ وہ آپ کو شفائے کاملہ عطا فرمائے۔ (۷ مرتبہ پڑھیں)",
                english = "I ask Allah the Almighty, Lord of the Mighty Throne, to cure you. (Recite 7 times)",
                reference = "جامع الترمذی و ابوداؤد",
                target = 7
            ),
            MasnoonDua(
                id = "d19",
                titleUr = "آئینہ دیکھنے کی مسنون دعا",
                titleEn = "Looking in the Mirror",
                category = "روزمرہ معمولات",
                arabic = "اللَّهُمَّ كَمَا حَسَّنْتَ خَلْقِي فَحَسِّنْ خُلُقِي",
                urdu = "اے اللہ! جس طرح تو نے میری صورت خوبصورت بنائی ہے، اسی طرح میرے اخلاق بھی اچھے فرما دے۔",
                english = "O Allah, just as You made my appearance beautiful, make my character beautiful as well.",
                reference = "مسند احمد و حصن المسلم"
            ),
            MasnoonDua(
                id = "d20",
                titleUr = "نیا کپڑا یا لباس پہننے کی دعا",
                titleEn = "Wearing New Clothes",
                category = "روزمرہ معمولات",
                arabic = "الْحَمْدُ لِلَّهِ الَّذِي كَسَانِي هَذَا الثَّوْبَ وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
                urdu = "تمام تعریفیں اللہ کے لیے ہیں جس نے مجھے یہ لباس پہنایا اور میری کسی تدبیر اور طاقت کے بغیر مجھے یہ عطا فرمایا۔",
                english = "All praise is for Allah Who has clothed me with this garment and provided it without power from me.",
                reference = "سنن ابوداؤد"
            ),
            MasnoonDua(
                id = "d21",
                titleUr = "غصہ ٹھنڈا کرنے کی مسنون دعا",
                titleEn = "When Overcome by Anger",
                category = "روزمرہ معمولات",
                arabic = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ",
                urdu = "میں مردود شیطان کے شر سے اللہ کی پناہ مانگتا ہوں۔",
                english = "I seek refuge in Allah from Satan the outcast.",
                reference = "صحیح بخاری و مسلم"
            ),
            MasnoonDua(
                id = "d22",
                titleUr = "بارش برستے وقت کی دعا",
                titleEn = "During Rainfall",
                category = "روزمرہ معمولات",
                arabic = "اللَّهُمَّ صَيِّبًا نَافِعًا",
                urdu = "اے اللہ! اس بارش کو نفع بخش اور خوب برسنے والی بنا دے۔",
                english = "O Allah, make it an abundant, beneficial rain.",
                reference = "صحیح بخاری"
            ),
            MasnoonDua(
                id = "d23",
                titleUr = "بارش ختم ہونے کے بعد کی دعا",
                titleEn = "After Rain Stops",
                category = "روزمرہ معمولات",
                arabic = "مُطِرْنَا بِفَضْلِ اللَّهِ وَرَحْمَتِهِ",
                urdu = "ہم پر اللہ کے فضل اور اس کی رحمت سے بارش نازل ہوئی۔",
                english = "We have been given rain by the grace and mercy of Allah.",
                reference = "صحیح بخاری"
            ),
            MasnoonDua(
                id = "d24",
                titleUr = "مصیبت یا کسی نقصان پر صبر کی دعا",
                titleEn = "During Affliction or Loss",
                category = "شفاء و پریشانی",
                arabic = "إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ، اللَّهُمَّ أْجُرْنِي فِي مُصِيبَتِي وَأَخْلِفْ لِي خَيْرًا مِنْهَا",
                urdu = "ہم سب اللہ کے ہیں اور اسی کی طرف لوٹنے والے ہیں۔ اے اللہ! مجھے میری مصیبت پر اجر عطا فرما اور مجھے اس کے بدلے میں اس سے بہتر عطا فرما۔",
                english = "Indeed to Allah we belong and to Him we shall return. O Allah, reward me in my affliction and grant me better in its place.",
                reference = "صحیح مسلم"
            ),
            MasnoonDua(
                id = "d25",
                titleUr = "علم میں اضافے کی قرآنی دعا",
                titleEn = "Seeking Beneficial Knowledge",
                category = "روزمرہ معمولات",
                arabic = "رَّبِّ زِدْنِي عِلْمًا",
                urdu = "اے میرے پروردگار! میرے علم میں اضافہ فرما۔",
                english = "My Lord, increase me in knowledge.",
                reference = "سورۃ طٰہٰ: ۱۱۴"
            ),
            MasnoonDua(
                id = "d26",
                titleUr = "والدین کی مغفرت اور رحمت کی دعا",
                titleEn = "Dua for Parents",
                category = "روزمرہ معمولات",
                arabic = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
                urdu = "اے میرے پروردگار! ان دونوں پر رحم فرما جیسا کہ انہوں نے بچپن میں میری پرورش فرمائی۔",
                english = "My Lord, have mercy upon them as they brought me up when I was small.",
                reference = "سورۃ الاسراء: ۲۴"
            ),
            MasnoonDua(
                id = "d27",
                titleUr = "دنیا و آخرت کی ہر بھلائی کی جامع دعا",
                titleEn = "Comprehensive Goodness in Dunya & Akhirah",
                category = "روزمرہ معمولات",
                arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                urdu = "اے ہمارے رب! ہمیں دنیا میں بھی بھلائی عطا فرما اور آخرت میں بھی بھلائی عطا فرما اور ہمیں جہنم کے عذاب سے بچا۔",
                english = "Our Lord, give us in this world what is good and in the Hereafter what is good and protect us from the fire.",
                reference = "سورۃ البقرۃ: ۲۰۱"
            )
        )
    }

    val morningAzkar = remember {
        mutableStateListOf(
            ZikrItem("m1", "صبح کے اذکار", "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ", "ہم نے صبح کی اور اللہ کے سارے ملک نے صبح کی، اور تمام تعریف اللہ ہی کے لیے ہے، اللہ کے سوا کوئی معبود نہیں وہ اکیلا ہے اس کا کوئی شریک نہیں", 1),
            ZikrItem("m2", "صبح کے اذکار", "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ", "اللہ کے نام سے جس کے نام کی برکت سے زمین و آسمان کی کوئی چیز نقصان نہیں پہنچا سکتی اور وہ خوب سننے اور جاننے والا ہے (صبح شام ۳ مرتبہ)", 3),
            ZikrItem("m3", "سید الاستغفار", "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ", "اے اللہ! تو ہی میرا پروردگار ہے، تیرے سوا کوئی معبود نہیں، تو نے مجھے پیدا کیا اور میں تیرا بندہ ہوں...", 1),
            ZikrItem("m4", "صبح کے اذکار", "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا", "میں اللہ کے رب ہونے پر، اسلام کے دین ہونے پر، اور محمد ﷺ کے نبی ہونے پر راضی ہوا (۳ مرتبہ)", 3),
            ZikrItem("m5", "آیت الکرسی", "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ...", "اللہ کے سوا کوئی معبود نہیں، وہ زندہ ہے سب کا تھامنے والا ہے، نہ اسے اونگھ آتی ہے نہ نیند...", 1),
            ZikrItem("m6", "حفاظت کے کلمات", "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ: عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ", "پاک ہے اللہ اپنی حمد کے ساتھ: اپنی مخلوق کی تعداد کے برابر، اپنی ذات کی رضا کے برابر، اپنے عرش کے وزن کے برابر...", 3)
        )
    }

    val eveningAzkar = remember {
        mutableStateListOf(
            ZikrItem("e1", "شام کے اذکار", "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ", "ہم نے شام کی اور اللہ کے سارے ملک نے شام کی، اور سب تعریف اللہ کے لیے ہے", 1),
            ZikrItem("e2", "شام کے اذکار", "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ", "میں اللہ کے کامل کلمات کی پناہ مانگتا ہوں اس کی تمام مخلوقات کے شر سے (شام کے وقت ۳ مرتبہ پڑھنے سے سانپ، بچھو یا زہر اثر نہیں کرتا)", 3),
            ZikrItem("e3", "شام کے اذکار", "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ", "اے اللہ! تیرے ہی حکم سے ہم نے شام کی اور تیرے ہی حکم سے صبح کی، اور تیرے ہی حکم سے ہم جیتے اور مرتے ہیں اور تیری طرف ہی لوٹنا ہے", 1),
            ZikrItem("e4", "شام کے اذکار", "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ", "اللہ کے نام سے جس کے نام کے ساتھ زمین و آسمان کی کوئی چیز نقصان نہیں پہنچا سکتی اور وہ خوب سننے اور جاننے والا ہے", 3),
            ZikrItem("e5", "سید الاستغفار", "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ...", "اے اللہ! تو ہی میرا رب ہے، تیرے سوا کوئی معبود نہیں...", 1)
        )
    }

    var tasbeehCount by remember { mutableStateOf(0) }
    var tasbeehTarget by remember { mutableStateOf(100) }
    var tasbeehZikr by remember { mutableStateOf("سُبْحَانَ اللَّهِ") }

    val categoriesList = listOf("سب", "کھانے پینے", "گھر و مسجد", "طہارت و عبادات", "سفر و مارکیٹ", "شفاء و پریشانی", "روزمرہ معمولات")

    val filteredDuas = remember(searchQuery, selectedCategoryFilter, allDuas) {
        allDuas.filter { dua ->
            val matchesCategory = (selectedCategoryFilter == "سب" || dua.category == selectedCategoryFilter)
            val q = searchQuery.trim().lowercase()
            val matchesSearch = q.isEmpty() ||
                    dua.titleUr.lowercase().contains(q) ||
                    dua.titleEn.lowercase().contains(q) ||
                    dua.urdu.lowercase().contains(q) ||
                    dua.english.lowercase().contains(q) ||
                    dua.arabic.contains(q)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF2563EB).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isUr) "مسنون دعائیں و روزمرہ اذکار" else "Masnoon Duas & Daily Azkar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = if (isUr) "حصن المسلم - مستند قرآنی و مسنون دعائیں" else "Fortress of the Muslim - Authentic Duas",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
            // Scrollable Tab Row for 4 options
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 12.dp
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isUr) "مسنون دعائیں (${allDuas.size})" else "Masnoon Duas (${allDuas.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isUr) "صبح کے اذکار" else "Morning")
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NightsStay, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isUr) "شام کے اذکار" else "Evening")
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isUr) "تسبیح کاؤنٹر" else "Tasbeeh")
                        }
                    }
                )
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: MASNOON DUAS
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search & Filters Header
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(if (isUr) "دعا تلاش کریں (کھانے، سفر، قرض، نیند...)" else "Search Duas (food, travel, debt, sleep...)")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dua_search_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Category filter chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categoriesList.forEach { cat ->
                                    FilterChip(
                                        selected = selectedCategoryFilter == cat,
                                        onClick = { selectedCategoryFilter = cat },
                                        label = { Text(if (cat == "سب") (if (isUr) "سب دعائیں" else "All") else cat) }
                                    )
                                }
                            }
                        }

                        if (filteredDuas.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.SearchOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (isUr) "کوئی دعا نہیں ملی!" else "No Duas found!",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isUr) "مختلف الفاظ لکھ کر تلاش کریں" else "Try searching with a different keyword",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(filteredDuas, key = { it.id }) { dua ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            // Top Header: Category & Reference badges
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color(0xFF2563EB).copy(alpha = 0.12f)
                                                ) {
                                                    Text(
                                                        text = dua.category,
                                                        color = Color(0xFF2563EB),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MaterialTheme.colorScheme.secondaryContainer
                                                ) {
                                                    Text(
                                                        text = dua.reference,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Dua Title
                                            Text(
                                                text = if (isUr) dua.titleUr else dua.titleEn,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Arabic Card Container with soft gradient
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = dua.arabic,
                                                    fontSize = 20.sp,
                                                    lineHeight = 36.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.End,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(14.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Urdu Translation
                                            Text(
                                                text = dua.urdu,
                                                style = MaterialTheme.typography.bodyMedium,
                                                lineHeight = 22.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = if (isUr) TextAlign.End else TextAlign.Start,
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            if (!isUr) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = dua.english,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }

                                            HorizontalDivider(
                                                modifier = Modifier.padding(vertical = 12.dp),
                                                color = MaterialTheme.colorScheme.outlineVariant
                                            )

                                            // Bottom Actions: Copy, Share, Recite Counter (+1)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    // Copy Button
                                                    OutlinedButton(
                                                        onClick = {
                                                            val copyText = "${dua.titleUr}\n\n${dua.arabic}\n\nترجمہ: ${dua.urdu}\n(${dua.reference})\n[ایپ: Rozana روزانہ]"
                                                            clipboardManager.setText(AnnotatedString(copyText))
                                                            Toast.makeText(context, if (isUr) "دعا کاپی ہو گئی!" else "Dua copied to clipboard!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        shape = RoundedCornerShape(10.dp),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                                    ) {
                                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(15.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(if (isUr) "کاپی" else "Copy", fontSize = 12.sp)
                                                    }

                                                    // Share Button
                                                    OutlinedButton(
                                                        onClick = {
                                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                                type = "text/plain"
                                                                putExtra(Intent.EXTRA_SUBJECT, dua.titleUr)
                                                                putExtra(Intent.EXTRA_TEXT, "${dua.titleUr}\n\n${dua.arabic}\n\nترجمہ: ${dua.urdu}\n\nحوالہ: ${dua.reference}\n\nروزانہ ایپ (Rozana) سے شیئر کی گئی")
                                                            }
                                                            context.startActivity(Intent.createChooser(shareIntent, "Share Dua via"))
                                                        },
                                                        shape = RoundedCornerShape(10.dp),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                                    ) {
                                                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(15.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(if (isUr) "شیئر" else "Share", fontSize = 12.sp)
                                                    }
                                                }

                                                // Recite Counter
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    if (dua.readCount > 0) {
                                                        Text(
                                                            text = "${dua.readCount} ${if (isUr) "بار پڑھا" else "read"}",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.padding(end = 8.dp)
                                                        )
                                                    }

                                                    Button(
                                                        onClick = {
                                                            val idx = allDuas.indexOfFirst { it.id == dua.id }
                                                            if (idx != -1) {
                                                                allDuas[idx] = allDuas[idx].copy(readCount = dua.readCount + 1)
                                                            }
                                                        },
                                                        shape = RoundedCornerShape(12.dp),
                                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                                    ) {
                                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(if (isUr) "پڑھا (+1)" else "+1", fontWeight = FontWeight.Bold)
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

                1, 2 -> {
                    // TAB 1 (Morning) or TAB 2 (Evening)
                    val currentList = if (selectedTab == 1) morningAzkar else eveningAzkar
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
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
                                        lineHeight = 32.sp,
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
                                                color = if (zikr.currentCount >= zikr.target) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                                            )
                                            if (zikr.currentCount >= zikr.target) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "✓ مکمل",
                                                    color = Color(0xFF10B981),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
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
                                                Text("+1", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: DIGITAL TASBEEH
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = tasbeehZikr,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                            )
                        }

                        // Presets
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "سُبْحَانَ اللَّهِ",
                                "الْحَمْدُ لِلَّهِ",
                                "اللَّهُ أَكْبَرُ",
                                "لَا إِلَهَ إِلَّا اللَّهُ",
                                "أَسْتَغْفِرُ اللَّهَ",
                                "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ"
                            ).forEach { zikrPreset ->
                                FilterChip(
                                    selected = tasbeehZikr == zikrPreset,
                                    onClick = { tasbeehZikr = zikrPreset },
                                    label = { Text(zikrPreset, fontSize = 13.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Target Selector Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isUr) "ہدف (Target):" else "Target:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            listOf(33, 100, 1000).forEach { t ->
                                FilterChip(
                                    selected = tasbeehTarget == t,
                                    onClick = { tasbeehTarget = t },
                                    label = { Text("$t") }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Large Digital Circle Button
                        FilledIconButton(
                            onClick = { tasbeehCount++ },
                            modifier = Modifier
                                .size(160.dp)
                                .testTag("tasbeeh_tap_button"),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = VioletBrand)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$tasbeehCount",
                                    color = Color.White,
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = if (isUr) "ٹیپ کریں (+1)" else "Tap (+1)",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp
                                )
                                if (tasbeehTarget > 0) {
                                    Text(
                                        text = "${if (isUr) "ہدف" else "Target"}: $tasbeehTarget",
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { tasbeehCount = 0 },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isUr) "ری سیٹ (0 کریں)" else "Reset")
                            }

                            if (tasbeehCount >= tasbeehTarget && tasbeehTarget > 0) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF10B981)
                                ) {
                                    Text(
                                        text = if (isUr) "ماشاءاللہ! ہدف مکمل ✓" else "Target Complete! ✓",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
