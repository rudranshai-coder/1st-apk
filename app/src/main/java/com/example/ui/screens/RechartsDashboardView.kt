package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ChartTeal
import com.example.ui.components.RechartsContainer
import com.example.ui.components.RechartsQuizPerformanceChart
import com.example.ui.components.RechartsStudyTimeBarChart
import com.example.ui.components.RechartsTopicRadialGauge
import com.example.ui.components.RechartsTopicTrackerSection
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
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Preview Widget for Recharts Study Progress displayed directly on Dashboard Home
 */
@Composable
fun RechartsStudyProgressPreviewCard(
    viewModel: MindNestViewModel,
    onOpenFullDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizTrendPoints by viewModel.quizPerformanceTrend.collectAsState()
    val completedTopicCount by viewModel.completedTopicCount.collectAsState()
    val totalTopicCount by viewModel.totalTopicCount.collectAsState()
    val averageQuizPercentage by viewModel.averageQuizPercentage.collectAsState()
    val totalStudyMinutes by viewModel.totalStudyMinutes.collectAsState()

    val topicPct = if (totalTopicCount > 0) ((completedTopicCount.toDouble() / totalTopicCount) * 100).toInt() else 0
    val totalHours = String.format(Locale.US, "%.1f", totalStudyMinutes / 60.0)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(22.dp), spotColor = Color(0x1F2C4638))
            .border(1.dp, SagePrimary.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            .testTag("recharts_progress_preview_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SageLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Analytics,
                            contentDescription = "Analytics",
                            tint = SageDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Study Progress & Performance",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Topics & Quiz Performance Over Time",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6B7570),
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SageContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Recharts™",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Pill KPI Overview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Completed Topics Pill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SageContainer)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = "TOPICS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SageDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                        Text(
                            text = "$completedTopicCount/$totalTopicCount",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SageDark,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "$topicPct% done",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SagePrimary,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                // Quiz Accuracy Pill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LavenderContainer)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = "QUIZ AVG",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = LavenderPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                        Text(
                            text = "$averageQuizPercentage%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SageDark,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Accuracy",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF6C657A),
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                // Study Hours Pill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PeachLight)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = "TRACKED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PeachAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                        Text(
                            text = "${totalHours}h",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SageDark,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Room DB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF7A6B65),
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Embedded Recharts Quiz Performance Line Chart
            RechartsQuizPerformanceChart(
                points = quizTrendPoints.takeLast(6),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // CTA Button to Full Recharts Dashboard
            Button(
                onClick = onOpenFullDashboard,
                colors = ButtonDefaults.buttonColors(containerColor = SageDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_recharts_dashboard_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open Full Recharts Analytics Dashboard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Full Comprehensive Recharts Progress & Analytics Dashboard View
 */
@Composable
fun RechartsDashboardView(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier
) {
    val quizTrendPoints by viewModel.quizPerformanceTrend.collectAsState()
    val dayStudyBarData by viewModel.dayStudyBarData.collectAsState()
    val allTopics by viewModel.allTopicProgress.collectAsState()
    val completedTopicCount by viewModel.completedTopicCount.collectAsState()
    val totalTopicCount by viewModel.totalTopicCount.collectAsState()
    val subjectTopicProgressList by viewModel.subjectTopicProgressList.collectAsState()
    val averageQuizPercentage by viewModel.averageQuizPercentage.collectAsState()
    val totalQuizCount by viewModel.totalQuizCount.collectAsState()
    val totalStudyMinutes by viewModel.totalStudyMinutes.collectAsState()
    val userSession by viewModel.userSession.collectAsState()
    val recentQuizAttempts by viewModel.recentQuizAttempts.collectAsState()

    val topicPct = if (totalTopicCount > 0) ((completedTopicCount.toDouble() / totalTopicCount) * 100).toInt() else 0
    val totalHours = String.format(Locale.US, "%.1f", totalStudyMinutes / 60.0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recharts_full_dashboard_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dashboard Header Card with 2x2 KPIs
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, RoundedCornerShape(22.dp), spotColor = Color(0x1F2C4638))
                .border(1.dp, Color(0xFFE2DDD2), RoundedCornerShape(22.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Student Progress Dashboard",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SageDark,
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = "BCA Sem ${userSession.semester} • Live Room Database",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6A7770),
                                fontSize = 12.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(SageLight)
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Recharts Active",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2x2 Metric Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Topics Completed
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SageContainer)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = SagePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Topics Done",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDark,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$completedTopicCount/$totalTopicCount",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SageDark,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "$topicPct% Syllabus",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF536059),
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }

                    // Quiz Accuracy
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(LavenderContainer)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = LavenderPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Quiz Score",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LavenderPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$averageQuizPercentage%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SageDark,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "$totalQuizCount Quizzes Taken",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF6B6478),
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Study Hours Tracked
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(PeachLight)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Timer,
                                    contentDescription = null,
                                    tint = PeachAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Study Tracked",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PeachAccent,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${totalHours}h",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SageDark,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "DPA, OS & Math",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF7A6B65),
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }

                    // Study Streak
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE6F3EE))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = PeachAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Active Streak",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDark,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${userSession.studyStreakDays} Days",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SageDark,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "High Focus Consistency",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF55605A),
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // 1. Recharts Area/Line Chart: Quiz Performance Over Time
        RechartsContainer(
            title = "Quiz Performance Over Time",
            subtitle = "Interactive test scores with target reference line & scrub inspection",
            badgeText = "Recharts AreaChart"
        ) {
            RechartsQuizPerformanceChart(points = quizTrendPoints)
        }

        // 2. Recharts Radial & Completed Topics Syllabus Tracker
        RechartsContainer(
            title = "Completed Topics & Syllabus Coverage",
            subtitle = "Interactive syllabus checklist for BCA Semester 3 — tap topics to mark complete",
            badgeText = "Topic Tracker"
        ) {
            RechartsTopicRadialGauge(
                completedCount = completedTopicCount,
                totalCount = totalTopicCount,
                subjectStats = subjectTopicProgressList
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Tap topics to update completion status in Room DB:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDark,
                    fontSize = 11.5.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            RechartsTopicTrackerSection(
                topics = allTopics,
                onToggleTopic = { id, comp ->
                    viewModel.toggleTopicCompletion(id, comp)
                }
            )
        }

        // 3. Recharts Bar Chart: Study Hours Distribution
        RechartsContainer(
            title = "Daily Study Hours Distribution",
            subtitle = "7-day study consistency and time spent on DPA, OS, Mathematics & Programming",
            badgeText = "Recharts BarChart"
        ) {
            RechartsStudyTimeBarChart(barDataList = dayStudyBarData)
        }

        // 4. Recent Quiz Attempts History Log
        RechartsContainer(
            title = "Recent Quiz Performance Records",
            subtitle = "Saved in local Room database",
            badgeText = "History Log"
        ) {
            if (recentQuizAttempts.isEmpty()) {
                Text(
                    text = "No recent quizzes found. Take a quiz in Study Tools!",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.US)
                    recentQuizAttempts.take(6).forEach { attempt ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F7F3)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE6E1D8), RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (attempt.percentage >= 80) SageContainer else PeachLight
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Quiz,
                                            contentDescription = null,
                                            tint = if (attempt.percentage >= 80) SagePrimary else PeachAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = attempt.quizTitle,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SageDark,
                                                fontSize = 12.5.sp
                                            )
                                        )
                                        Text(
                                            text = "${attempt.subject} • ${dateFormat.format(attempt.timestamp)}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF75817B),
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (attempt.percentage >= 80) SageLight else Color(0xFFFDE8E4)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${attempt.score}/${attempt.totalQuestions} (${attempt.percentage}%)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (attempt.percentage >= 80) SageDark else PeachAccent,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { viewModel.navigateTo(Screen.STUDY_TOOLS) },
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Practice More MCQs & Quizzes", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }
                }
            }
        }
    }
}
