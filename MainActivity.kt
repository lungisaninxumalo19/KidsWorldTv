package com.kidsworldtv.app

import android.os.Bundle
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.SpeechRecognizer
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import java.util.Locale
import android.content.Context
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.DisposableEffect
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Sky = Color(0xFFEAF7FF)
private val Navy = Color(0xFF17324D)
private val Coral = Color(0xFFFF8066)
private val Teal = Color(0xFF39BFA7)
private val Yellow = Color(0xFFFFD45C)

data class Lesson(val title: String, val category: String, val age: String, val emoji: String, val description: String, val videoUrl: String = "")

private val lessons = listOf(
    Lesson("Meet the Alphabet: A to Z", "ABC & Reading", "Ages 3–6", "🔤", "Watch, say, and learn the letters from A to Z.", "android.resource://com.kidsworldtv.app/raw/alphabet_lesson"),
    Lesson("Counting from 1 to 10", "Numbers & Maths", "Ages 3–6", "🔢", "Learn to count from one to ten with friendly objects."),
    Lesson("Learn Colours", "Colours & Shapes", "Ages 2–5", "🌈", "Discover red, blue, yellow, green, and purple."),
    Lesson("Amazing Animals", "Nature & Science", "Ages 4–8", "🦁", "Meet animals and learn where they live."),
    Lesson("Our Solar System", "Little Scientists", "Ages 6–9", "🪐", "Take a gentle first trip through space."),
    Lesson("Shapes Hunt", "Colours & Shapes", "Ages 3–6", "🔺", "Find circles, squares, triangles, and more.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KidsWorldApp() }
    }
}

@Composable
fun KidsWorldApp() {
    var selectedCategory by remember { mutableStateOf("All") }
    var search by remember { mutableStateOf("") }
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }
    var showParentGate by remember { mutableStateOf(false) }
    var parentUnlocked by remember { mutableStateOf(false) }
    var language by rememberSaveable { mutableStateOf("English") }
    var section by remember { mutableStateOf("") }

    val categories = listOf("All", "ABC & Reading", "Numbers & Maths", "Colours & Shapes", "Nature & Science", "Little Scientists")
    val visibleLessons = lessons.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (it.title.contains(search, ignoreCase = true) || it.category.contains(search, ignoreCase = true))
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Teal,
            secondary = Coral,
            background = Sky,
            surface = Color.White,
            onPrimary = Color.White,
            onBackground = Navy,
            onSurface = Navy
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Sky) {
            if (section == "Dictionary") { DictionaryScreen(language, { section = "" })
            } else if (section == "Story Time") { StoryTimeScreen(language, { section = "" })
            } else if (section == "Speak & Pronounce") { SpeakScreen(language, { section = "" })
} else if (selectedLesson != null) {
    LessonScreen(lesson = selectedLesson!!, language = language, onBack = { selectedLesson = null })
} else if (showParentGate) {
                ParentGate(
                    onCancel = { showParentGate = false },
                    onUnlock = {
                        parentUnlocked = true
                        showParentGate = false
                    }
                )
            } else if (parentUnlocked) {
                ParentScreen(onBack = { parentUnlocked = false })
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 28.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Navy)
                                .padding(horizontal = 20.dp, vertical = 22.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🌟", fontSize = 30.sp)
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Kids World TV", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                    Text("Big fun. Bright minds.", color = Color(0xFFCBEAFF), fontSize = 14.sp)
                                }
                                IconButton(onClick = { showParentGate = true }) {
                                    Icon(Icons.Default.Lock, contentDescription = "Parent area", tint = Color.White)
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            Text(if (language == "English") "Choose language" else "Khetha ulimi", color = Color.White, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("English", "IsiZulu").forEach { lang ->
                                    FilterChip(selected = language == lang, onClick = { language = lang }, label = { Text(lang) })
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            Image(
                                painter = painterResource(id = R.drawable.kids_world_tv_cover),
                                contentDescription = "Kids World TV cover",
                                modifier = Modifier.fillMaxWidth().height(170.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(if (language == "English") "What shall we learn today?" else "Sizofundani namuhla?", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = search,
                                onValueChange = { search = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text(if (language == "English") "Search lessons" else "Sesha izifundo") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
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
                            Text(if (language == "English") "More ways to learn" else "Ezinye izindlela zokufunda", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy)
                            Spacer(Modifier.height(8.dp))
                            listOf("📖  My Dictionary" to "Dictionary", "🎬  Story Time" to "Story Time", "🗣️  Speak & Pronounce" to "Speak & Pronounce").forEach { item ->
                                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { section = item.second }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Text(if (language == "IsiZulu") when(item.second) { "Dictionary" -> "📖  Isichazamazwi"; "Story Time" -> "🎬  Isikhathi Sezindaba"; else -> "🗣️  Khuluma Nokubiza Amagama" } else item.first, modifier = Modifier.padding(16.dp), color = Navy, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            Text("Explore topics", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy)
                            Spacer(Modifier.height(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                categories.chunked(2).forEach { pair ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        pair.forEachIndexed { index, category ->
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
                                                modifier = Modifier.weight(1f).clickable { selectedCategory = category },
                                                color = if (active) Teal else Color.White,
                                                shape = RoundedCornerShape(14.dp),
                                                shadowElevation = 1.dp
                                            ) {
                                                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(emoji, fontSize = 24.sp)
                                                    Spacer(Modifier.height(4.dp))
                                                    Text(category, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                                                        color = if (active) Color.White else Navy)
                                                }
                                            }
                                        }
                                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                            Text("Learning videos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy)
                            Text("Choose a lesson to get started.", fontSize = 14.sp, color = Color(0xFF547087))
                        }
                    }
                    if (visibleLessons.isEmpty()) {
                        item {
                            Text("No lessons found. Try another search.", modifier = Modifier.padding(20.dp), color = Navy)
                        }
                    } else {
                        items(visibleLessons) { lesson ->
                            LessonCard(lesson = lesson, onClick = { selectedLesson = lesson })
                        }
                    }
                    item {
                        Text(
                            "A safe space to learn and grow 💛",
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            color = Color(0xFF547087),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonCard(lesson: Lesson, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(76.dp).background(
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
                Text(lesson.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Navy)
                Text(lesson.category, fontSize = 13.sp, color = Color(0xFF547087))
                Spacer(Modifier.height(5.dp))
                Text(lesson.age, fontSize = 12.sp, color = Color(0xFF547087))
            }
            Icon(Icons.Default.PlayArrow, contentDescription = "Open lesson", tint = Coral, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun LessonScreen(lesson: Lesson, language: String, onBack: () -> Unit) {
    val isCounting = lesson.title == "Counting from 1 to 10"
    val isColours = lesson.title == "Learn Colours"
    if (isCounting || isColours) {
        val slides = if (isCounting) listOf(
            Triple("1", "🍎", if (language == "IsiZulu") "I-apula ELILODWA" else "ONE apple"), Triple("2", "🐥🐥", if (language == "IsiZulu") "AMATSHWANE AMABILI" else "TWO little chicks"),
            Triple("3", "⭐ ⭐ ⭐", if (language == "IsiZulu") "IZINKANYEZI EZINTATHU" else "THREE stars"), Triple("4", "🦋 🦋 🦋 🦋", if (language == "IsiZulu") "IZIMVEMVANE EZINE" else "FOUR butterflies"),
            Triple("5", "🌼 🌼 🌼 🌼 🌼", if (language == "IsiZulu") "IZIMBALI EZINHLANU" else "FIVE flowers"), Triple("6", "🐠 🐠 🐠 🐠 🐠 🐠", if (language == "IsiZulu") "IZINHLANZI EZIYISITHUPHA" else "SIX fish"),
            Triple("7", "🎈 🎈 🎈 🎈 🎈 🎈 🎈", if (language == "IsiZulu") "AMABHALUNI AYISIKHOMBISA" else "SEVEN balloons"), Triple("8", "🍓 🍓 🍓 🍓 🍓 🍓 🍓 🍓", if (language == "IsiZulu") "AMASTRAWBERRY AYISISHIYAGALOMBILI" else "EIGHT strawberries"),
            Triple("9", "🚗 🚗 🚗 🚗 🚗 🚗 🚗 🚗 🚗", if (language == "IsiZulu") "IZIMOTO EZIYISISHIYAGALOLUNYE" else "NINE cars"), Triple("10", "🌟 🌟 🌟 🌟 🌟 🌟 🌟 🌟 🌟 🌟", if (language == "IsiZulu") "IZINKANYEZI EZISHUMI EZIKHAZIMULAYO" else "TEN bright stars")
        ) else listOf(
            Triple("RED", "🍎 ❤️ 🍓", if (language == "IsiZulu") "BOMVU NJENGE-APULA" else "Red like an apple"),
            Triple("BLUE", "🐳 💙 🫐", "Blue like the ocean"),
            Triple("YELLOW", "☀️ 🌻 ⭐", "Yellow like the sunshine"),
            Triple("GREEN", "🐸 🍀 🥝", "Green like a leaf"),
            Triple("PURPLE", "🍇 💜 🌂", "Purple like grapes")
        )
        var page by remember(lesson.title) { mutableIntStateOf(0) }
        val slide = slides[page]
        val slideColor = if (isCounting) Color(0xFFFFF2CC) else when (slide.first) {
            "RED" -> Color(0xFFFFE0E0)
            "BLUE" -> Color(0xFFDCEEFF)
            "YELLOW" -> Color(0xFFFFF5C2)
            "GREEN" -> Color(0xFFE0F5DF)
            else -> Color(0xFFEEDFFF)
        }
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) { Text("← Back to lessons", color = Navy) }
            Text(lesson.title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Navy)
            Spacer(Modifier.height(12.dp))
            Card(Modifier.fillMaxWidth().weight(1f), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = slideColor)) {
                Column(Modifier.fillMaxSize().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(slide.first, fontSize = if (isCounting) 76.sp else 46.sp, fontWeight = FontWeight.ExtraBold, color = Navy)
                    Spacer(Modifier.height(18.dp))
                    Text(slide.second, fontSize = 35.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(18.dp))
                    Text(slide.third, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Navy, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Slide ${page + 1} of ${slides.size}", color = Navy, fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { if (page > 0) page-- }, enabled = page > 0, modifier = Modifier.weight(1f)) { Text("Previous") }
                Button(onClick = { if (page < slides.lastIndex) page++ else page = 0 }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Teal)) {
                    Text(if (page == slides.lastIndex) "Start again" else "Next")
                }
            }
        }
    } else {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            TextButton(onClick = onBack) { Text("← Back to lessons", color = Navy) }
            Spacer(Modifier.height(20.dp))
            if (lesson.videoUrl.isNotBlank()) VideoPlayer(videoUrl = lesson.videoUrl)
            else Box(Modifier.fillMaxWidth().height(220.dp).background(Navy, RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(lesson.emoji, fontSize = 64.sp)
                    Spacer(Modifier.height(8.dp))
                    Icon(Icons.Default.PlayArrow, contentDescription = "Video placeholder", tint = Yellow, modifier = Modifier.size(46.dp))
                    Text("VIDEO NOT CONNECTED YET", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(22.dp))
            Text(lesson.title, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Navy)
            Text(lesson.category + " • " + lesson.age, color = Color(0xFF547087), fontSize = 14.sp)
            Spacer(Modifier.height(14.dp))
            Text(lesson.description, fontSize = 17.sp, color = Navy)
            Spacer(Modifier.height(20.dp))
            Text(if (lesson.videoUrl.isBlank()) "Video content will be added in a future update." else "If the video does not play, check that the URL is a direct playable video URL and that hosting permits streaming.", color = Color(0xFF547087), fontSize = 14.sp)
        }
    }
}

@Composable
fun DictionaryScreen(language: String, onBack: () -> Unit) {
    val words = listOf(
        Triple("Apple", "A round fruit that grows on a tree.", "I-apula"),
        Triple("Book", "A set of pages with writing or pictures.", "Incwadi"),
        Triple("Cat", "A small animal that says meow.", "Ikati"),
        Triple("Dog", "An animal that can be a friendly pet.", "Inja"),
        Triple("Friend", "A person you like and enjoy spending time with.", "Umngane"),
        Triple("Happy", "Feeling glad or pleased.", "Ukujabula"),
        Triple("School", "A place where children learn.", "Isikole"),
        Triple("Water", "The clear liquid we drink.", "Amanzi")
    )
    var query by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        TextButton(onClick = onBack) { Text("← Back", color = Navy) }
        Text(if (language == "English") "My English Dictionary" else "Isichazamazwi sesiNgisi", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Navy)
        Text(if (language == "English") "English meanings with IsiZulu translations" else "Izincazelo zesiNgisi nezihumusho zesiZulu", color = Color(0xFF547087))
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), label = { Text("Find a word / Thola igama") }, singleLine = true)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(words.filter { it.first.contains(query, true) || it.third.contains(query, true) }) { w ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(w.first, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Teal)
                        Text(w.second, color = Navy)
                        Text("IsiZulu: ${w.third}", color = Color(0xFF547087), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        Text("Starter word list: we will expand this into a full A–Z dictionary with reviewed definitions and audio.", color = Color(0xFF547087), fontSize = 12.sp)
    }
}

@Composable
fun StoryTimeScreen(language: String, onBack: () -> Unit) {
    val stories = if (language == "English") listOf("The Helpful Little Elephant", "The Lion Who Learned to Share", "The Clever Hare", "A Rainy Day Adventure") else listOf("Indlovu Encane Esizayo", "Ibhubesi Elafunda Ukwabelana", "Unogwaja Ohlakaniphile", "Uhambo Losuku Lwemvula")
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        TextButton(onClick = onBack) { Text("← Back", color = Navy) }
        Text(if (language == "English") "Story Time" else "Isikhathi Sezindaba", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = Navy)
        Text(if (language == "English") "Short, kind stories for young learners" else "Izindaba ezimfushane ezifundisa izingane", color = Color(0xFF547087))
        stories.forEachIndexed { i, title ->
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text(listOf("🐘", "🦁", "🐇", "🌦️")[i], fontSize = 34.sp)
                    Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Navy)
                    Text(if (language == "English") "Story clip coming soon" else "Ividiyo yendaba iyeza", color = Coral)
                }
            }
        }
        Text("These are story titles/placeholders. Original narrated clips must be added before they can play.", color = Color(0xFF547087), fontSize = 12.sp)
    }
}

@Composable
fun SpeakScreen(language: String, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val locale = if (language == "IsiZulu") Locale("zu", "ZA") else Locale.US
    val examples = if (language == "IsiZulu") listOf("Sawubona", "Ngiyabonga", "Unjani?") else listOf("Hello", "Thank you", "How are you?")
    var index by remember(language) { mutableIntStateOf(0) }
    var heard by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    val tts = remember { TextToSpeech(context) {} }
    DisposableEffect(tts) { onDispose { tts.shutdown() } }
    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
        heard = spoken
        feedback = if (spoken.isNotBlank() && spoken.contains(examples[index], ignoreCase = true)) "Well done! / Wenze kahle!" else "Good try! Listen and try again. / Zama futhi."
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (language == "IsiZulu") "zu-ZA" else "en-US")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: ${examples[index]}")
            }
            speechLauncher.launch(intent)
        } else feedback = "Microphone permission is needed for speaking practice."
    }
    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) { Text("← Back", color = Navy) }
        Text(if (language == "English") "Speak & Pronounce" else "Khuluma Nokubiza", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy)
        Spacer(Modifier.height(28.dp))
        Text(if (language == "English") "Listen, then say this phrase" else "Lalela bese usho la magama", color = Color(0xFF547087))
        Text(examples[index], fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = Teal)
        Button(onClick = { tts.language = locale; tts.speak(examples[index], TextToSpeech.QUEUE_FLUSH, null, "kids_word") }, colors = ButtonDefaults.buttonColors(containerColor = Teal)) { Text("🔊 Listen") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (language == "IsiZulu") "zu-ZA" else "en-US")
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: ${examples[index]}")
                }; speechLauncher.launch(intent)
            } else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }) { Text("🎤 My turn – speak") }
        if (heard.isNotBlank()) Text("I heard: $heard", color = Navy)
        if (feedback.isNotBlank()) Text(feedback, color = Teal, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        OutlinedButton(onClick = { index = (index + 1) % examples.size; heard = ""; feedback = "" }) { Text("Next phrase") }
        Text("Speech recognition depends on your phone's language services and may not judge pronunciation perfectly. Adult guidance is recommended.", color = Color(0xFF547087), fontSize = 12.sp)
    }
}

@Composable
fun ParentGate(onCancel: () -> Unit, onUnlock: () -> Unit) {
    var answer by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("🔒 Parent area", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = Navy)
        Spacer(Modifier.height(8.dp))
        Text("For the prototype, answer this simple check: what is 4 + 3?", color = Navy)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = answer, onValueChange = { answer = it }, label = { Text("Answer") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { if (answer.trim() == "7") onUnlock() }, colors = ButtonDefaults.buttonColors(containerColor = Teal)) {
            Text("Continue")
        }
        TextButton(onClick = onCancel) { Text("Cancel") }
        Text("Prototype only: replace this with a stronger parent verification flow before release.", color = Color(0xFF547087), fontSize = 12.sp)
    }
}

@Composable
fun ParentScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Back", color = Navy) }
        Text("Parent dashboard", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = Navy)
        Spacer(Modifier.height(12.dp))
        Text("Content and safety settings", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Navy)
        Spacer(Modifier.height(8.dp))
        Text("• Review lessons before publishing", color = Navy)
        Text("• Add licensed educational videos", color = Navy)
        Text("• Configure privacy and screen-time controls", color = Navy)
        Text("• Integrate only ads suitable for child-directed apps", color = Navy)
        Spacer(Modifier.height(18.dp))
        Text("Advertising is not enabled in this starter build.", color = Color(0xFF547087), fontSize = 14.sp)
    }
}


@Composable
fun VideoPlayer(videoUrl: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
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
        update = { it.player = player }
    )
}
