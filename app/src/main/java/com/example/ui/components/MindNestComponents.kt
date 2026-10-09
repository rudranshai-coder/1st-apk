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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserSession
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PeachAccent
import com.example.ui.theme.PeachLight
import com.example.ui.theme.SageDark
import com.example.ui.theme.SageLight
import com.example.ui.theme.SagePrimary

@Composable
fun MindNestTopBar(
    userSession: UserSession,
    onSemesterClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Brand & Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onProfileClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .shadow(2.dp, RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2EBF8), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ic_mindnest_hero_icon),
                    contentDescription = "MindNest Logo",
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MindNest",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 17.sp,
                            letterSpacing = (-0.3).sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(LavenderPrimary.copy(alpha = 0.14f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "BCA AI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LavenderPrimary
                            )
                        )
                    }
                }
                Text(
                    text = "BCA • Semester ${userSession.semester}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = Color(0xFF7A8680)
                    )
                )
            }
        }

        // Streak & Semester Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Study Streak Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(PeachLight)
                    .border(0.8.dp, PeachAccent.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("streak_badge")
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Streak",
                    tint = PeachAccent,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${userSession.studyStreakDays}d",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PeachAccent,
                        fontSize = 11.sp
                    )
                )
            }

            // Semester Switcher
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(SageLight)
                    .border(0.8.dp, SagePrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .clickable { onSemesterClick() }
                    .padding(horizontal = 9.dp, vertical = 4.dp)
                    .testTag("semester_switcher")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.School,
                        contentDescription = "Semester",
                        tint = SageDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sem ${userSession.semester}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun NestyAvatar(
    sizeDp: Int = 36,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .shadow(2.dp, CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFE8F6F1), Color(0xFFEDECF9), Color(0xFFFBF4ED))
                )
            )
            .border(1.2.dp, Color(0xFFD6E4DC), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Nesty AI",
            tint = SagePrimary,
            modifier = Modifier.size((sizeDp * 0.55).dp)
        )
    }
}

@Composable
fun MarkdownFormattedContent(
    content: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val blocks = splitContentByCodeBlocks(content)
        for (block in blocks) {
            if (block.isCode) {
                CodeBlockView(
                    code = block.text,
                    language = block.language.ifBlank { "code" }
                )
            } else {
                Text(
                    text = block.text.trim(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = Color(0xFF26322C)
                    )
                )
            }
        }
    }
}

private data class FormattedBlock(
    val isCode: Boolean,
    val text: String,
    val language: String = ""
)

private fun splitContentByCodeBlocks(input: String): List<FormattedBlock> {
    val result = mutableListOf<FormattedBlock>()
    val lines = input.lines()
    var inCode = false
    var currentLang = ""
    val currentBuffer = StringBuilder()

    for (line in lines) {
        if (line.trim().startsWith("```")) {
            if (!inCode) {
                if (currentBuffer.isNotBlank()) {
                    result.add(FormattedBlock(isCode = false, text = currentBuffer.toString()))
                    currentBuffer.clear()
                }
                inCode = true
                currentLang = line.trim().removePrefix("```").trim()
            } else {
                result.add(FormattedBlock(isCode = true, text = currentBuffer.toString().trimEnd(), language = currentLang))
                currentBuffer.clear()
                inCode = false
                currentLang = ""
            }
        } else {
            currentBuffer.appendLine(line)
        }
    }

    if (currentBuffer.isNotBlank()) {
        result.add(FormattedBlock(isCode = inCode, text = currentBuffer.toString().trimEnd(), language = currentLang))
    }

    return result
}
