package com.kidsworldtv.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.util.Locale

private val Sky = Color(0xFFEAF7FF)
private val Navy = Color(0xFF17324D)
private val Coral = Color(0xFFFF8066)
private val Teal = Color(0xFF39BFA7)
private val Yellow = Color(0xFFFFD45C)

private data class Lesson(
    val title: String,
    val category: String,
    val age: String,
    val emoji: String,
    val description: String,
    val videoUrl: String = ""
)

private val lessons = listOf(
    Lesson("Meet the Alphabet: A to Z", "ABC & Reading", "Ages 3–6", "🔤",
        "Watch, say, and learn the letters from A to Z.",
        "android.resource://com.kidsworldtv.app/raw/alphabet_lesson"),
    Lesson("Counting from 1 to 10", "Numbers & Maths", "Ages 3–6", "🔢",
        "Learn to count from one to ten with friendly objects."),
    Lesson("Learn Colours", "Colours & Shapes", "Ages 2–5", "🌈",
        "Discover red, blue, yellow, green, and purple."),
    Lesson("Amazing Animals", "Nature & Science", "Ages 4–8", "🦁",
        "Meet animals and learn where they live."),
    Lesson("Our Solar System", "Little Scientists", "Ages 6–9", "🪐",
        "Take a gentle first trip through space."),
    Lesson("Shapes Hunt", "Colours & Shapes", "Ages 3–6", "🔺",
        "Find circles, squares, triangles, and more.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KidsWorldApp() }
    }
}

private fun translatedLessonTitle(title: String, language: String): String {
    if (language == "English") return title
    return when (title) {
        "Meet the Alphabet: A to Z" -> "Funda Izinhlamvu: A kuye ku-Z"
        "Counting from 1 to 10" -> "Bala kusukela ku-1 kuya ku-10"
        "Learn Colours" -> "Funda Imibala"
        "Amazing Animals" -> "Izilwane Ezimangalisayo"
        "Our Solar System" -> "Isimiso Sethu Sonozungezilanga"
        "Shapes Hunt" -> "Thola Izimo"
        else -> title
    }
}

private fun lessonNarration(lesson: Lesson, page: Int = 0): Pair<String, String> {
    return when (lesson.title) {
        "Meet the Alphabet: A to Z" -> Pair(
            "Welcome to the alphabet lesson! Let's learn the letters from A to Z together. A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y and Z. Excellent work!",
            "Siyakwamukela esifundweni sezinhlamvu! Masifunde izinhlamvu kusukela ku-A kuye ku-Z ndawonye. Wenze kahle!"
        )
        "Counting from 1 to 10" -> {
            val en = listOf(
                "Number one. One apple.", "Number two. Two little chicks.",
                "Number three. Three stars.", "Number four. Four butterflies.",
                "Number five. Five flowers.", "Number six. Six fish.",
                "Number seven. Seven balloons.", "Number eight. Eight strawberries.",
                "Number nine. Nine cars.", "Number ten. Ten bright stars. Well done!"
            )
            val zu = listOf(
                "Inombolo yokuqala. I-apula elilodwa.",
                "Inombolo yesibili. Amatshwane amabili.",
                "Inombolo yesithathu. Izinkanyezi ezintathu.",
                "Inombolo yesine. Izimvemvane ezine.",
                "Inombolo yesihlanu. Izimbali ezinhlanu.",
                "Inombolo yesithupha. Izinhlanzi eziyisithupha.",
                "Inombolo yesikhombisa. Amabhaluni ayisikhombisa.",
                "Inombolo yesishiyagalombili. Ama-strawberry ayisishiyagalombili.",
                "Inombolo yesishiyagalolunye. Izimoto eziyisishiyagalolunye.",
                "Inombolo yeshumi. Izinkanyezi eziyishumi. Wenze kahle!"
            )
            Pair(en[page.coerceIn(0, 9)], zu[page.coerceIn(0, 9)])
        }
        "Learn Colours" -> {
            val en = listOf(
                "Red. Red like an apple.", "Blue. Blue like the ocean.",
                "Yellow. Yellow like the sunshine.", "Green. Green like a leaf.",
                "Purple. Purple like grapes."
            )
            val zu = listOf(
                "Bomvu. Bomvu njenge-apula.",
                "Luhlaza okwesibhakabhaka. Njengolwandle.",
                "Phuzi. Njengelanga.", "Luhlaza. Njengeqabunga.",
                "Nsomi. Njengamagilebhisi."
            )
            Pair(en[page.coerceIn(0, 4)], zu[page.coerceIn(0, 4)])
        }
        "Amazing Animals" -> Pair(
            "Welcome! Let's discover amazing animals. Lions, elephants, giraffes and many more. Animals need food, water and shelter.",
            "Siyakwamukela! Masifunde ngezilwane ezimangalisayo. Amabhubesi, izindlovu, izindlulamithi nezinye eziningi. Izilwane zidinga ukudla, amanzi nendawo yokuhlala."
        )
        "Our Solar System" -> Pair(
            "Welcome to space! The Sun is a star. Eight planets travel around the Sun. Our home planet is Earth. Let's explore our solar system!",
            "Siyakwamukela emkhathini! ILanga liyinkanyezi. Kunamaplanethi ayisishiyagalombili azungeza iLanga. Umhlaba uyikhaya lethu. Masifunde ngesimiso sonozungezilanga!"
        )
        "Shapes Hunt" -> Pair(
            "Let's learn shapes! A circle is round. A square has four equal sides. A triangle has three sides. A rectangle has four sides. Well done!",
            "Masifunde izimo! Isiyingi siyindilinga. Isikwele sinezinhlangothi ezine ezilinganayo. Unxantathu unezinhlangothi ezintathu. Wenze kahle!"
        )
        else -> Pair(lesson.description, "Masifunde ndawonye!")
    }
}

@Composable
private fun LessonNarratorControls(englishText: String, isiZuluText: String) {
    val context = LocalContext.current
    var ready by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val tts = remember {
        TextToSpeech(context.applicationContext) { status ->
            ready = status == TextToSpeech.SUCCESS
        }
    }
    DisposableEffect(tts) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Choose your narrator / Khetha umxoxi", color = Navy, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Button(
            onClick = {
                if (!ready) {
                    message = "Narrator is loading. Please try again."
                } else {
                    val result = tts.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        message = "English voice is not available on this phone."
                    } else {
                        message = ""
                        tts.speak(englishText, TextToSpeech.QUEUE_FLUSH, null, "english_narrator")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Teal)
        ) { Text("🇬🇧 English Narrator 🔊") }

        Button(
            onClick = {
                if (!ready) {
                    message = "Narrator is loading. Please try again."
                } else {
                    val result = tts.setLanguage(Locale("zu", "ZA"))
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        message = "IsiZulu voice is not installed on this phone."
                    } else {
                        message = ""
                        tts.speak(isiZuluText, TextToSpeech.QUEUE_FLUSH, null, "isizulu_narrator")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Coral)
        ) { Text("🇿🇦 IsiZulu Narrator 🔊") }

        OutlinedButton(
            onClick = { tts.stop(); message = "" },
            modifier = Modifier.fillMaxWidth()
        ) { Text("■ Stop narration") }

        if (message.isNotBlank()) {
            Text(message, color = Coral, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun KidsWorldApp() {
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var search by rememberSaveable { mutableStateOf("") }
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }
    var showParentGate by remember { mutableStateOf(false) }
    var parentUnlocked by remember { mutableStateOf(false) }
    var language by rememberSaveable { mutableStateOf("English") }
    var section by rememberSaveable { mutableStateOf("") }

    val categories = listOf(
        "All", "ABC & Reading", "Numbers & Maths",
        "Colours & Shapes", "Nature & Science", "Little Scientists"
    )
    val visibleLessons = lessons.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
            (it.title.contains(search, true) || it.category.contains(search, true))
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Teal, secondary = Coral, background = Sky,
            surface = Color.White, onPrimary = Color.White,
            onBackground = Navy, onSurface = Navy
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = Sky) {
            when {
    section == "About" -> AboutScreen(language) { section = "" }
    section == "Dictionary" -> DictionaryScreen(language) { section = "" }
    section == "Story Time" -> StoryTimeScreen(language) { section = "" }
    section == "Speak & Pronounce" -> SpeakScreen(language) { section = "" }
                )
                showParentGate -> ParentGate(
                    onCancel = { showParentGate = false },
                    onUnlock = { parentUnlocked = true; showParentGate = false }
                )
                parentUnlocked -> ParentScreen { parentUnlocked = false }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 28.dp)
                    ) {
                        item {
                            Column(
                                Modifier.fillMaxWidth().background(Navy)
                                    .padding(horizontal = 20.dp, vertical = 22.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🌟", fontSize = 30.sp)
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("Kids World TV", fontSize = 25.sp,
                                            fontWeight = FontWeight.ExtraBold, color = Color.White)
                                        Text(
                                            if (language == "English") "Big fun. Bright minds."
                                            else "Ubumnandi obukhulu. Izingqondo ezikhanyayo.",
                                            color = Color(0xFFCBEAFF), fontSize = 14.sp
                                        )
                                    }
                                    IconButton(onClick = { showParentGate = true }) {
                                        Icon(Icons.Default.Lock, "Parent area", tint = Color.White)
                                    }
                                }
                                Spacer(Modifier.height(14.dp))
                                Text(
                                    if (language == "English") "Choose language" else "Khetha ulimi",
                                    color = Color.White, fontWeight = FontWeight.Bold
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("English", "IsiZulu").forEach { lang ->
                                        FilterChip(
                                            selected = language == lang,
                                            onClick = { language = lang },
                                            label = { Text(lang) }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(14.dp))
                                Image(
                                    painter = painterResource(R.drawable.kids_world_tv_cover),
                                    contentDescription = "Kids World TV cover",
                                    modifier = Modifier.fillMaxWidth().height(170.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    if (language == "English") "What shall we learn today?"
                                    else "Sizofundani namuhla?",
                                    color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = search,
                                    onValueChange = { search = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(if (language == "English") "Search lessons" else "Sesha izifundo")
                                    },
                                    leadingIcon = { Icon(Icons.Default.Search, null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    )
                                )
                            }
                        }

                        item {
                            Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                                Text(
                                    if (language == "English") "More ways to learn" else "Ezinye izindlela zokufunda",
                                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy
                                )
                                Spacer(Modifier.height(8.dp))
    listOf(
    "📖  My Dictionary" to "Dictionary",
    "🎬  Story Time" to "Story Time",
    "🗣️  Speak & Pronounce" to "Speak & Pronounce",
    "ℹ️  About & App Information" to "About"
).forEach { menuItem ->
                                    Card(
                                        Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                            .clickable { section = menuItem.second },
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Text(
                                            if (language == "IsiZulu") when (menuItem.second) {
                                                "Dictionary" -> "📖  Isichazamazwi"
                                                "Story Time" -> "🎬  Isikhathi Sezindaba"
                                                else -> "🗣️  Khuluma Nokubiza Amagama"
                                            } else menuItem.first,
                                            Modifier.padding(16.dp), color = Navy,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(Modifier.height(14.dp))
                                Text(
                                    if (language == "English") "Explore topics" else "Hlola izihloko",
                                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy
                                )
                                Spacer(Modifier.height(10.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    categories.chunked(2).forEach { pair ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            pair.forEach { category ->
                                                val emoji = when (category) {
                                                    "All" -> "✨"
                                                    "ABC & Reading" -> "🔤"
                                                    "Numbers & Maths" -> "🔢"
                                                    "Colours & Shapes" -> "🌈"
                                                    "Nature & Science" -> "🐾"
                                                    else -> "🚀"
                                                }
                                                val active = selectedCategory == category
                                                Surface(
                                                    Modifier.weight(1f).clickable { selectedCategory = category },
                                                    color = if (active) Teal else Color.White,
                                                    shape = RoundedCornerShape(14.dp),
                                                    shadowElevation = 1.dp
                                                ) {
                                                    Column(
                                                        Modifier.padding(12.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(emoji, fontSize = 24.sp)
                                                        Spacer(Modifier.height(4.dp))
                                                        Text(
                                                            if (language == "IsiZulu") when (category) {
                                                                "All" -> "Konke"
                                                                "ABC & Reading" -> "Izinhlamvu Nokufunda"
                                                                "Numbers & Maths" -> "Izinombolo Nezibalo"
                                                                "Colours & Shapes" -> "Imibala Nezimo"
                                                                "Nature & Science" -> "Imvelo Nesayensi"
                                                                "Little Scientists" -> "Ososayensi Abancane"
                                                                else -> category
                                                            } else category,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = if (active) Color.White else Navy
                                                        )
                                                    }
                                                }
                                            }
                                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                                        }
                                    }
                                }
                                Spacer(Modifier.height(20.dp))
                                Text(
                                    if (language == "English") "Learning videos" else "Amavidiyo okufunda",
                                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy
                                )
                                Text(
                                    if (language == "English") "Choose a lesson to get started."
                                    else "Khetha isifundo ukuze uqale.",
                                    fontSize = 14.sp, color = Color(0xFF547087)
                                )
                            }
                        }

                        if (visibleLessons.isEmpty()) {
                            item {
                                Text(
                                    if (language == "English") "No lessons found. Try another search."
                                    else "Azikho izifundo ezitholakele. Zama futhi.",
                                    Modifier.padding(20.dp), color = Navy
                                )
                            }
                        } else {
                            items(visibleLessons) { lesson ->
                                LessonCard(lesson, language) { selectedLesson = lesson }
                            }
                        }
                        item {
                            Text(
                                if (language == "English") "A safe space to learn and grow 💛"
                                else "Indawo ephephile yokufunda nokukhula 💛",
                                Modifier.fillMaxWidth().padding(20.dp),
                                color = Color(0xFF547087), fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonCard(lesson: Lesson, language: String, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(76.dp).background(
                    when (lesson.category) {
                        "ABC & Reading" -> Color(0xFFFFE4B8)
                        "Numbers & Maths" -> Color(0xFFD8F5EB)
                        "Colours & Shapes" -> Color(0xFFFFE0E7)
                        "Nature & Science" -> Color(0xFFE0EDFF)
                        else -> Color(0xFFE9E0FF)
                    }, RoundedCornerShape(16.dp)
                ),
                contentAlignment = Alignment.Center
            ) { Text(lesson.emoji, fontSize = 34.sp) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(translatedLessonTitle(lesson.title, language),
                    fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Navy)
                Spacer(Modifier.height(5.dp))
                Text(
                    if (language == "IsiZulu") when (lesson.category) {
                        "ABC & Reading" -> "Izinhlamvu Nokufunda"
                        "Numbers & Maths" -> "Izinombolo Nezibalo"
                        "Colours & Shapes" -> "Imibala Nezimo"
                        "Nature & Science" -> "Imvelo Nesayensi"
                        "Little Scientists" -> "Ososayensi Abancane"
                        else -> "Konke"
                    } else lesson.category,
                    fontSize = 13.sp, color = Color(0xFF547087)
                )
                Spacer(Modifier.height(5.dp))
                Text(lesson.age, fontSize = 12.sp, color = Color(0xFF547087))
            }
            Icon(Icons.Default.PlayArrow, "Open lesson", tint = Coral, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun LessonScreen(lesson: Lesson, language: String, onBack: () -> Unit) {
    val isCounting = lesson.title == "Counting from 1 to 10"
    val isColours = lesson.title == "Learn Colours"
if (isCounting) {
    CountingPracticeScreen(lesson, language, onBack)
    return
}

    if (isCounting || isColours) {
        val slides = if (isCounting) listOf(
            Triple("1", "🍎", if (language == "IsiZulu") "I-APULA ELILODWA" else "ONE apple"),
            Triple("2", "🐥🐥", if (language == "IsiZulu") "AMATSHWANE AMABILI" else "TWO little chicks"),
            Triple("3", "⭐ ⭐ ⭐", if (language == "IsiZulu") "IZINKANYEZI EZINTATHU" else "THREE stars"),
            Triple("4", "🦋 🦋 🦋 🦋", if (language == "IsiZulu") "IZIMVEMVANE EZINE" else "FOUR butterflies"),
            Triple("5", "🌼 🌼 🌼 🌼 🌼", if (language == "IsiZulu") "IZIMBALI EZINHLANU" else "FIVE flowers"),
            Triple("6", "🐠 🐠 🐠 🐠 🐠 🐠", if (language == "IsiZulu") "IZINHLANZI EZIYISITHUPHA" else "SIX fish"),
            Triple("7", "🎈 🎈 🎈 🎈 🎈 🎈 🎈", if (language == "IsiZulu") "AMABHALUNI AYISIKHOMBISA" else "SEVEN balloons"),
            Triple("8", "🍓 🍓 🍓 🍓 🍓 🍓 🍓 🍓", if (language == "IsiZulu") "AMASTRAWBERRY AYISISHIYAGALOMBILI" else "EIGHT strawberries"),
            Triple("9", "🚗 🚗 🚗 🚗 🚗 🚗 🚗 🚗 🚗", if (language == "IsiZulu") "IZIMOTO EZIYISISHIYAGALOLUNYE" else "NINE cars"),
            Triple("10", "🌟 🌟 🌟 🌟 🌟 🌟 🌟 🌟 🌟 🌟", if (language == "IsiZulu") "IZINKANYEZI EZISHUMI EZIKHAZIMULAYO" else "TEN bright stars")
        ) else listOf(
            Triple("RED", "🍎 ❤️ 🍓", if (language == "IsiZulu") "BOMVU NJENGE-APULA" else "Red like an apple"),
            Triple("BLUE", "🐳 💙 🫐", if (language == "IsiZulu") "LUHLAZA OKWESIBHAKABHAKA NJENGOLWANDLE" else "Blue like the ocean"),
            Triple("YELLOW", "☀️ 🌻 ⭐", if (language == "IsiZulu") "PHUZI NJENGELANGA" else "Yellow like the sunshine"),
            Triple("GREEN", "🐸 🍀 🥝", if (language == "IsiZulu") "LUHLAZA NJENGEQABUNGA" else "Green like a leaf"),
            Triple("PURPLE", "🍇 💜 🌂", if (language == "IsiZulu") "NSOMI NJENGAMAGILEBHISI" else "Purple like grapes")
        )

        var page by remember(lesson.title) { mutableIntStateOf(0) }
        val slide = slides[page]
        val narration = lessonNarration(lesson, page)
        val slideColor = if (isCounting) Color(0xFFFFF2CC) else when (slide.first) {
            "RED" -> Color(0xFFFFE0E0)
            "BLUE" -> Color(0xFFDCEEFF)
            "YELLOW" -> Color(0xFFFFF5C2)
            "GREEN" -> Color(0xFFE0F5DF)
            else -> Color(0xFFEEDFFF)
        }

        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
                Text("← Back to lessons", color = Navy)
            }
            Text(translatedLessonTitle(lesson.title, language),
                fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Navy)
            Spacer(Modifier.height(8.dp))
            LessonNarratorControls(narration.first, narration.second)
            Spacer(Modifier.height(8.dp))
            Card(
                Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = slideColor)
            ) {
                Column(
                    Modifier.fillMaxSize().padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(slide.first,
                        fontSize = if (isCounting) 76.sp else 46.sp,
                        fontWeight = FontWeight.ExtraBold, color = Navy)
                    Spacer(Modifier.height(18.dp))
                    Text(slide.second, fontSize = 35.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(18.dp))
                    Text(slide.third, fontSize = 23.sp, fontWeight = FontWeight.Bold,
                        color = Navy, textAlign = TextAlign.Center)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Slide ${page + 1} of ${slides.size}", color = Navy, fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { if (page > 0) page-- },
                    enabled = page > 0, modifier = Modifier.weight(1f)
                ) { Text("Previous") }
                Button(
                    onClick = { if (page < slides.lastIndex) page++ else page = 0 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal)
                ) { Text(if (page == slides.lastIndex) "Start again" else "Next") }
            }
        }
    } else {
        val narration = lessonNarration(lesson)
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            TextButton(onClick = onBack) { Text("← Back to lessons", color = Navy) }
            Spacer(Modifier.height(12.dp))
            if (lesson.videoUrl.isNotBlank()) {
                VideoPlayer(lesson.videoUrl)
            } else {
                Box(
                    Modifier.fillMaxWidth().height(220.dp)
                        .background(Navy, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(lesson.emoji, fontSize = 64.sp)
                        Spacer(Modifier.height(8.dp))
                        Icon(Icons.Default.PlayArrow, "Video placeholder",
                            tint = Yellow, modifier = Modifier.size(46.dp))
                        Text("VIDEO NOT CONNECTED YET", color = Color.White,
                            fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(translatedLessonTitle(lesson.title, language),
                fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Navy)
            Text(lesson.category + " • " + lesson.age,
                color = Color(0xFF547087), fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            LessonNarratorControls(narration.first, narration.second)
            Spacer(Modifier.height(12.dp))
            Text(
                if (language == "IsiZulu") when (lesson.title) {
                    "Meet the Alphabet: A to Z" -> "Buka, yisho futhi ufunde izinhlamvu kusukela ku-A kuye ku-Z."
                    "Amazing Animals" -> "Hlangana nezilwane ufunde ngezindawo ezihlala kuzo."
                    "Our Solar System" -> "Thatha uhambo olufushane lokufunda ngesimiso sonozungezilanga."
                    "Shapes Hunt" -> "Thola imibuthano, izikwele, onxantathu nezinye izimo."
                    else -> lesson.description
                } else lesson.description,
                fontSize = 17.sp, color = Navy
            )
            Spacer(Modifier.height(16.dp))
            Text(
                if (lesson.videoUrl.isBlank())
                    "Video content will be added in a future update."
                else
                    "If the video does not play, check that the video file is included in the app.",
                color = Color(0xFF547087), fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CountingPracticeScreen(
    lesson: Lesson,
    language: String,
    onBack: () -> Unit
) {
    val modes = listOf(
        "1–10", "11–20", "21–50", "51–100",
        "2s", "5s", "10s", "Addition"
    )

    var selectedMode by remember(lesson.title) {
        mutableStateOf("1–10")
    }
    var page by remember(selectedMode) {
        mutableIntStateOf(0)
    }

    val additions = listOf(
        Triple("1 + 1", "🍎 + 🍎", "2"),
        Triple("2 + 1", "🐥 🐥 + 🐥", "3"),
        Triple("2 + 2", "⭐ ⭐ + ⭐ ⭐", "4"),
        Triple("3 + 2", "🍓 🍓 🍓 + 🍓 🍓", "5"),
        Triple("4 + 2", "🦋 🦋 🦋 🦋 + 🦋 🦋", "6"),
        Triple("5 + 2", "🌼 🌼 🌼 🌼 🌼 + 🌼 🌼", "7"),
        Triple("4 + 4", "🎈 🎈 🎈 🎈 + 🎈 🎈 🎈 🎈", "8"),
        Triple("5 + 4", "🚗 🚗 🚗 🚗 🚗 + 🚗 🚗 🚗 🚗", "9"),
        Triple("5 + 5", "🌟 🌟 🌟 🌟 🌟 + 🌟 🌟 🌟 🌟 🌟", "10"),
        Triple("10 + 5", "🍊 10 + 🍊 5", "15")
    )

    val slides: List<Triple<String, String, String>> =
        when (selectedMode) {
            "1–10" -> listOf(
                Triple("1", "🍎", "ONE apple"),
                Triple("2", "🐥 🐥", "TWO little chicks"),
                Triple("3", "⭐ ⭐ ⭐", "THREE stars"),
                Triple("4", "🦋 🦋 🦋 🦋", "FOUR butterflies"),
                Triple("5", "🌼 🌼 🌼 🌼 🌼", "FIVE flowers"),
                Triple("6", "🐠 🐠 🐠 🐠 🐠 🐠", "SIX fish"),
                Triple("7", "🎈 🎈 🎈 🎈 🎈 🎈 🎈", "SEVEN balloons"),
                Triple("8", "🍓 🍓 🍓 🍓 🍓 🍓 🍓 🍓", "EIGHT strawberries"),
                Triple("9", "🚗 🚗 🚗 🚗 🚗 🚗 🚗 🚗 🚗", "NINE cars"),
                Triple("10", "🌟 🌟 🌟 🌟 🌟 🌟 🌟 🌟 🌟 🌟", "TEN bright stars")
            ).map {
                Triple(
                    it.first,
                    it.second,
                    if (language == "IsiZulu") "Bala nami!" else it.third
                )
            }

            "11–20" -> (11..20).map {
                Triple(
                    it.toString(),
                    "⭐ × $it",
                    if (language == "IsiZulu") "Bala nami!" else "Count with me!"
                )
            }

            "21–50" -> (21..50).map {
                Triple(
                    it.toString(),
                    "🌟 × $it",
                    if (language == "IsiZulu") "Bala nami!" else "Count with me!"
                )
            }

            "51–100" -> (51..100).map {
                Triple(
                    it.toString(),
                    "🎈 × $it",
                    if (language == "IsiZulu") "Bala nami!" else "Count with me!"
                )
            }

            "2s" -> (2..20 step 2).map {
                Triple(it.toString(), "⭐ × $it", "Count by twos!")
            }

            "5s" -> (5..50 step 5).map {
                Triple(it.toString(), "🌼 × $it", "Count by fives!")
            }

            "10s" -> (10..100 step 10).map {
                Triple(it.toString(), "🎈 × $it", "Count by tens!")
            }

            else -> additions.map {
                Triple(
                    it.first,
                    it.second,
                    if (language == "IsiZulu")
                        "Impendulo: ${it.third}"
                    else
                        "Answer: ${it.third}"
                )
            }
        }

    val slide = slides[page]

    val narration = when {
        selectedMode == "1–10" -> lessonNarration(lesson, page)

        selectedMode == "Addition" -> Pair(
            "${slide.first}. The answer is ${slide.third}.",
            "${slide.first}. Impendulo ngu-${slide.third}."
        )

        else -> Pair(
            "Number ${slide.first}. Count with me!",
            "Inombolo ${slide.first}. Bala nami!"
        )
    }

    Column(
        Modifier.fillMaxSize().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text(
                if (language == "English") "← Back to lessons"
                else "← Buyela ezifundweni",
                color = Navy
            )
        }

        Text(
            if (language == "English") "Counting & Maths"
            else "Ukubala Nezibalo",
            fontSize = 23.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Navy
        )

        Spacer(Modifier.height(8.dp))

        modes.chunked(4).forEach { group ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                group.forEach { mode ->
                    val label = when (mode) {
                        "2s" -> if (language == "English") "By 2s" else "Ngo-2"
                        "5s" -> if (language == "English") "By 5s" else "Ngo-5"
                        "10s" -> if (language == "English") "By 10s" else "Ngo-10"
                        "Addition" -> if (language == "English") "Add" else "Hlanganisa"
                        else -> mode
                    }

                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { selectedMode = mode },
                        modifier = Modifier.weight(1f),
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        LessonNarratorControls(narration.first, narration.second)

        Spacer(Modifier.height(8.dp))

        Card(
            Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF2CC)
            )
        ) {
            Column(
                Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    slide.first,
                    fontSize = 62.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Navy
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    slide.second,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    slide.third,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "Slide ${page + 1} of ${slides.size}",
            color = Navy,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { if (page > 0) page-- },
                enabled = page > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text("Previous")
            }

            Button(
                onClick = {
                    if (page < slides.lastIndex) page++
                    else page = 0
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal
                )
            ) {
                Text(
                    if (page == slides.lastIndex) "Start again"
                    else "Next"
                )
            }
        }
    }
}

@Composable
private fun DictionaryScreen(language: String, onBack: () -> Unit) {
    data class Word(
        val category: String,
        val english: String,
        val meaning: String,
        val isiZulu: String
    )

    val words = listOf(
        // ANIMALS (17)
        Word("Everyday", "Apple", "A round fruit that grows on a tree.", "I-apula"),
        Word("Animals", "Cat", "A small animal that says meow.", "Ikati"),
        Word("Animals", "Dog", "A friendly animal often kept as a pet.", "Inja"),
        Word("Animals", "Lion", "A large wild cat called the king of animals.", "Ibhubesi"),
        Word("Animals", "Elephant", "A very large animal with a long trunk.", "Indlovu"),
        Word("Animals", "Bird", "An animal with feathers and wings.", "Inyoni"),
        Word("Animals", "Fish", "An animal that lives in water.", "Inhlanzi"),
        Word("Animals", "Cow", "A farm animal that gives us milk.", "Inkomo"),
        Word("Animals", "Horse", "An animal people can ride.", "Ihhashi"),
        Word("Animals", "Sheep", "A farm animal covered in wool.", "Imvu"),
        Word("Animals", "Goat", "A farm animal that likes to eat leaves.", "Imbuzi"),
        Word("Animals", "Monkey", "An animal that climbs trees.", "Inkawu"),
        Word("Animals", "Rabbit", "A small animal with long ears.", "Unogwaja"),
        Word("Animals", "Giraffe", "A tall animal with a very long neck.", "Indlulamithi"),
        Word("Animals", "Snake", "A long animal with no legs.", "Inyoka"),
        Word("Animals", "Duck", "A bird that swims in water.", "Idada"),
        Word("Animals", "Butterfly", "A colourful insect with beautiful wings.", "Uvemvane"),

        // HOME (17)
        Word("Home", "House", "A building where people live.", "Indlu"),
        Word("Home", "Door", "Something we open to enter a room.", "Umnyango"),
        Word("Home", "Window", "An opening that lets light into a room.", "Iwindi"),
        Word("Home", "Chair", "Something we sit on.", "Isihlalo"),
        Word("Home", "Table", "Furniture with a flat top.", "Itafula"),
        Word("Home", "Bed", "Something we sleep on.", "Umbhede"),
        Word("Home", "Kitchen", "A room where food is prepared.", "Ikhishi"),
        Word("Home", "Room", "A space inside a house.", "Igumbi"),
        Word("Home", "Roof", "The top covering of a house.", "Uphahla"),
        Word("Home", "Floor", "The surface we walk on indoors.", "Iphansi"),
        Word("Home", "Wall", "A structure that separates rooms.", "Udonga"),
        Word("Home", "Cup", "A small container used for drinking.", "Inkomishi"),
        Word("Home", "Plate", "A dish used for serving food.", "Ipuleti"),
        Word("Home", "Spoon", "A tool used for eating or stirring.", "Isipuni"),
        Word("Home", "Broom", "A tool used to sweep the floor.", "Umshanelo"),
        Word("Home", "Blanket", "A warm covering used on a bed.", "Ingubo"),
        Word("Home", "Light", "Something that helps us see.", "Ukukhanya"),

        // FAMILY (16)
        Word("Family", "Mother", "A female parent.", "Umama"),
        Word("Family", "Father", "A male parent.", "Ubaba"),
        Word("Family", "Baby", "A very young child.", "Usana"),
        Word("Family", "Child", "A young boy or girl.", "Ingane"),
        Word("Family", "Family", "People who are related to one another.", "Umndeni"),
        Word("Family", "Brother", "A boy or man with the same parent as you.", "Umfowethu"),
        Word("Family", "Sister", "A girl or woman with the same parent as you.", "Udadewethu"),
        Word("Family", "Grandmother", "The mother of your mother or father.", "Ugogo"),
        Word("Family", "Grandfather", "The father of your mother or father.", "Umkhulu"),
        Word("Family", "Friend", "Someone you like and enjoy spending time with.", "Umngane"),
        Word("Family", "Happy", "Feeling glad or pleased.", "Ukujabula"),
        Word("Family", "Love", "A strong feeling of care and affection.", "Uthando"),
        Word("Family", "Boy", "A male child.", "Umfana"),
        Word("Family", "Girl", "A female child.", "Intombazane"),
        Word("Family", "People", "More than one person.", "Abantu"),
        Word("Family", "Baby sister", "A younger female sibling.", "Udadewethu omncane"),

        // NATURE (17)
        Word("Nature", "Sun", "The star that gives Earth light and warmth.", "Ilanga"),
        Word("Nature", "Moon", "The object that shines in the night sky.", "Inyanga"),
        Word("Nature", "Star", "A bright object in the night sky.", "Inkanyezi"),
        Word("Nature", "Rain", "Water that falls from clouds.", "Imvula"),
        Word("Nature", "Tree", "A tall plant with a trunk and branches.", "Isihlahla"),
        Word("Nature", "Flower", "The colourful part of some plants.", "Imbali"),
        Word("Nature", "Water", "The clear liquid we drink.", "Amanzi"),
        Word("Nature", "River", "A natural stream of flowing water.", "Umfula"),
        Word("Nature", "Sea", "A large area of salty water.", "Ulwandle"),
        Word("Nature", "Mountain", "A very high area of land.", "Intaba"),
        Word("Nature", "Cloud", "A white or grey shape in the sky.", "Ifu"),
        Word("Nature", "Grass", "The green plants that cover the ground.", "Utshani"),
        Word("Nature", "Leaf", "The flat green part of a plant.", "Iqabunga"),
        Word("Nature", "Earth", "The planet where we live.", "Umhlaba"),
        Word("Nature", "Wind", "Moving air that we can feel.", "Umoya"),
        Word("Nature", "Sky", "The space above the Earth.", "Isibhakabhaka"),
        Word("Nature", "Fire", "Heat and light produced by burning.", "Umlilo"),

        // EVERYDAY (17)
        Word("Everyday", "Book", "Pages with writing or pictures.", "Incwadi"),
        Word("Everyday", "School", "A place where children learn.", "Isikole"),
        Word("Everyday", "Food", "Things we eat to stay healthy.", "Ukudla"),
        Word("Everyday", "Milk", "A white drink often from cows.", "Ubisi"),
        Word("Everyday", "Bread", "A food made from flour.", "Isinkwa"),
        Word("Everyday", "Car", "A vehicle with four wheels.", "Imoto"),
        Word("Everyday", "Bus", "A large vehicle that carries passengers.", "Ibhasi"),
        Word("Everyday", "Pencil", "A tool used for writing and drawing.", "Ipensela"),
        Word("Everyday", "Pen", "A tool used for writing with ink.", "Ipeni"),
        Word("Everyday", "Bag", "Something used to carry belongings.", "Isikhwama"),
        Word("Everyday", "Shoes", "Things we wear on our feet.", "Izicathulo"),
        Word("Everyday", "Clothes", "Things we wear on our bodies.", "Izingubo"),
        Word("Everyday", "Ball", "A round object used for playing.", "Ibhola"),
        Word("Everyday", "Phone", "A device used to communicate.", "Ucingo"),
        Word("Everyday", "Clock", "Something that shows the time.", "Iwashi"),
        Word("Everyday", "Money", "Something used to buy things.", "Imali"),
        Word("Everyday", "Market", "A place where people buy and sell things.", "Imakethe"),

        // COLOURS & OBJECTS (16)
        Word("Colours & Objects", "Red", "The colour of many apples.", "Bomvu"),
        Word("Colours & Objects", "Blue", "The colour of a clear sky.", "Luhlaza okwesibhakabhaka"),
        Word("Colours & Objects", "Green", "The colour of fresh grass.", "Luhlaza"),
        Word("Colours & Objects", "Yellow", "The colour of sunshine.", "Phuzi"),
        Word("Colours & Objects", "Purple", "A colour like some grapes.", "Nsomi"),
        Word("Colours & Objects", "Black", "A very dark colour.", "Mnyama"),
        Word("Colours & Objects", "White", "The colour of clean snow.", "Mhlophe"),
        Word("Colours & Objects", "Orange", "A colour like a ripe orange fruit.", "Owolintshi"),
        Word("Colours & Objects", "Pink", "A light colour like some flowers.", "Obomvana"),
        Word("Colours & Objects", "Brown", "A colour like tree bark.", "Nsundu"),
        Word("Colours & Objects", "Circle", "A round shape with no corners.", "Indilinga"),
        Word("Colours & Objects", "Square", "A shape with four equal sides.", "Isikwele"),
        Word("Colours & Objects", "Triangle", "A shape with three sides.", "Unxantathu"),
        Word("Colours & Objects", "Rectangle", "A shape with four sides.", "Unxande"),
        Word("Colours & Objects", "Star", "A shape often drawn with five points.", "Inkanyezi"),
        Word("Colours & Objects", "Heart", "A shape often used to show love.", "Inhliziyo")
    )

    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var ttsReady by remember { mutableStateOf(false) }
    var voiceMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val tts = remember {
        TextToSpeech(context.applicationContext) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
    }

    DisposableEffect(tts) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val categories = listOf(
        "All", "Animals", "Home", "Family",
        "Nature", "Everyday", "Colours & Objects"
    )

    val filteredWords = words.filter { word ->
        (selectedCategory == "All" || word.category == selectedCategory) &&
        (word.english.contains(query, true) ||
         word.isiZulu.contains(query, true) ||
         word.meaning.contains(query, true))
    }

    fun speakWord(word: String, isiZulu: Boolean) {
        if (!ttsReady) {
            voiceMessage = "Voice is loading. Please try again."
            return
        }

        val locale = if (isiZulu) Locale("zu", "ZA") else Locale.US
        val result = tts.setLanguage(locale)

        if (result == TextToSpeech.LANG_MISSING_DATA ||
            result == TextToSpeech.LANG_NOT_SUPPORTED) {
            voiceMessage = if (isiZulu)
                "IsiZulu voice is not available on this phone."
            else
                "English voice is not available on this phone."
        } else {
            voiceMessage = ""
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "dictionary_word")
        }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back", color = Navy)
        }

        Text(
            if (language == "English") "📖 My English Dictionary"
            else "📖 Isichazamazwi sesiNgisi",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Navy
        )

        Text(
            if (language == "English")
                "Learn 100 English words with IsiZulu translations"
            else
                "Funda amagama esiNgisi ayi-100 nezihumusho zesiZulu",
            color = Color(0xFF547087),
            fontSize = 13.sp
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search English or IsiZulu / Sesha") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Choose a category / Khetha isigaba",
            color = Navy,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        categories.chunked(2).forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                pair.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                if (language == "IsiZulu") when (category) {
                                    "All" -> "Konke"
                                    "Animals" -> "Izilwane"
                                    "Home" -> "Ikhaya"
                                    "Family" -> "Umndeni"
                                    "Nature" -> "Imvelo"
                                    "Everyday" -> "Nsuku zonke"
                                    else -> "Imibala Nezinto"
                                } else category,
                                fontSize = 11.sp
                            )
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            "${filteredWords.size} words found / Amagama atholakele",
            color = Teal,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        if (voiceMessage.isNotBlank()) {
            Text(voiceMessage, color = Coral, fontSize = 12.sp)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(filteredWords) { word ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            word.english,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Teal
                        )

                        Text(
                            word.meaning,
                            color = Navy,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            "IsiZulu: ${word.isiZulu}",
                            color = Color(0xFF547087),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    speakWord(word.english, false)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Teal
                                )
                            ) {
                                Text("🇬🇧 English 🔊", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    speakWord(word.isiZulu, true)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Coral
                                )
                            ) {
                                Text("🇿🇦 IsiZulu 🔊", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryTimeScreen(language: String, onBack: () -> Unit) {
    data class Story(
        val englishTitle: String,
        val isiZuluTitle: String,
        val emoji: String,
        val englishPages: List<String>,
        val isiZuluPages: List<String>
    )

    val stories = listOf(
        Story(
            "The Helpful Little Elephant",
            "Indlovu Encane Esizayo",
            "🐘",
            listOf(
                "Once upon a time, a little elephant named Ellie lived in a beautiful forest.",
                "One morning, Ellie saw a small bird whose nest had fallen from a tree.",
                "Ellie gently lifted the nest with her trunk and helped put it back. The bird was very happy.",
                "Ellie learned that helping others is a wonderful thing. Everyone thanked their kind friend."
            ),
            listOf(
                "Kwesukasukela, indlovu encane okuthiwa u-Ellie yayihlala ehlathini elihle.",
                "Ngolunye usuku ekuseni, u-Ellie wabona inyoni encane enesidleke esiwele phansi.",
                "U-Ellie wasebenzisa umboko wakhe ngobumnene waphakamisa isidleke wasibuyisela esihlahleni. Inyoni yajabula kakhulu.",
                "U-Ellie wafunda ukuthi ukusiza abanye kuyinto enhle. Bonke babonga umngane wabo onomusa."
            )
        ),
        Story(
            "The Lion Who Learned to Share",
            "Ibhubesi Elafunda Ukwabelana",
            "🦁",
            listOf(
                "Leo the young lion had many delicious fruits, but he never shared them.",
                "One day, his friends came to visit. They were hungry and asked if they could have some fruit.",
                "Leo thought about it and decided to share. They all enjoyed the fruit together.",
                "Leo discovered that sharing makes friendship stronger and brings happiness."
            ),
            listOf(
                "ULeo, ibhubesi elincane, wayenezithelo eziningi ezimnandi kodwa wayengafuni ukwabelana.",
                "Ngelinye ilanga, abangane bakhe bamvakashela. Babelambile bacela izithelo.",
                "ULeo wacabanga wabe esenquma ukwabelana nabo. Bonke badla izithelo ndawonye.",
                "ULeo wafunda ukuthi ukwabelana kuqinisa ubungane futhi kuletha injabulo."
            )
        ),
        Story(
            "The Clever Hare",
            "Unogwaja Ohlakaniphile",
            "🐇",
            listOf(
                "A clever little hare lived near a big river.",
                "One day, the river became too deep for the hare to cross.",
                "The hare looked around and found a safe path across some large stones.",
                "The hare crossed carefully and learned to think before acting."
            ),
            listOf(
                "Unogwaja omncane ohlakaniphile wayehlala eduze komfula omkhulu.",
                "Ngelinye ilanga, amanzi omfula ajula kakhulu kangangokuba wayengakwazi ukuwela.",
                "Unogwaja wabheka nxazonke wathola indlela ephephile yokuwela ngamatshe amakhulu.",
                "Unogwaja wawela ngokucophelela wafunda ukuthi kubalulekile ukucabanga ngaphambi kokwenza."
            )
        ),
        Story(
            "A Rainy Day Adventure",
            "Uhambo Losuku Lwemvula",
            "🌧️",
            listOf(
                "Thandi woke up and heard the rain falling outside.",
                "She put on her raincoat and watched the little drops dance on the ground.",
                "After the rain, she saw a beautiful rainbow in the sky.",
                "Thandi learned that rainy days can bring wonderful surprises."
            ),
            listOf(
                "UThandi wavuka wezwa imvula ina ngaphandle.",
                "Wagqoka ijazi lakhe lemvula wabuka amathonsi amancane edansa phansi.",
                "Ngemva kwemvula, wabona uthingo oluhle esibhakabhakeni.",
                "UThandi wafunda ukuthi izinsuku zemvula zingaletha izinto ezimangalisayo."
            )
        ),
        Story(
            "The Little Tree Planters",
            "Izingane Ezatshala Izihlahla",
            "🌳",
            listOf(
                "A group of children wanted to make their playground greener.",
                "They collected small trees and prepared the soil together.",
                "They planted the trees, watered them and promised to care for them.",
                "As the trees grew, the children learned to protect and love nature."
            ),
            listOf(
                "Iqembu lezingane lalifuna ukwenza indawo yalo yokudlala ibe luhlaza.",
                "Baqoqa izihlahla ezincane balungisa nomhlabathi ndawonye.",
                "Batshala izihlahla, bazinisela futhi bathembisa ukuzinakekela.",
                "Njengoba izihlahla zikhula, izingane zafunda ukuvikela nokuthanda imvelo."
            )
        ),
        Story(
            "The Little Firefighter",
            "Umcimi-mlilo Omncane",
            "🚒",
            listOf(
                "Sam loved learning about firefighters and how they help people.",
                "One day, Sam saw a little puppy trapped behind a garden gate.",
                "Sam called a grown-up for help. Together, they safely helped the puppy.",
                "Sam learned that being brave also means asking a trusted adult for help."
            ),
            listOf(
                "USam wayethanda ukufunda ngabacimi-mlilo nangendlela abasiza ngayo abantu.",
                "Ngelinye ilanga, uSam wabona umdlwane omncane uvaleleke ngemuva kwesango lengadi.",
                "USam wabiza umuntu omdala ukuba amsize. Base besiza umdlwane ngokuphepha.",
                "USam wafunda ukuthi ukuba nesibindi kusho nokucela usizo kumuntu omdala omethembayo."
            )
        )
    )

    var selectedStory by remember { mutableIntStateOf(-1) }
    var page by remember { mutableIntStateOf(0) }
    var ttsReady by remember { mutableStateOf(false) }
    var voiceMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val tts = remember {
        TextToSpeech(context.applicationContext) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
    }

    DisposableEffect(tts) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp)
    ) {
        TextButton(
            onClick = {
                if (selectedStory >= 0) {
                    tts.stop()
                    selectedStory = -1
                    page = 0
                    voiceMessage = ""
                } else {
                    onBack()
                }
            }
        ) {
            Text(
                if (selectedStory >= 0) "← Back to stories"
                else "← Back",
                color = Navy
            )
        }

        Text(
            if (language == "English") "📖 Story Time"
            else "📖 Isikhathi Sezindaba",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Navy
        )

        Text(
            if (language == "English")
                "Six short stories about kindness, friendship and learning."
            else
                "Izindaba eziyisithupha ezimfushane ezifundisa umusa, ubungane nokufunda.",
            color = Color(0xFF547087),
            fontSize = 13.sp
        )

        Spacer(Modifier.height(12.dp))

        if (selectedStory == -1) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(stories) { story ->
                    val storyIndex = stories.indexOf(story)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth()
                                .clickable {
                                    selectedStory = storyIndex
                                    page = 0
                                    voiceMessage = ""
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(story.emoji, fontSize = 38.sp)
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    if (language == "English")
                                        story.englishTitle
                                    else
                                        story.isiZuluTitle,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy
                                )
                                Text(
                                    if (language == "English")
                                        "Read and listen to this story"
                                    else
                                        "Funda futhi ulalele le ndaba",
                                    fontSize = 12.sp,
                                    color = Color(0xFF547087)
                                )
                            }
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Open story",
                                tint = Coral,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        } else {
            val story = stories[selectedStory]
            val storyPages = if (language == "English")
                story.englishPages else story.isiZuluPages

            Text(
                story.emoji,
                fontSize = 48.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                if (language == "English")
                    story.englishTitle else story.isiZuluTitle,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Page ${page + 1} of ${storyPages.size}",
                        color = Teal,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        storyPages[page],
                        fontSize = 21.sp,
                        lineHeight = 30.sp,
                        color = Navy,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (!ttsReady) {
                        voiceMessage = "Voice is loading. Please try again."
                    } else {
                        val locale = if (language == "English")
                            Locale.US else Locale("zu", "ZA")
                        val result = tts.setLanguage(locale)

                        if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED
                        ) {
                            voiceMessage =
                                if (language == "English")
                                    "English voice is not available on this phone."
                                else
                                    "IsiZulu voice is not installed on this phone."
                        } else {
                            voiceMessage = ""
                            tts.speak(
                                storyPages[page],
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "story_page"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal
                )
            ) {
                Text(
                    if (language == "English")
                        "🔊 Listen to this page"
                    else
                        "🔊 Lalela leli khasi"
                )
            }

            OutlinedButton(
                onClick = {
                    tts.stop()
                    voiceMessage = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("■ Stop narration")
            }

            if (voiceMessage.isNotBlank()) {
                Text(
                    voiceMessage,
                    color = Coral,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (page > 0) {
                            tts.stop()
                            page--
                        }
                    },
                    enabled = page > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Previous")
                }

                Button(
                    onClick = {
                        tts.stop()
                        if (page < storyPages.lastIndex) {
                            page++
                        } else {
                            page = 0
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal
                    )
                ) {
                    Text(
                        if (page == storyPages.lastIndex)
                            "Read again"
                        else
                            "Next page"
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeakScreen(language: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val locale = if (language == "IsiZulu") Locale("zu", "ZA") else Locale.US
    val examples = if (language == "IsiZulu")
        listOf("Sawubona", "Ngiyabonga", "Unjani?")
    else listOf("Hello", "Thank you", "How are you?")

    var index by remember(language) { mutableIntStateOf(0) }
    var heard by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var ttsReady by remember { mutableStateOf(false) }

    val tts = remember {
        TextToSpeech(context.applicationContext) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
    }
    DisposableEffect(tts) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spoken = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull().orEmpty()
        heard = spoken
        feedback = if (spoken.isNotBlank()) "Good try! / Wenze kahle!"
        else "Please try again. / Zama futhi."
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val intent = makeSpeechIntent(language, examples[index])
            if (intent != null) speechLauncher.launch(intent)
            else feedback = "Speech recognition is not available on this phone."
        } else {
            feedback = "Microphone permission is needed for speaking practice."
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Text("← Back", color = Navy)
        }
        Text(if (language == "English") "Speak & Pronounce" else "Khuluma Nokubiza",
            fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy)
        Spacer(Modifier.height(28.dp))
        Text(if (language == "English") "Listen, then say this phrase"
            else "Lalela bese usho la magama", color = Color(0xFF547087))
        Text(examples[index], fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = Teal)
        Button(
            onClick = {
                if (ttsReady) {
                    val result = tts.setLanguage(locale)
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        feedback = "This language voice is not installed on this phone."
                    } else {
                        feedback = ""
                        tts.speak(examples[index], TextToSpeech.QUEUE_FLUSH, null, "kids_word")
                    }
                } else feedback = "Voice is loading. Please try again."
            },
            colors = ButtonDefaults.buttonColors(containerColor = Teal)
        ) { Text("🔊 Listen") }

        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
                val intent = makeSpeechIntent(language, examples[index])
                if (intent != null) speechLauncher.launch(intent)
                else feedback = "Speech recognition is not available on this phone."
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }) { Text("🎤 My turn – speak") }

        if (heard.isNotBlank()) Text("I heard: $heard", color = Navy)
        if (feedback.isNotBlank()) Text(feedback, color = Teal, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(18.dp))
        OutlinedButton(onClick = {
            index = (index + 1) % examples.size
            heard = ""
            feedback = ""
        }) { Text("Next phrase") }

        Text(
            "Speech recognition depends on your phone's language services and may not judge pronunciation perfectly. Adult guidance is recommended.",
            color = Color(0xFF547087), fontSize = 12.sp
        )
    }
}

private fun makeSpeechIntent(language: String, phrase: String): Intent? {
    return try {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (language == "IsiZulu") "zu-ZA" else "en-US")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: $phrase")
        }
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun ParentGate(onCancel: () -> Unit, onUnlock: () -> Unit) {
    var answer by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center) {
        Text("🔒 Parent area", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = Navy)
        Spacer(Modifier.height(8.dp))
        Text("For the prototype, answer this simple check: what is 4 + 3?", color = Navy)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = answer, onValueChange = { answer = it },
            label = { Text("Answer") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { if (answer.trim() == "7") onUnlock() },
            colors = ButtonDefaults.buttonColors(containerColor = Teal)
        ) { Text("Continue") }
        TextButton(onClick = onCancel) { Text("Cancel") }
        Text("Prototype only: replace this with a stronger parent verification flow before release.",
            color = Color(0xFF547087), fontSize = 12.sp)
    }
}

@Composable
private fun ParentScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Back", color = Navy) }
        Text("Parent dashboard", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = Navy)
        Spacer(Modifier.height(12.dp))
        Text("Content and safety settings", fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold, color = Navy)
        Spacer(Modifier.height(8.dp))
        Text("• Review lessons before publishing", color = Navy)
        Text("• Add licensed educational videos", color = Navy)
        Text("• Configure privacy and screen-time controls", color = Navy)
        Text("• Integrate only ads suitable for child-directed apps", color = Navy)
        Spacer(Modifier.height(18.dp))
        Text("Advertising is not enabled in this starter build.",
            color = Color(0xFF547087), fontSize = 14.sp)
    }
}

@Composable
private fun VideoPlayer(videoUrl: String) {
    val context = LocalContext.current
    val player = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = false
        }
    }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    AndroidView(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        factory = { viewContext: Context ->
            PlayerView(viewContext).apply {
                this.player = player
                useController = true
                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
            }
        },
        update = { view -> view.player = player }
    )
}
