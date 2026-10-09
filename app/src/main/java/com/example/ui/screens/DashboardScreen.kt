package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BcaRoadmapComponent
import com.example.ui.components.BcaRoadmapPreviewCard
import com.example.ui.components.BcaSubjectSelector
import com.example.ui.components.LogStudySessionDialog
import com.example.ui.components.MindNestTopBar
import com.example.ui.components.NestyAvatar
import com.example.ui.components.RechartsContainer
import com.example.ui.components.RechartsQuizPerformanceChart
import com.example.ui.components.RechartsStudyTimeBarChart
import com.example.ui.components.RechartsTopicRadialGauge
import com.example.ui.components.RechartsTopicTrackerSection
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

private data class SubjectCardItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val prompt: String
)

@Composable
fun DashboardScreen(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier
) {
    val userSession by viewModel.userSession.collectAsState()
    val subjectStudyStats by viewModel.subjectStudyStats.collectAsState()
    val totalStudyMinutes by viewModel.totalStudyMinutes.collectAsState()
    var selectedDashboardTab by remember { mutableIntStateOf(0) }
    var homeInputText by remember { mutableStateOf("") }
    var showLogSessionDialog by remember { mutableStateOf(false) }

    val quickPills = listOf(
        "Tree Traversals",
        "1NF to BCNF",
        "Deadlock Banker's",
        "SQL Joins",
        "OSI 7 Layers",
        "Truth Tables"
    )

    val coreSubjects = listOf(
        SubjectCardItem(
            title = "Programming",
            subtitle = "C, C++, Java, Python",
            icon = Icons.Outlined.Code,
            color = SagePrimary,
            prompt = "Explain and write practical code with time & space complexity for "
        ),
        SubjectCardItem(
            title = "DPA",
            subtitle = "Data Processing & Applications",
            icon = Icons.Outlined.Storage,
            color = LavenderPrimary,
            prompt = "Explain DPA concepts, processing models, and queries for "
        ),
        SubjectCardItem(
            title = "Operating Systems",
            subtitle = "Deadlock, Scheduling",
            icon = Icons.Outlined.Memory,
            color = PeachAccent,
            prompt = "Explain operating system process scheduling and deadlock conditions for "
        ),
        SubjectCardItem(
            title = "Discrete Math",
            subtitle = "Logic, Sets & Graphs",
            icon = Icons.Outlined.Calculate,
            color = Color(0xFF2E7D32),
            prompt = "Solve this discrete mathematics problem step by step with proofs for "
        )
    )

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

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Dashboard Mode Switcher: Overview vs BCA Roadmap vs Recharts Analytics
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEFECE5))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tab 0: Overview
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedDashboardTab == 0) Color.White else Color.Transparent)
                            .clickable { selectedDashboardTab = 0 }
                            .padding(vertical = 8.dp)
                            .testTag("tab_dashboard_overview"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🏠 Home",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedDashboardTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedDashboardTab == 0) SageDark else Color(0xFF6E7A73),
                                fontSize = 12.sp
                            )
                        )
                    }

                    // Tab 1: BCA Roadmap
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedDashboardTab == 1) Color.White else Color.Transparent)
                            .clickable { selectedDashboardTab = 1 }
                            .padding(vertical = 8.dp)
                            .testTag("tab_dashboard_roadmap"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = null,
                                tint = if (selectedDashboardTab == 1) SageDark else Color(0xFF6E7A73),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Roadmap",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedDashboardTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedDashboardTab == 1) SageDark else Color(0xFF6E7A73),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Tab 2: Recharts Analytics
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedDashboardTab == 2) Color.White else Color.Transparent)
                            .clickable { selectedDashboardTab = 2 }
                            .padding(vertical = 8.dp)
                            .testTag("tab_dashboard_recharts"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.BarChart,
                                contentDescription = null,
                                tint = if (selectedDashboardTab == 2) SageDark else Color(0xFF6E7A73),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Analytics",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedDashboardTab == 2) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedDashboardTab == 2) SageDark else Color(0xFF6E7A73),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            if (selectedDashboardTab == 1) {
                item {
                    BcaRoadmapComponent(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth(),
                        onNavigateToQuiz = { subj ->
                            viewModel.setSelectedQuizSubject(subj)
                            viewModel.navigateTo(Screen.QUIZ)
                        },
                        onNavigateToChat = { viewModel.navigateTo(Screen.CHAT) },
                        onNavigateToExams = { viewModel.navigateTo(Screen.EXAM_PREP) }
                    )
                }
            } else if (selectedDashboardTab == 2) {
                item {
                    RechartsDashboardView(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                // MindNest App Icon with "MindNest" written just below it
                item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x337BB6F8))
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(1.2.dp, Color(0xFFE2EBF8), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_mindnest_hero_icon),
                            contentDescription = "MindNest App Icon",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "MindNest",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 19.sp,
                            letterSpacing = (-0.3).sp
                        )
                    )
                }
            }

            // Personalized Warm Greeting
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = userSession.greetingText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 21.sp,
                                letterSpacing = (-0.3).sp
                            ),
                            modifier = Modifier.testTag("home_greeting_text")
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "BCA Sem ${userSession.semester} • What are we studying today?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6E7B74),
                                fontSize = 12.5.sp
                            )
                        )
                    }

                    // Study Streak Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(PeachLight)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = PeachAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${userSession.studyStreakDays}d",
                            fontWeight = FontWeight.Bold,
                            color = PeachAccent,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Quick Clean Ask Box (The Heart of the App directly on Home)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFFE5DFC), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Quick Subject Filter Indicator
                        val activeSubj by viewModel.selectedSubject.collectAsState()
                        if (activeSubj != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SageContainer
                                ) {
                                    Text(
                                        text = "🎯 AI Filter Active: ${activeSubj?.name}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SageDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = "Reset",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.clickable { viewModel.selectSubject(null) }
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NestyAvatar(sizeDp = 30)
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedTextField(
                                value = homeInputText,
                                onValueChange = { homeInputText = it },
                                placeholder = {
                                    Text(
                                        text = if (activeSubj != null) "Ask Nesty about ${activeSubj?.name}..." else "Ask Nesty anything about BCA...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF909C95),
                                            fontSize = 13.5.sp
                                        )
                                    )
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = SagePrimary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_chat_input")
                            )

                            IconButton(
                                onClick = {
                                    if (homeInputText.isNotBlank()) {
                                        viewModel.sendMessage(homeInputText)
                                        homeInputText = ""
                                    }
                                    viewModel.navigateTo(Screen.CHAT)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SagePrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send to Nesty",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Quick Suggestion Pills
                        Spacer(modifier = Modifier.height(10.dp))
                        val pillScroll = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(pillScroll),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (pill in quickPills) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SageLight)
                                        .clickable {
                                            viewModel.sendMessage(pill)
                                            viewModel.navigateTo(Screen.CHAT)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = pill,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SageDark,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BCA Semester Progression Roadmap Preview Card
            item {
                BcaRoadmapPreviewCard(
                    viewModel = viewModel,
                    onOpenFullRoadmap = { selectedDashboardTab = 1 }
                )
            }

            // Recharts Study Progress & Quiz Performance Overview Widget
            item {
                RechartsStudyProgressPreviewCard(
                    viewModel = viewModel,
                    onOpenFullDashboard = { selectedDashboardTab = 2 }
                )
            }

            // Study Hours Tracker (Room Database Tracking for DPA, OS, Mathematics)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp))
                        .border(1.dp, SagePrimary.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .testTag("study_hours_tracker_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SageContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Timer,
                                        contentDescription = "Study Hours",
                                        tint = SagePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Study Hours Tracked",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SageDark,
                                            fontSize = 15.sp
                                        )
                                    )
                                    val totalHours = String.format(java.util.Locale.US, "%.1f", totalStudyMinutes / 60.0)
                                    Text(
                                        text = "$totalHours hrs total • Room DB",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF6E7B74),
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = { showLogSessionDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("log_study_session_button")
                            ) {
                                Text("+ Log Study", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Subject Stats Row for DPA, OS, Mathematics
                        val dpaMinutes = subjectStudyStats.find { it.subject.contains("DPA", ignoreCase = true) }?.totalMinutes ?: 0
                        val osMinutes = subjectStudyStats.find { it.subject.contains("Operating", ignoreCase = true) || it.subject == "OS" }?.totalMinutes ?: 0
                        val mathMinutes = subjectStudyStats.find { it.subject.contains("Math", ignoreCase = true) }?.totalMinutes ?: 0

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // DPA
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(LavenderContainer)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "DPA",
                                        fontWeight = FontWeight.Bold,
                                        color = LavenderPrimary,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = String.format(java.util.Locale.US, "%.1fh", dpaMinutes / 60.0),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SageDark,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "${dpaMinutes} mins",
                                        color = Color(0xFF6C657A),
                                        fontSize = 10.5.sp
                                    )
                                }
                            }

                            // Operating Systems
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PeachLight)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "OS",
                                        fontWeight = FontWeight.Bold,
                                        color = PeachAccent,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = String.format(java.util.Locale.US, "%.1fh", osMinutes / 60.0),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SageDark,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "${osMinutes} mins",
                                        color = Color(0xFF7A6B65),
                                        fontSize = 10.5.sp
                                    )
                                }
                            }

                            // Mathematics
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SageContainer)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Maths",
                                        fontWeight = FontWeight.Bold,
                                        color = SageDark,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = String.format(java.util.Locale.US, "%.1fh", mathMinutes / 60.0),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SageDark,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "${mathMinutes} mins",
                                        color = Color(0xFF55605A),
                                        fontSize = 10.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Core BCA Subjects (Clean 2x2 Grid)
            item {
                Text(
                    text = "BCA Subjects",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDark,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (row in coreSubjects.chunked(2)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (subj in row) {
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(1.dp, RoundedCornerShape(18.dp))
                                        .border(1.dp, Color(0xFFE5DFC), RoundedCornerShape(18.dp))
                                        .clickable {
                                            viewModel.selectSubjectByName(subj.title)
                                            viewModel.sendMessage("${subj.prompt} my BCA semester syllabus")
                                            viewModel.navigateTo(Screen.CHAT)
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(subj.color.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = subj.icon,
                                                contentDescription = subj.title,
                                                tint = subj.color,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = subj.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SageDark,
                                                fontSize = 13.5.sp
                                            )
                                        )
                                        Text(
                                            text = subj.subtitle,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF7A8680),
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Study Hub Shortcuts (Simple & Clear)
            item {
                Text(
                    text = "Quick Study Hub",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDark,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Documents shortcut
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color(0xFFE5DFC), RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(Screen.DOCUMENTS) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = "Notes",
                                tint = SagePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Notes & Files",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark
                                )
                            )
                        }
                    }

                    // Flashcards shortcut
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color(0xFFE5DFC), RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(Screen.STUDY_TOOLS) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Psychology,
                                contentDescription = "Flashcards",
                                tint = LavenderPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Flashcards",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark
                                )
                            )
                        }
                    }

                    // Exams shortcut
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color(0xFFE5DFC), RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(Screen.EXAM_PREP) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = "Exams",
                                tint = PeachAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Exam PYQs",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark
                                )
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

        if (showLogSessionDialog) {
            LogStudySessionDialog(
                onDismiss = { showLogSessionDialog = false },
                onSaveSession = { subject, topic, durationMinutes, notes, rating ->
                    viewModel.logStudySession(subject, topic, durationMinutes, notes, rating)
                }
            )
        }
    }
}
