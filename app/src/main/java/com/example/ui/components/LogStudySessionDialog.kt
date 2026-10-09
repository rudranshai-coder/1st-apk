package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PeachAccent
import com.example.ui.theme.PeachLight
import com.example.ui.theme.SageDark
import com.example.ui.theme.SagePrimary

@Composable
fun LogStudySessionDialog(
    onDismiss: () -> Unit,
    onSaveSession: (subject: String, topic: String, durationMinutes: Int, notes: String, rating: Int) -> Unit
) {
    val subjects = listOf("DPA", "Operating Systems", "Discrete Mathematics", "Data Structures", "Java Programming")
    val durations = listOf(30, 45, 60, 90, 120)

    var selectedSubject by remember { mutableStateOf("DPA") }
    var topicText by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableIntStateOf(60) }
    var notesText by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
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
                                .background(SagePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = SagePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Log Study Session",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 17.sp
                            )
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subject Selector
                Text(
                    text = "Select Subject",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF55605A),
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (subj in listOf("DPA", "Operating Systems", "Discrete Mathematics")) {
                        val isSelected = subj == selectedSubject
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SagePrimary else Color(0xFFF2F4F2))
                                .border(
                                    1.dp,
                                    if (isSelected) SagePrimary else Color(0xFFE0E5E0),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedSubject = subj }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (subj) {
                                    "Operating Systems" -> "OS"
                                    "Discrete Mathematics" -> "Maths"
                                    else -> subj
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else SageDark,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Topic Field
                Text(
                    text = "Topic Studied",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF55605A),
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = topicText,
                    onValueChange = { topicText = it },
                    placeholder = {
                        Text(
                            text = when (selectedSubject) {
                                "DPA" -> "e.g. 1NF to BCNF Normalization & SQL"
                                "Operating Systems" -> "e.g. Deadlock & Banker's Algorithm"
                                else -> "e.g. Propositional Logic & Truth Tables"
                            },
                            fontSize = 12.5.sp,
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SagePrimary,
                        unfocusedBorderColor = Color(0xFFD6DDD8)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("study_topic_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Duration Selector
                Text(
                    text = "Duration (Minutes)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF55605A),
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (mins in durations) {
                        val isSelected = mins == selectedDuration
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) LavenderPrimary else Color(0xFFF4F1F8))
                                .clickable { selectedDuration = mins }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mins}m",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFF4A405A),
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Focus Rating (Stars)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Focus Rating",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF55605A),
                            fontSize = 12.sp
                        )
                    )
                    Row {
                        for (star in 1..5) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "$star stars",
                                tint = if (star <= rating) Color(0xFFF59E0B) else Color.LightGray,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { rating = star }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save Button
                Button(
                    onClick = {
                        val finalTopic = if (topicText.isBlank()) {
                            when (selectedSubject) {
                                "DPA" -> "DPA Concepts & Queries"
                                "Operating Systems" -> "OS Concepts"
                                else -> "Mathematics Problem Solving"
                            }
                        } else topicText.trim()

                        onSaveSession(selectedSubject, finalTopic, selectedDuration, notesText, rating)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("save_study_session_button")
                ) {
                    Text("Save Study Session to Database", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                }
            }
        }
    }
}
