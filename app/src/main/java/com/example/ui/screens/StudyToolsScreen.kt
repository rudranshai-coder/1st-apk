package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FlashcardEntity
import com.example.data.model.McqQuestion
import com.example.ui.components.MindNestTopBar
import com.example.ui.theme.CreamBase
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PeachAccent
import com.example.ui.theme.PeachLight
import com.example.ui.theme.SageContainer
import com.example.ui.theme.SageDark
import com.example.ui.theme.SageLight
import com.example.ui.theme.SagePrimary
import com.example.viewmodel.MindNestViewModel
import com.example.viewmodel.Screen

@Composable
fun StudyToolsScreen(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier
) {
    val userSession by viewModel.userSession.collectAsState()
    val flashcards by viewModel.flashcards.collectAsState()
    val mcqList = viewModel.mcqQuestions

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Flashcards", "MCQ Practice", "Nesty Solvers")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
    ) {
        MindNestTopBar(
            userSession = userSession,
            onSemesterClick = { viewModel.navigateTo(Screen.EXAM_PREP) },
            onProfileClick = { viewModel.navigateTo(Screen.PROFILE) }
        )

        // Title
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            Text(
                text = "BCA Study Tools",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDark,
                    fontSize = 18.sp
                )
            )
            Text(
                text = "Flashcard revision, multiple-choice quizzes & academic solvers",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF7A8680))
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = CreamBase,
            contentColor = SageDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = SagePrimary,
                    height = 2.5.dp
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) SageDark else Color.Gray
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTabIndex) {
            0 -> FlashcardsSection(
                flashcards = flashcards,
                onToggleMastered = { viewModel.toggleMastered(it) }
            )
            1 -> McqPracticeSection(
                mcqList = mcqList,
                onQuizCompleted = { title, subject, score, total ->
                    viewModel.recordQuizAttempt(title, subject, score, total)
                },
                onLaunchAiQuiz = {
                    viewModel.navigateTo(Screen.QUIZ)
                }
            )
            2 -> SolversLauncherSection(
                onLaunchPrompt = { prompt ->
                    viewModel.sendMessage(prompt)
                    viewModel.navigateTo(Screen.CHAT)
                }
            )
        }
    }
}

@Composable
fun FlashcardsSection(
    flashcards: List<FlashcardEntity>,
    onToggleMastered: (FlashcardEntity) -> Unit
) {
    if (flashcards.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No flashcards found for this semester.", color = Color.Gray)
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val safeIndex = currentIndex.coerceIn(0, flashcards.lastIndex)
    val card = flashcards[safeIndex]

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "card_flip"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Card ${safeIndex + 1} of ${flashcards.size}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDark
                )
            )
            Text(
                text = card.subject,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = LavenderPrimary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LavenderContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Flippable Flashcard
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .shadow(2.dp, RoundedCornerShape(22.dp))
                .border(
                    1.2.dp,
                    if (card.isMastered) SagePrimary else Color(0xFFE2DDD1),
                    RoundedCornerShape(22.dp)
                )
                .clickable { isFlipped = !isFlipped }
                .testTag("flashcard_item")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    // Front: Question
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SageLight)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "QUESTION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = SageDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = card.question,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 16.sp,
                                lineHeight = 22.sp
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Tap to reveal answer 🔄",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        )
                    }
                } else {
                    // Back: Answer & Explanation
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.graphicsLayer { rotationY = 180f }
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LavenderContainer)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "ANSWER & REASONING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = LavenderPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = card.answer,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF2E3B34),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Concept: ${card.keyConcept}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SagePrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions: Prev, Mastered, Next
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (currentIndex > 0) {
                        currentIndex--
                        isFlipped = false
                    }
                },
                enabled = currentIndex > 0,
                colors = ButtonDefaults.buttonColors(containerColor = SageLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Previous", color = SageDark, fontWeight = FontWeight.Bold)
            }

            IconButton(
                onClick = { onToggleMastered(card) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (card.isMastered) SageContainer else Color.White)
                    .border(1.dp, Color(0xFFDED8CD), CircleShape)
            ) {
                Icon(
                    imageVector = if (card.isMastered) Icons.Default.CheckCircle else Icons.Default.Check,
                    contentDescription = "Mastered",
                    tint = if (card.isMastered) SagePrimary else Color.Gray
                )
            }

            Button(
                onClick = {
                    if (currentIndex < flashcards.lastIndex) {
                        currentIndex++
                        isFlipped = false
                    }
                },
                enabled = currentIndex < flashcards.lastIndex,
                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Next", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun McqPracticeSection(
    mcqList: List<McqQuestion>,
    onQuizCompleted: (String, String, Int, Int) -> Unit = { _, _, _, _ -> },
    onLaunchAiQuiz: () -> Unit = {}
) {
    var selectedQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    val currentQ = mcqList[selectedQuestionIndex]

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // AI Exam Quiz Generator Launch Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SageContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.2.dp, SagePrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .clickable { onLaunchAiQuiz() }
                    .testTag("btn_launch_ai_quiz_module")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(SagePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AI Exam Readiness Quiz",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDark,
                                        fontSize = 14.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SagePrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Gemini",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SageDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Select any subject and generate a complete exam paper with instant AI grading.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF5A6660),
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Start AI Quiz",
                        tint = SageDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${selectedQuestionIndex + 1} of ${mcqList.size}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDark
                    )
                )
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PeachAccent
                    )
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE5DFC), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = currentQ.subject,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = LavenderPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentQ.question,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 15.sp,
                            lineHeight = 21.sp
                        )
                    )
                }
            }
        }

        items(currentQ.options.size) { optionIndex ->
            val optionText = currentQ.options[optionIndex]
            val isSelected = selectedOptionIndex == optionIndex
            val isCorrect = optionIndex == currentQ.correctIndex

            val backgroundColor = when {
                !isSubmitted && isSelected -> SageContainer
                isSubmitted && isCorrect -> Color(0xFFD4EDDA)
                isSubmitted && isSelected && !isCorrect -> Color(0xFFF8D7DA)
                else -> Color.White
            }

            val borderColor = when {
                !isSubmitted && isSelected -> SagePrimary
                isSubmitted && isCorrect -> Color(0xFF28A745)
                isSubmitted && isSelected && !isCorrect -> Color(0xFFDC3545)
                else -> Color(0xFFE5DFC)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(backgroundColor)
                    .border(1.2.dp, borderColor, RoundedCornerShape(14.dp))
                    .clickable(enabled = !isSubmitted) {
                        selectedOptionIndex = optionIndex
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isSelected || (isSubmitted && isCorrect)) SagePrimary else Color(0xFFF0EFE9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = listOf("A", "B", "C", "D")[optionIndex],
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected || (isSubmitted && isCorrect)) Color.White else Color.DarkGray
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = optionText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF26322C),
                        fontSize = 13.5.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (isSubmitted) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SageLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💡 Explanation:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQ.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF3B4841),
                                fontSize = 12.5.sp,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))

            if (!isSubmitted) {
                Button(
                    onClick = {
                        if (selectedOptionIndex != null) {
                            isSubmitted = true
                            if (selectedOptionIndex == currentQ.correctIndex) {
                                score++
                            }
                        }
                    },
                    enabled = selectedOptionIndex != null,
                    colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Check Answer", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        if (selectedQuestionIndex < mcqList.lastIndex) {
                            selectedQuestionIndex++
                            selectedOptionIndex = null
                            isSubmitted = false
                        } else {
                            // Completed full quiz -> Record attempt in Room
                            onQuizCompleted(
                                "BCA Semester MCQ Practice",
                                currentQ.subject,
                                score,
                                mcqList.size
                            )
                            // Reset quiz
                            selectedQuestionIndex = 0
                            selectedOptionIndex = null
                            isSubmitted = false
                            score = 0
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SageDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedQuestionIndex < mcqList.lastIndex) "Next Question" else "Restart Quiz",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SolversLauncherSection(
    onLaunchPrompt: (String) -> Unit
) {
    val solvers = listOf(
        Pair("C++ & Java Code Debugger", "Find syntax errors, logic bugs, and provide optimized solution with comments."),
        Pair("Discrete Math Truth Table Generator", "Generate a step-by-step truth table and check if a proposition is a Tautology, Contradiction, or Contingency."),
        Pair("SQL Query & Schema Builder", "Write standard ANSI SQL queries with joins, subqueries, and table constraints."),
        Pair("Operating Systems Banker's Algorithm", "Verify system safety state given Allocation, Max, and Available matrices."),
        Pair("BCA Viva Voce Practice", "Ask 3 high-yield viva questions on Computer Networks and OSI Model.")
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(solvers.size) { i ->
            val (title, prompt) = solvers[i]
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE5DFC), RoundedCornerShape(16.dp))
                    .clickable { onLaunchPrompt(prompt) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LavenderContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LavenderPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 13.5.sp
                            )
                        )
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF7A8680),
                                fontSize = 11.5.sp
                            ),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
