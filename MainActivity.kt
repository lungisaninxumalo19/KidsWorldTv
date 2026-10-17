package com.kidsworldtv.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Sky = Color(0xFFEAF7FF)
private val Navy = Color(0xFF17324D)
private val Coral = Color(0xFFFF8066)
private val Teal = Color(0xFF39BFA7)
private val Yellow = Color(0xFFFFD45C)

data class Lesson(val title: String, val category: String, val age: String, val emoji: String, val description: String)

private val lessons = listOf(
    Lesson("Meet the Alphabet", "ABC & Reading", "Ages 3–6", "🔤", "Sing along and discover letters A to Z."),
    Lesson("Count to Twenty", "Numbers & Maths", "Ages 3–6", "🔢", "Count colourful objects from one to twenty."),
    Lesson("Colours Around Us", "Colours & Shapes", "Ages 2–5", "🌈", "Explore red, blue, yellow, and every colour."),
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
            if (selectedLesson != null) {
                LessonScreen(lesson = selectedLesson!!, onBack = { selectedLesson = null })
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
                            Spacer(Modifier.height(20.dp))
                            Text("What shall we learn today?", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = search,
                                onValueChange = { search = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Search lessons") },
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
fun LessonScreen(lesson: Lesson, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        TextButton(onClick = onBack) { Text("← Back to lessons", color = Navy) }
        Spacer(Modifier.height(20.dp))
        Box(
            Modifier.fillMaxWidth().height(220.dp).background(Navy, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(lesson.emoji, fontSize = 64.sp)
                Spacer(Modifier.height(8.dp))
                Icon(Icons.Default.PlayArrow, contentDescription = "Video placeholder", tint = Yellow, modifier = Modifier.size(46.dp))
                Text("VIDEO LESSON PLACEHOLDER", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(22.dp))
        Text(lesson.title, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Navy)
        Text(lesson.category + " • " + lesson.age, color = Color(0xFF547087), fontSize = 14.sp)
        Spacer(Modifier.height(14.dp))
        Text(lesson.description, fontSize = 17.sp, color = Navy)
        Spacer(Modifier.height(20.dp))
        Text("Next step: connect a video you own or have permission to stream.", color = Color(0xFF547087), fontSize = 14.sp)
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
