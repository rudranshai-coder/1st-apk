package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MindNestTopBar
import com.example.ui.components.NestyAvatar
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
fun ProfileScreen(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier
) {
    val userSession by viewModel.userSession.collectAsState()
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userSession.name) }
    var editSemester by remember { mutableIntStateOf(userSession.semester) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(userSession.name) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
    ) {
        MindNestTopBar(
            userSession = userSession,
            onSemesterClick = {},
            onProfileClick = {}
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(22.dp))
                        .border(1.dp, Color(0xFFE5E0D6), RoundedCornerShape(22.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(SageLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userSession.name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark,
                                    fontSize = 24.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = userSession.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    nameInput = userSession.name
                                    showEditNameDialog = true
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Name",
                                    tint = SagePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = userSession.email,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CreamBase)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = PeachAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${userSession.studyStreakDays} Days",
                                        fontWeight = FontWeight.Bold,
                                        color = SageDark,
                                        fontSize = 14.sp
                                    )
                                }
                                Text("Streak", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${userSession.questionsSolved}",
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark,
                                    fontSize = 14.sp
                                )
                                Text("Solved", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Sem ${userSession.semester}",
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark,
                                    fontSize = 14.sp
                                )
                                Text("BCA Level", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                            }
                        }
                    }
                }
            }

            // Academic Profile Details
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE5E0D6), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Academic Settings",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Degree Program", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                        Text(
                            text = "Bachelor of Computer Applications (BCA)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SageDark
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select BCA Semester", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            (1..6).forEach { sem ->
                                val isSelected = userSession.semester == sem
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) SagePrimary else CreamBase)
                                        .clickable { viewModel.updateSemester(sem) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$sem",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else SageDark
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick navigation items
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE5E0D6), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo(Screen.SETTINGS) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = SagePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("AI & API Settings", fontWeight = FontWeight.SemiBold, color = SageDark)
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo(Screen.AUTH) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = PeachAccent)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Switch Account / Sign Out", fontWeight = FontWeight.SemiBold, color = SageDark)
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = {
                Text(
                    text = "Edit Your Name",
                    fontWeight = FontWeight.Bold,
                    color = SageDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your name as you'd like it to appear on MindNest home screen and study sessions:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        singleLine = true,
                        placeholder = { Text("Enter your name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = nameInput.trim()
                        if (trimmed.isNotBlank()) {
                            viewModel.updateProfile(
                                name = trimmed,
                                email = userSession.email,
                                university = userSession.university,
                                semester = userSession.semester
                            )
                            android.widget.Toast.makeText(context, "Name updated to $trimmed", android.widget.Toast.LENGTH_SHORT).show()
                            showEditNameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                ) {
                    Text("Save Name")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEditNameDialog = false },
                    colors = ButtonDefaults.textButtonColors()
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun SettingsScreen(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier
) {
    val customApiKey by viewModel.customApiKey.collectAsState()
    val context = LocalContext.current
    var inputKey by remember { mutableStateOf(customApiKey) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Settings & AI Config",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDark,
                    fontSize = 18.sp
                )
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE5E0D6), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = SagePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Nesty AI Intelligence Model",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Model: gemini-3.5-flash\nDirect Multimodal Academic Engine with full support for C, Java, Python, SQL, LaTeX, Discrete Mathematics, and Document OCR.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF56625C),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE5E0D6), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = LavenderPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gemini API Key",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "By default, MindNest automatically connects to the server-side Gemini API key configured in AI Studio Secrets. You can also paste a personal key below:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6B7871),
                                fontSize = 11.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = inputKey,
                            onValueChange = { inputKey = it },
                            placeholder = { Text("Paste custom Gemini API Key (optional)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                unfocusedBorderColor = Color(0xFFDED8CD)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.setCustomApiKey(inputKey)
                                Toast.makeText(context, "API Key configuration updated!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Key", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE5E0D6), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = PeachAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "About MindNest & Nesty",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "MindNest v1.0 • Designed specifically for Bachelor of Computer Applications (BCA) students. Powered by Gemini Multimodal AI & Room Local Database.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF56625C),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
