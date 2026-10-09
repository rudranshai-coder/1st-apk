package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BcaOverallProgress
import com.example.data.model.SemesterRoadmapProgress
import com.example.data.model.SemesterStatus
import com.example.data.model.SubjectRoadmapProgress
import com.example.data.model.TopicProgressEntity
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

/**
 * Main Interactive Semester-wise Roadmap Component for BCA Progression Tracking
 */
@Composable
fun BcaRoadmapComponent(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier,
    onNavigateToQuiz: (String) -> Unit = { subjName ->
        viewModel.setSelectedQuizSubject(subjName)
        viewModel.navigateTo(Screen.QUIZ)
    },
    onNavigateToChat: (String) -> Unit = { viewModel.navigateTo(Screen.CHAT) },
    onNavigateToExams: () -> Unit = { viewModel.navigateTo(Screen.EXAM_PREP) }
) {
    val roadmaps by viewModel.semesterRoadmapList.collectAsState()
    val overallProgress by viewModel.bcaOverallProgress.collectAsState()
    val userSession by viewModel.userSession.collectAsState()

    var selectedSemesterFilter by remember { mutableIntStateOf(0) } // 0 = All, 1..6 = Specific
    var searchQuery by remember { mutableStateOf("") }
    val expandedSemesters = remember { mutableStateMapOf<Int, Boolean>() }
    val expandedSubjects = remember { mutableStateMapOf<String, Boolean>() }

    // By default expand current semester
    remember(userSession.semester) {
        expandedSemesters[userSession.semester] = true
    }

    val filteredRoadmaps = remember(roadmaps, selectedSemesterFilter, searchQuery) {
        val list = if (selectedSemesterFilter == 0) {
            roadmaps
        } else {
            roadmaps.filter { it.semesterNumber == selectedSemesterFilter }
        }

        if (searchQuery.isBlank()) {
            list
        } else {
            val q = searchQuery.trim().lowercase()
            list.filter { sem ->
                sem.title.lowercase().contains(q) ||
                sem.subjects.any { subj ->
                    subj.name.lowercase().contains(q) ||
                    subj.code.lowercase().contains(q) ||
                    subj.topics.any { it.topicName.lowercase().contains(q) }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Overall Degree Progression Header
        BcaDegreeProgressCard(
            progress = overallProgress,
            onActionClick = {
                onNavigateToChat("Help me master ${overallProgress.nextRecommendedSubject}: ${overallProgress.nextRecommendedTopic} step-by-step!")
            }
        )

        // 2. Search & Filter Bar
        RoadmapFilterBar(
            selectedSemester = selectedSemesterFilter,
            onSemesterSelected = { selectedSemesterFilter = it },
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )

        // 3. Semester Progression Timeline
        if (filteredRoadmaps.isEmpty()) {
            RoadmapEmptyState(onReset = {
                selectedSemesterFilter = 0
                searchQuery = ""
            })
        } else {
            filteredRoadmaps.forEachIndexed { index, semesterProgress ->
                val isExpanded = expandedSemesters[semesterProgress.semesterNumber] ?: (semesterProgress.status == SemesterStatus.CURRENT)

                SemesterTimelineNodeCard(
                    semester = semesterProgress,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedSemesters[semesterProgress.semesterNumber] = !isExpanded
                    },
                    expandedSubjects = expandedSubjects,
                    onToggleSubjectExpand = { subjId ->
                        expandedSubjects[subjId] = !(expandedSubjects[subjId] ?: false)
                    },
                    onToggleTopic = { topicId, currentCompleted ->
                        viewModel.toggleTopicCompletion(topicId, currentCompleted)
                    },
                    onPracticeQuiz = { subj -> onNavigateToQuiz(subj.name) },
                    onAskAi = { topicName ->
                        onNavigateToChat("Explain the BCA concept '$topicName' with simple real-world examples and exam-ready points.")
                    },
                    onViewPyqs = onNavigateToExams
                )
            }
        }
    }
}

/**
 * BCA Degree Overall Progress Card
 */
@Composable
fun BcaDegreeProgressCard(
    progress: BcaOverallProgress,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1A2C4638))
            .border(1.dp, SagePrimary.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SageLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = "BCA Roadmap",
                            tint = SageDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "BCA Progression Roadmap",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = "3-Year Degree Curriculum & Mastery Plan",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF7A8680),
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Progress Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SagePrimary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SagePrimary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${progress.overallPercentage}% Complete",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Progress Bar
            val animatedProgress by animateFloatAsState(
                targetValue = progress.overallPercentage / 100f,
                label = "bca_overall_progress"
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = SagePrimary,
                    trackColor = Color(0xFFEFECE5)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${progress.completedDegreeTopics} of ${progress.totalDegreeTopics} Topics Completed",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6E7A73),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = "${progress.completedSemestersCount} of 6 Semesters Cleared",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SageDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BcaRoadmapMetricPill(
                    label = "Current Milestone",
                    value = "Semester ${progress.currentSemester}",
                    tint = SageDark,
                    bg = SageContainer,
                    modifier = Modifier.weight(1f)
                )
                BcaRoadmapMetricPill(
                    label = "Subjects Mastered",
                    value = "${progress.subjectsMasteredCount} / ${progress.totalSubjectsCount}",
                    tint = LavenderPrimary,
                    bg = LavenderContainer,
                    modifier = Modifier.weight(1f)
                )
                BcaRoadmapMetricPill(
                    label = "Semesters Left",
                    value = "${6 - progress.completedSemestersCount} Left",
                    tint = PeachAccent,
                    bg = PeachLight,
                    modifier = Modifier.weight(1f)
                )
            }

            // Up Next Recommendation Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onActionClick() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SagePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "NEXT RECOMMENDED TOPIC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SagePrimary,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = "${progress.nextRecommendedSubject} • ${progress.nextRecommendedTopic}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SageDark,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Start Topic",
                        tint = SageDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BcaRoadmapMetricPill(
    label: String,
    value: String,
    tint: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg.copy(alpha = 0.6f))
            .padding(vertical = 10.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = tint,
                    fontSize = 14.sp
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF6E7A73),
                    fontSize = 10.sp
                )
            )
        }
    }
}

/**
 * Filter and Search Bar for Roadmaps
 */
@Composable
fun RoadmapFilterBar(
    selectedSemester: Int,
    onSemesterSelected: (Int) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Search subjects, topics (e.g. Normalization, SQL, OS)...",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, color = Color(0xFF9AA59F))
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = SageDark,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SagePrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clickable { onSearchQueryChange("") }
                            .padding(end = 12.dp)
                    )
                }
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
                .testTag("input_roadmap_search")
        )

        // Semester Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedSemester == 0,
                onClick = { onSemesterSelected(0) },
                label = { Text("All 6 Semesters", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SageDark,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Color(0xFF5A6660)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("filter_sem_all")
            )

            (1..6).forEach { semNum ->
                val isSelected = selectedSemester == semNum
                FilterChip(
                    selected = isSelected,
                    onClick = { onSemesterSelected(semNum) },
                    label = {
                        Text(
                            text = if (semNum == 3) "Sem 3 (Active)" else "Sem $semNum",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (semNum == 3) SagePrimary else SageDark,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Color(0xFF5A6660)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("filter_sem_$semNum")
                )
            }
        }
    }
}

/**
 * Semester Timeline Node Card
 */
@Composable
fun SemesterTimelineNodeCard(
    semester: SemesterRoadmapProgress,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    expandedSubjects: Map<String, Boolean>,
    onToggleSubjectExpand: (String) -> Unit,
    onToggleTopic: (String, Boolean) -> Unit,
    onPracticeQuiz: (SubjectRoadmapProgress) -> Unit,
    onAskAi: (String) -> Unit,
    onViewPyqs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nodeColor = when (semester.status) {
        SemesterStatus.COMPLETED -> SageDark
        SemesterStatus.CURRENT -> SagePrimary
        SemesterStatus.UPCOMING -> Color(0xFF9AA59F)
    }

    val statusBadgeText = when (semester.status) {
        SemesterStatus.COMPLETED -> "COMPLETED"
        SemesterStatus.CURRENT -> "CURRENT SEMESTER"
        SemesterStatus.UPCOMING -> "UPCOMING"
    }

    val statusBadgeBg = when (semester.status) {
        SemesterStatus.COMPLETED -> SageLight
        SemesterStatus.CURRENT -> PeachLight
        SemesterStatus.UPCOMING -> Color(0xFFF1EFEA)
    }

    val statusBadgeTextColor = when (semester.status) {
        SemesterStatus.COMPLETED -> SageDark
        SemesterStatus.CURRENT -> Color(0xFFC05E26)
        SemesterStatus.UPCOMING -> Color(0xFF6E7A73)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Left Timeline Column (Node + Connecting Line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            // Milestone Circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(nodeColor)
                    .then(
                        if (semester.status == SemesterStatus.CURRENT) {
                            Modifier.border(3.dp, PeachAccent.copy(alpha = 0.5f), CircleShape)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (semester.status) {
                    SemesterStatus.COMPLETED -> {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    SemesterStatus.CURRENT -> {
                        Text(
                            text = "S${semester.semesterNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                    }
                    SemesterStatus.UPCOMING -> {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Upcoming",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Connecting vertical dashed/solid line
            Canvas(
                modifier = Modifier
                    .width(4.dp)
                    .height(if (isExpanded) 180.dp else 60.dp)
                    .padding(vertical = 4.dp)
            ) {
                val isDashed = semester.status == SemesterStatus.UPCOMING
                val strokeColor = if (semester.status == SemesterStatus.COMPLETED) SageDark.copy(alpha = 0.6f) else Color(0xFFD4CEC3)

                drawLine(
                    color = strokeColor,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = if (isDashed) PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f) else null
                )
            }
        }

        // Right Card with Semester Content
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .weight(1f)
                .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x14000000))
                .border(
                    width = if (semester.status == SemesterStatus.CURRENT) 1.5.dp else 1.dp,
                    color = if (semester.status == SemesterStatus.CURRENT) SagePrimary.copy(alpha = 0.4f) else Color(0xFFEBE6DC),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Row (Semester & Title)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleExpand() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Semester ${semester.semesterNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = nodeColor,
                                    fontSize = 12.sp
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = statusBadgeBg
                            ) {
                                Text(
                                    text = statusBadgeText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = statusBadgeTextColor,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = semester.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = semester.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF7A8680),
                                fontSize = 12.sp
                            )
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = SageDark,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Progress Bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${semester.completedTopics} / ${semester.totalTopics} Topics Mastered",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6E7A73),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "${semester.progressPercentage}%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = nodeColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    LinearProgressIndicator(
                        progress = { semester.progressPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = nodeColor,
                        trackColor = Color(0xFFF0EDE6)
                    )
                }

                // Career Skills Chips
                if (semester.careerSkills.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        semester.careerSkills.forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF7F5F0),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2DDD5))
                            ) {
                                Text(
                                    text = skill,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF5A6660),
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Expandable Subject List
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HorizontalDivider(color = Color(0xFFF0EDE6))

                        Text(
                            text = "Subjects & Topics Curriculum",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 12.sp
                            )
                        )

                        semester.subjects.forEach { subj ->
                            val isSubjExpanded = expandedSubjects[subj.id] ?: (semester.status == SemesterStatus.CURRENT)

                            SubjectRoadmapCard(
                                subject = subj,
                                isExpanded = isSubjExpanded,
                                onToggleExpand = { onToggleSubjectExpand(subj.id) },
                                onToggleTopic = onToggleTopic,
                                onPracticeQuiz = { onPracticeQuiz(subj) },
                                onAskAi = onAskAi
                            )
                        }

                        // Semester PYQ Shortcut Button
                        OutlinedButton(
                            onClick = onViewPyqs,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SageDark)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lightbulb,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = SageDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "View PYQs & Exam Solutions for Semester ${semester.semesterNumber}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Subject Roadmap Card inside a semester
 */
@Composable
fun SubjectRoadmapCard(
    subject: SubjectRoadmapProgress,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleTopic: (String, Boolean) -> Unit,
    onPracticeQuiz: () -> Unit,
    onAskAi: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CreamSurface),
        modifier = modifier
            .fillMaxWidth()
            .border(0.8.dp, Color(0xFFE5E0D6), RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Subject Title Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Subject Code Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SageContainer
                    ) {
                        Text(
                            text = subject.code,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "${subject.completedTopics} of ${subject.totalTopics} topics • ${subject.progressPercentage}%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF7A8680),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (subject.isCompleted) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Completed",
                            tint = SagePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF7A8680),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Subject Progress Line
            LinearProgressIndicator(
                progress = { subject.progressPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (subject.isCompleted) SagePrimary else LavenderPrimary,
                trackColor = Color(0xFFEBE6DC)
            )

            // Expandable Topic Checklist
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subject.topics.forEach { topic ->
                        TopicChecklistItem(
                            topic = topic,
                            onToggle = { onToggleTopic(topic.id, topic.isCompleted) },
                            onAskNesty = { onAskAi(topic.topicName) }
                        )
                    }

                    // Action buttons row: Take Quiz & Ask AI
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onPracticeQuiz,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Quiz,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("MCQ Quiz", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onAskAi(subject.name) },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SageDark)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = SageDark
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ask Nesty AI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive Topic Checklist Item
 */
@Composable
fun TopicChecklistItem(
    topic: TopicProgressEntity,
    onToggle: () -> Unit,
    onAskNesty: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (topic.isCompleted) Color(0xFFF3F7F4) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            if (topic.isCompleted) SagePrimary.copy(alpha = 0.3f) else Color(0xFFE2DDD5)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onToggle() }
            ) {
                Checkbox(
                    checked = topic.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SagePrimary,
                        uncheckedColor = Color(0xFF88958E)
                    ),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = topic.topicName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = if (topic.isCompleted) FontWeight.Medium else FontWeight.Normal,
                            color = if (topic.isCompleted) SageDark else Color(0xFF2C3E35),
                            textDecoration = if (topic.isCompleted) TextDecoration.None else null
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (topic.isCompleted) "Mastered (${topic.masteryPercentage}%)" else "Not Started",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = if (topic.isCompleted) SagePrimary else Color(0xFF9AA59F)
                        )
                    )
                }
            }

            // Quick AI Explain button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF7F5F0))
                    .clickable { onAskNesty() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = "Explain topic with AI",
                    tint = SageDark,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun RoadmapEmptyState(onReset: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = SagePrimary,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "No Subjects or Topics Found",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDark
                )
            )
            Text(
                text = "Try searching for a different keyword or view all semesters.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF7A8680))
            )
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Show All Semesters", fontSize = 12.sp)
            }
        }
    }
}

/**
 * Preview Widget for BCA Roadmap placed directly on the Dashboard Overview screen
 */
@Composable
fun BcaRoadmapPreviewCard(
    viewModel: MindNestViewModel,
    onOpenFullRoadmap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val overallProgress by viewModel.bcaOverallProgress.collectAsState()
    val animatedPct by animateFloatAsState(
        targetValue = overallProgress.overallPercentage / 100f,
        label = "preview_bca_progress"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(22.dp), spotColor = Color(0x1F2C4638))
            .border(1.dp, SagePrimary.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            .clickable { onOpenFullRoadmap() }
            .testTag("bca_roadmap_preview_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SageContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = "BCA Roadmap",
                            tint = SagePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "BCA Semester Roadmap",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Semester ${overallProgress.currentSemester} Milestone • 6-Sem Plan",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6E7B74),
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SageLight
                ) {
                    Text(
                        text = "${overallProgress.overallPercentage}% Done",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { animatedPct },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SagePrimary,
                    trackColor = Color(0xFFF0EDE6)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${overallProgress.completedDegreeTopics} of ${overallProgress.totalDegreeTopics} Topics Completed",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6E7A73),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "${overallProgress.completedSemestersCount}/6 Semesters",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SageDark,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Next Recommended Action Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CreamSurface,
                border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFFE5E0D6)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = SagePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Next: ${overallProgress.nextRecommendedSubject} • ${overallProgress.nextRecommendedTopic}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SageDark,
                                fontSize = 11.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Roadmap",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SagePrimary,
                                fontSize = 11.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = SagePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
