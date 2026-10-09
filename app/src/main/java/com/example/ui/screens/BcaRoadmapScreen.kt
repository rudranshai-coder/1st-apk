package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BcaRoadmapComponent
import com.example.ui.components.MindNestTopBar
import com.example.ui.theme.CreamBase
import com.example.ui.theme.SageDark
import com.example.ui.theme.SagePrimary
import com.example.viewmodel.MindNestViewModel
import com.example.viewmodel.Screen

@Composable
fun BcaRoadmapScreen(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier
) {
    val userSession by viewModel.userSession.collectAsState()

    BackHandler {
        viewModel.navigateBack()
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

        // Screen Header with back arrow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_roadmap_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SageDark
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "BCA Semester Progression Roadmap",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDark,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = "Track your learning progress across all 6 semesters",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF7A8680),
                        fontSize = 12.sp
                    )
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                BcaRoadmapComponent(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth(),
                    onNavigateToQuiz = { subjectName ->
                        viewModel.setSelectedQuizSubject(subjectName)
                        viewModel.navigateTo(Screen.QUIZ)
                    },
                    onNavigateToChat = { prompt ->
                        viewModel.navigateTo(Screen.CHAT)
                    },
                    onNavigateToExams = {
                        viewModel.navigateTo(Screen.EXAM_PREP)
                    }
                )
            }
        }
    }
}
