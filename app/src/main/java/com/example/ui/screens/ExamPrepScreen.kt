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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.School
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamQuestion
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
fun ExamPrepScreen(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier
) {
    val userSession by viewModel.userSession.collectAsState()
    val papers = viewModel.pastYearPapers

    var selectedSemester by remember { mutableIntStateOf(userSession.semester) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
    ) {
        MindNestTopBar(
            userSession = userSession,
            onSemesterClick = {},
            onProfileClick = { viewModel.navigateTo(Screen.PROFILE) }
        )

        // Title
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            Text(
                text = "BCA Exam Preparation",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDark,
                    fontSize = 18.sp
                )
            )
            Text(
                text = "Previous Year Papers (PYQs), 5/10-Mark Questions & Model Answers",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF7A8680))
            )
        }

        // Semester Selector Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(6) { idx ->
                val sem = idx + 1
                val isSelected = selectedSemester == sem
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) SagePrimary else Color.White)
                        .border(
                            1.dp,
                            if (isSelected) Color.Transparent else Color(0xFFE5DFC),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            selectedSemester = sem
                            viewModel.updateSemester(sem)
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Sem $sem",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else SageDark
                        )
                    )
                }
            }
        }

        // Semester Roadmap Navigation Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SageLight)
                .clickable { viewModel.navigateTo(Screen.ROADMAP) }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.School,
                    contentDescription = null,
                    tint = SageDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Track Syllabus & Topic Progress",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = SageDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
            }
            Text(
                text = "View Roadmap →",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SagePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }

        // Papers List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Exam Strategy Banner
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PeachLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PeachAccent.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalFireDepartment,
                            contentDescription = null,
                            tint = PeachAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "High-Yield Topics for Sem $selectedSemester",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PeachAccent
                                )
                            )
                            Text(
                                text = "Focus on unit proofs, code syntax, and 10-mark algorithmic steps for semester exams.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF6B4536),
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }
            }

            for (paper in papers) {
                item {
                    Text(
                        text = "${paper.subject} • ${paper.year} Paper",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 14.5.sp
                        ),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                items(paper.questions) { q ->
                    ExamQuestionCard(
                        question = q,
                        onSolveWithNesty = {
                            viewModel.sendMessage("Please provide a full 10-mark semester exam model answer for: ${q.questionText}")
                            viewModel.navigateTo(Screen.CHAT)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExamQuestionCard(
    question: ExamQuestion,
    onSolveWithNesty: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(18.dp))
            .border(1.dp, Color(0xFFE5E0D6), RoundedCornerShape(18.dp))
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
                            .clip(RoundedCornerShape(6.dp))
                            .background(SageLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${question.marks} MARKS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = question.unit,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF7A8680)
                        )
                    )
                }

                Text(
                    text = question.examFrequency,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PeachAccent,
                        fontSize = 10.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDark,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.modelAnswer,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF56625C),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSolveWithNesty,
                colors = ButtonDefaults.buttonColors(containerColor = SageLight),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = SageDark,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Generate Full Model Answer with Nesty",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SageDark,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
