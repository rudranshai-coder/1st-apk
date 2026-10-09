package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveQuizSession
import com.example.data.model.McqQuestion
import com.example.ui.components.MindNestTopBar
import com.example.ui.components.NestyAvatar
import com.example.ui.theme.CreamBase
import com.example.ui.theme.CreamSurface
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
import kotlinx.coroutines.delay

@Composable
fun AiQuizScreen(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier,
    initialSubject: String? = null
) {
    val userSession by viewModel.userSession.collectAsState()
    val activeSession by viewModel.activeQuizSession.collectAsState()
    val isGenerating by viewModel.isQuizGenerating.collectAsState()
    val generationError by viewModel.quizGenerationError.collectAsState()
    val selectedSubjectState by viewModel.selectedQuizSubject.collectAsState()

    var showExitDialog by remember { mutableStateOf(false) }

    // If an initial subject was passed, preselect it
    LaunchedEffect(initialSubject) {
        if (!initialSubject.isNullOrBlank()) {
            viewModel.setSelectedQuizSubject(initialSubject)
        }
    }

    BackHandler {
        if (activeSession != null && !activeSession!!.isFinished) {
            showExitDialog = true
        } else if (activeSession != null && activeSession!!.isFinished) {
            viewModel.resetQuizSession()
        } else {
            viewModel.navigateBack()
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "Exit Quiz in Progress?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SageDark)
                )
            },
            text = {
                Text(
                    text = "Your answers will not be saved if you exit before finishing. Are you sure you want to quit?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF5A6660))
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        viewModel.resetQuizSession()
                        viewModel.navigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Exit Quiz")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Keep Answering", color = SageDark)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
    ) {
        // App Top Bar
        MindNestTopBar(
            userSession = userSession,
            onSemesterClick = { viewModel.navigateTo(Screen.EXAM_PREP) },
            onProfileClick = { viewModel.navigateTo(Screen.PROFILE) }
        )

        // Screen Top Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (activeSession != null && !activeSession!!.isFinished) {
                        showExitDialog = true
                    } else if (activeSession != null && activeSession!!.isFinished) {
                        viewModel.resetQuizSession()
                    } else {
                        viewModel.navigateBack()
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_quiz_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SageDark
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AI Exam Readiness Quiz",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 17.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SagePrimary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = SagePrimary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Gemini 3.5 Flash",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SagePrimary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
                Text(
                    text = "Multiple-choice exam simulations to test and certify your BCA readiness",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF7A8680),
                        fontSize = 11.5.sp
                    )
                )
            }
        }

        // Active State Routing
        when {
            isGenerating -> {
                QuizGeneratingView(subject = selectedSubjectState)
            }
            activeSession != null && !activeSession!!.isFinished -> {
                ActiveQuizQuestionsView(
                    session = activeSession!!,
                    onSelectOption = { qIdx, optIdx ->
                        viewModel.selectQuizOption(qIdx, optIdx)
                    },
                    onCheckAnswer = { qIdx ->
                        viewModel.checkCurrentQuestionAnswer(qIdx)
                    },
                    onNext = {
                        viewModel.nextQuizQuestion()
                    },
                    onPrevious = {
                        viewModel.previousQuizQuestion()
                    },
                    onFinish = { timeSpent ->
                        viewModel.finishQuiz(timeSpent)
                    }
                )
            }
            activeSession != null && activeSession!!.isFinished -> {
                QuizReadinessResultsView(
                    session = activeSession!!,
                    onRetake = {
                        viewModel.startGeneratingQuiz(
                            subject = activeSession!!.subject,
                            topic = activeSession!!.topic,
                            difficulty = activeSession!!.difficulty,
                            questionCount = activeSession!!.questions.size
                        )
                    },
                    onNewSubject = {
                        viewModel.resetQuizSession()
                    },
                    onAskNestyExplanation = { qText ->
                        viewModel.navigateTo(Screen.CHAT)
                        viewModel.sendMessage("Can you explain this BCA exam quiz question in depth: '$qText'?")
                    },
                    onViewRoadmap = {
                        viewModel.navigateTo(Screen.ROADMAP)
                    }
                )
            }
            else -> {
                QuizConfigurationView(
                    viewModel = viewModel,
                    errorMessage = generationError,
                    onStartQuiz = { subject, topic, difficulty, count ->
                        viewModel.startGeneratingQuiz(subject, topic, difficulty, count)
                    }
                )
            }
        }
    }
}

/**
 * Screen 1: Subject Selection & Quiz Setup Configuration
 */
@Composable
fun QuizConfigurationView(
    viewModel: MindNestViewModel,
    errorMessage: String?,
    onStartQuiz: (String, String?, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val bcaSubjects = viewModel.subjects
    val selectedSubject by viewModel.selectedQuizSubject.collectAsState()
    var selectedTopic by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("University Exam Standard") }
    var selectedQuestionCount by remember { mutableIntStateOf(5) }

    val difficulties = listOf(
        "University Exam Standard" to "Semester PYQ format with real university weightage",
        "Concept Mastery & Viva" to "In-depth conceptual problems & edge cases",
        "Rapid Revision Drill" to "Fast 5-minute memory and definition test"
    )

    val questionCounts = listOf(5, 10)

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(22.dp), spotColor = Color(0x1A2C4638))
                    .border(1.dp, SagePrimary.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SageLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Quiz,
                            contentDescription = null,
                            tint = SageDark,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Test Your Exam Readiness",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "Pick any subject to receive real AI-generated exam questions aligned with your university curriculum.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6E7B74),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Error Banner if present
        if (!errorMessage.isNullOrBlank()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFDEDED),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC62828))
                        )
                    }
                }
            }
        }

        // 1. Select Subject Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "1. Select BCA Subject",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDark,
                        fontSize = 14.sp
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    bcaSubjects.forEach { subj ->
                        val isSelected = selectedSubject.equals(subj.name, ignoreCase = true) ||
                                (subj.name == "DPA" && selectedSubject == "DPA")

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SagePrimary else Color.White
                            ),
                            modifier = Modifier
                                .width(150.dp)
                                .clickable {
                                    viewModel.setSelectedQuizSubject(subj.name)
                                }
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) SagePrimary else Color(0xFFE2DDD5),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("quiz_subject_${subj.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) Color.White.copy(alpha = 0.25f) else SageLight
                                    ) {
                                        Text(
                                            text = subj.code,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else SageDark
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "Sem ${subj.semester}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF7A8680)
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = subj.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = if (isSelected) Color.White else SageDark
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Optional Topic Focus
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "2. Focus Topic (Optional)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDark,
                        fontSize = 14.sp
                    )
                )

                OutlinedTextField(
                    value = selectedTopic,
                    onValueChange = { selectedTopic = it },
                    placeholder = {
                        Text(
                            text = "e.g. Normalization (1NF to BCNF), Banker's Algorithm, SQL...",
                            fontSize = 12.5.sp,
                            color = Color(0xFF9AA59F)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = SagePrimary,
                        unfocusedBorderColor = Color(0xFFE2DDD5)
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_quiz_topic")
                )

                // Quick Topic Suggestions based on chosen subject
                val chosenSubjObj = bcaSubjects.find { it.name.equals(selectedSubject, ignoreCase = true) || (it.name == "DPA" && selectedSubject == "DPA") }
                if (chosenSubjObj != null && chosenSubjObj.topics.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        chosenSubjObj.topics.take(4).forEach { topicItem ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedTopic == topicItem) SageContainer else Color.White,
                                border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFFDED8CD)),
                                modifier = Modifier.clickable {
                                    selectedTopic = if (selectedTopic == topicItem) "" else topicItem
                                }
                            ) {
                                Text(
                                    text = topicItem,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SageDark,
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedTopic == topicItem) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Difficulty Level
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "3. Exam Difficulty Level",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDark,
                        fontSize = 14.sp
                    )
                )

                difficulties.forEach { (title, subtitle) ->
                    val isSelected = selectedDifficulty == title
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CreamSurface else Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedDifficulty = title }
                            .border(
                                width = if (isSelected) 1.5.dp else 0.8.dp,
                                color = if (isSelected) SagePrimary else Color(0xFFE5DFC),
                                shape = RoundedCornerShape(14.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, if (isSelected) SagePrimary else Color(0xFFB0B9B3), CircleShape)
                                    .background(if (isSelected) SagePrimary else Color.Transparent)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDark,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF6E7B74),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Number of Questions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "4. Number of Questions",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDark,
                        fontSize = 14.sp
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    questionCounts.forEach { count ->
                        val isSelected = selectedQuestionCount == count
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedQuestionCount = count },
                            label = { Text("$count MCQs", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SagePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = Color(0xFF5A6660)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Launch Quiz Button
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    onStartQuiz(
                        selectedSubject,
                        if (selectedTopic.isNotBlank()) selectedTopic else null,
                        selectedDifficulty,
                        selectedQuestionCount
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = SagePrimary.copy(alpha = 0.4f))
                    .testTag("btn_generate_ai_quiz"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate $selectedQuestionCount Exam MCQs with Gemini AI",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Screen 2: Animated Generation in progress view
 */
@Composable
fun QuizGeneratingView(
    subject: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "generating_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(SageLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = "Generating",
                tint = SagePrimary,
                modifier = Modifier
                    .size(44.dp)
                    .rotate(angle)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Generating Exam Quiz for $subject",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SageDark,
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Gemini 3.5 Flash is formulating high-yield university questions with detailed explanations and exam pointers...",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF6E7B74),
                fontSize = 13.sp,
                lineHeight = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        CircularProgressIndicator(
            color = SagePrimary,
            strokeWidth = 3.dp,
            modifier = Modifier.size(36.dp)
        )
    }
}

/**
 * Screen 3: Interactive Exam In Progress
 */
@Composable
fun ActiveQuizQuestionsView(
    session: ActiveQuizSession,
    onSelectOption: (Int, Int) -> Unit,
    onCheckAnswer: (Int) -> Unit = {},
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onFinish: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsElapsed by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            secondsElapsed++
        }
    }

    val currentIdx = session.currentQuestionIndex
    val currentQuestion = session.questions[currentIdx]
    val selectedOption = session.userAnswers[currentIdx]
    val isSubmitted = session.submittedQuestions.contains(currentIdx)

    val progressRatio = (currentIdx + 1).toFloat() / session.totalQuestions.coerceAtLeast(1)

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Exam Status Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, Color(0xFFE2DDD5), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SageContainer
                            ) {
                                Text(
                                    text = session.subject,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDark,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Question ${currentIdx + 1} of ${session.totalQuestions}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        // Timer
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = "Timer",
                                tint = PeachAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val mins = secondsElapsed / 60
                            val secs = secondsElapsed % 60
                            Text(
                                text = String.format("%02d:%02d", mins, secs),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PeachAccent,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SagePrimary,
                        trackColor = Color(0xFFEFECE5)
                    )
                }
            }
        }

        // Question Statement Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1A000000))
                    .border(1.dp, Color(0xFFE2DDD5), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "QUESTION ${currentIdx + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LavenderPrimary,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Single Choice",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF9AA59F),
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentQuestion.question,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }

        // 4 Option Cards
        itemsIndexed(currentQuestion.options) { optIdx, optText ->
            val isSelected = selectedOption == optIdx
            val isCorrect = optIdx == currentQuestion.correctIndex

            val backgroundColor = when {
                isSubmitted && isCorrect -> Color(0xFFD4EDDA)
                isSubmitted && isSelected && !isCorrect -> Color(0xFFF8D7DA)
                isSelected -> SageContainer
                else -> Color.White
            }

            val borderColor = when {
                isSubmitted && isCorrect -> Color(0xFF28A745)
                isSubmitted && isSelected && !isCorrect -> Color(0xFFDC3545)
                isSelected -> SagePrimary
                else -> Color(0xFFE2DDD5)
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isSubmitted) {
                        onSelectOption(currentIdx, optIdx)
                    }
                    .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
                    .testTag("option_${currentIdx}_$optIdx")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSubmitted && isCorrect -> Color(0xFF28A745)
                                    isSubmitted && isSelected && !isCorrect -> Color(0xFFDC3545)
                                    isSelected -> SagePrimary
                                    else -> Color(0xFFF0EFE9)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = listOf("A", "B", "C", "D")[optIdx],
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected || (isSubmitted && isCorrect)) Color.White else Color.DarkGray
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = optText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF2C3E35),
                            fontSize = 13.5.sp,
                            lineHeight = 18.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    if (isSubmitted && isCorrect) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Correct",
                            tint = Color(0xFF28A745),
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (isSubmitted && isSelected && !isCorrect) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Incorrect",
                            tint = Color(0xFFDC3545),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Explanation Card (Revealed after checking or submitting)
        if (isSubmitted) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SageLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Lightbulb,
                                contentDescription = null,
                                tint = SageDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "University Exam Explanation:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Text(
                            text = currentQuestion.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF2E3B34),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
            }
        }

        // Navigation Actions Row
        item {
            Spacer(modifier = Modifier.height(4.dp))

            if (!isSubmitted) {
                Button(
                    onClick = {
                        onCheckAnswer(currentIdx)
                    },
                    enabled = selectedOption != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_check_answer"),
                    colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Check Answer & See Reasoning", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentIdx > 0) {
                        OutlinedButton(
                            onClick = onPrevious,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SageDark)
                        ) {
                            Text("Previous", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (currentIdx < session.questions.lastIndex) {
                        Button(
                            onClick = onNext,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_next_question"),
                            colors = ButtonDefaults.buttonColors(containerColor = SageDark),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Next Question →", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { onFinish(secondsElapsed) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_finish_quiz"),
                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Finish Exam & See Results", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen 4: Exam Readiness Results & Diagnostic Evaluation
 */
@Composable
fun QuizReadinessResultsView(
    session: ActiveQuizSession,
    onRetake: () -> Unit,
    onNewSubject: () -> Unit,
    onAskNestyExplanation: (String) -> Unit,
    onViewRoadmap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedQuestions = remember { mutableStateMapOf<Int, Boolean>() }

    val verdictColor = when {
        session.percentage >= 90 -> SageDark
        session.percentage >= 70 -> SagePrimary
        session.percentage >= 50 -> PeachAccent
        else -> Color(0xFFC62828)
    }

    val verdictBg = when {
        session.percentage >= 90 -> SageLight
        session.percentage >= 70 -> SageContainer
        session.percentage >= 50 -> PeachLight
        else -> Color(0xFFFDEDED)
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Hero Score & Verdict Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1F2C4638))
                    .border(1.2.dp, SagePrimary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Score Circle
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(verdictBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${session.score}/${session.totalQuestions}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = verdictColor,
                                    fontSize = 26.sp
                                )
                            )
                            Text(
                                text = "${session.percentage}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = verdictColor,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Verdict Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = verdictBg
                    ) {
                        Text(
                            text = session.readinessVerdict,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = verdictColor,
                                fontSize = 13.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Text(
                        text = session.feedbackMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF4A5550),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    HorizontalDivider(color = Color(0xFFF0EDE6))

                    // Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${session.score} Correct",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF28A745)
                                )
                            )
                            Text("Accuracy", fontSize = 11.sp, color = Color(0xFF7A8680))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${session.totalQuestions - session.score} Incorrect",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (session.totalQuestions > session.score) Color(0xFFDC3545) else Color(0xFF7A8680)
                                )
                            )
                            Text("Mistakes", fontSize = 11.sp, color = Color(0xFF7A8680))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val mins = session.timeElapsedSeconds / 60
                            val secs = session.timeElapsedSeconds % 60
                            Text(
                                text = String.format("%02d:%02d", mins, secs),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark
                                )
                            )
                            Text("Time Spent", fontSize = 11.sp, color = Color(0xFF7A8680))
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRetake,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retake Quiz", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onNewSubject,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SageDark)
                ) {
                    Text("Change Subject", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }

        // Question-by-Question Review Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question-by-Question Diagnostic Review",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDark,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = "${session.questions.size} Questions",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF7A8680))
                )
            }
        }

        itemsIndexed(session.questions) { idx, q ->
            val userPick = session.userAnswers[idx]
            val isCorrect = userPick == q.correctIndex
            val isExpanded = expandedQuestions[idx] ?: false

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isCorrect) Color(0xFFD4EDDA) else Color(0xFFF8D7DA),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedQuestions[idx] = !isExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isCorrect) Color(0xFF28A745) else Color(0xFFDC3545)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Q${idx + 1}: ${q.question}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark,
                                    fontSize = 12.5.sp
                                ),
                                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFF7A8680),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Expanded Details
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Your Answer: ${if (userPick != null) q.options[userPick] else "Not answered"}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isCorrect) Color(0xFF28A745) else Color(0xFFDC3545),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            )
                            if (!isCorrect) {
                                Text(
                                    text = "Correct Answer: ${q.options[q.correctIndex]}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF28A745),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SageLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "💡 ${q.explanation}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF2C3E35),
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    ),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            // Ask AI clarify button
                            TextButton(
                                onClick = { onAskNestyExplanation(q.question) },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = SagePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ask Nesty AI to explain", fontSize = 11.sp, color = SagePrimary)
                            }
                        }
                    }
                }
            }
        }

        // View BCA Roadmap link
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewRoadmap() }
                    .border(0.8.dp, Color(0xFFE2DDD5), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = SagePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Track Overall BCA Progression",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark
                                )
                            )
                            Text(
                                text = "See how this score impacts your semester progression plan",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF7A8680))
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = SageDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
