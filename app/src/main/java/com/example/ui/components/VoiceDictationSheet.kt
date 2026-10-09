package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.SpeechRecognitionService
import com.example.service.SpeechUiState
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

/**
 * Bottom sheet modal for hands-free BCA speech dictation into Nesty AI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceDictationSheet(
    onDismiss: () -> Unit,
    onSendQuestion: (String) -> Unit,
    onInsertText: (String) -> Unit,
    selectedSubject: String? = null,
    hasMicPermission: Boolean = true,
    onRequestPermission: () -> Unit = {}
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val speechService = remember { SpeechRecognitionService(context) }
    val speechState by speechService.uiState.collectAsState()
    val transcribedText by speechService.transcribedText.collectAsState()
    val rmsLevel by speechService.rmsLevel.collectAsState()

    var editableText by remember { mutableStateOf("") }
    var autoSendOnFinish by remember { mutableStateOf(false) }

    // Synchronize speech transcription with local editable state
    LaunchedEffect(transcribedText) {
        if (transcribedText.isNotBlank()) {
            editableText = transcribedText
        }
    }

    // Auto-send when finished if enabled
    LaunchedEffect(speechState) {
        if (autoSendOnFinish && speechState is SpeechUiState.Success) {
            val finalQuery = (speechState as SpeechUiState.Success).finalText
            if (finalQuery.isNotBlank()) {
                onSendQuestion(finalQuery)
                onDismiss()
            }
        }
    }

    // Clean up on exit
    DisposableEffect(Unit) {
        if (hasMicPermission) {
            speechService.startListening(selectedSubject) { finalText ->
                if (autoSendOnFinish && finalText.isNotBlank()) {
                    onSendQuestion(finalText)
                    onDismiss()
                }
            }
        }
        onDispose {
            speechService.destroy()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            speechService.stopListening()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = CreamBase,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("voice_dictation_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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
                            imageVector = Icons.Outlined.Headphones,
                            contentDescription = null,
                            tint = SageDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Hands-Free Voice Study",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "Dictate your BCA question to Nesty",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6A7970),
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = {
                        speechService.stopListening()
                        onDismiss()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Subject Filter Badge if active
            if (!selectedSubject.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LavenderContainer,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.School,
                            contentDescription = null,
                            tint = LavenderPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Active Subject: $selectedSubject",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LavenderPrimary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (!hasMicPermission) {
                // Permission Required State
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PeachLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MicOff,
                                contentDescription = null,
                                tint = PeachAccent,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Microphone Permission Needed",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "To dictate academic programming questions, algorithms, and exam doubts hands-free, please grant microphone access.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6B7871),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onRequestPermission,
                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("grant_mic_permission_button")
                        ) {
                            Text(
                                text = "Allow Microphone Access",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // Active Voice Session Card with Audio Waveforms
                val isListening = speechState is SpeechUiState.Listening || speechState is SpeechUiState.Ready

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (isListening) SagePrimary.copy(alpha = 0.5f) else Color(0xFFE2DDD2),
                            RoundedCornerShape(18.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Sound Wave Visualizer
                        VoiceWaveformVisualizer(
                            isListening = isListening,
                            rmsLevel = rmsLevel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Badge
                        val (statusText, statusBg, statusColor) = when (speechState) {
                            is SpeechUiState.Ready -> Triple("Listening for speech...", SageLight, SageDark)
                            is SpeechUiState.Listening -> Triple("Capturing voice...", SageContainer, SagePrimary)
                            is SpeechUiState.Processing -> Triple("Processing question...", PeachLight, PeachAccent)
                            is SpeechUiState.Success -> Triple("Transcription Complete", LavenderContainer, LavenderPrimary)
                            is SpeechUiState.Error -> Triple(
                                (speechState as SpeechUiState.Error).message,
                                Color(0xFFFFEBEE),
                                Color(0xFFC62828)
                            )
                            SpeechUiState.Idle -> Triple("Microphone ready", Color(0xFFF0EFEA), Color.Gray)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = statusBg,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = statusColor,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Text Transcription Box
                        OutlinedTextField(
                            value = editableText,
                            onValueChange = { editableText = it },
                            placeholder = {
                                Text(
                                    text = if (isListening) {
                                        "Speak your question now...\nE.g.: 'Explain Banker's Algorithm safety state'"
                                    } else {
                                        "Tap the microphone button below to start dictating..."
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF909C95)
                                    )
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                unfocusedBorderColor = Color(0xFFE5DFC),
                                focusedContainerColor = CreamSurface,
                                unfocusedContainerColor = CreamSurface
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp, max = 130.dp)
                                .testTag("voice_transcription_input")
                        )

                        // Quick Term Pills for BCA students
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = PeachAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Quick Technical Phrases:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF6B7871),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        val quickPhrases = remember(selectedSubject) {
                            when {
                                selectedSubject?.contains("OS", ignoreCase = true) == true ||
                                selectedSubject?.contains("Operating", ignoreCase = true) == true -> listOf(
                                    "Deadlock conditions",
                                    "Banker's Algorithm",
                                    "Virtual Memory Paging",
                                    "CPU Scheduling Round Robin"
                                )
                                selectedSubject?.contains("Math", ignoreCase = true) == true -> listOf(
                                    "De Morgan's Laws proof",
                                    "Pigeonhole Principle",
                                    "Truth table equivalence",
                                    "Graph Theory Eulerian path"
                                )
                                selectedSubject?.contains("DPA", ignoreCase = true) == true ||
                                selectedSubject?.contains("DBMS", ignoreCase = true) == true -> listOf(
                                    "1NF to BCNF Normalization",
                                    "ACID Properties",
                                    "SQL Subqueries",
                                    "2-Phase Locking (2PL)"
                                )
                                else -> listOf(
                                    "Explain with C syntax",
                                    "Step-by-step exam answer",
                                    "Time complexity Big-O",
                                    "Write Java example"
                                )
                            }
                        }

                        val phraseScroll = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(phraseScroll),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (phrase in quickPhrases) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SageLight)
                                        .clickable {
                                            editableText = if (editableText.isBlank()) phrase else "$editableText $phrase"
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "+ $phrase",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SageDark,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Hands-free auto-send toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CreamSurface)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Auto-Send to Nesty",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDark,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = "Automatically sends when you pause speaking",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Switch(
                                checked = autoSendOnFinish,
                                onCheckedChange = { autoSendOnFinish = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SagePrimary,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD4CFC4)
                                ),
                                modifier = Modifier.scale(0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Microphone Control FAB & Actions
                val isCurrentlyListening = speechState is SpeechUiState.Listening || speechState is SpeechUiState.Ready

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Clear Button
                    IconButton(
                        onClick = {
                            speechService.clearTranscribedText()
                            editableText = ""
                        },
                        enabled = editableText.isNotBlank(),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (editableText.isNotBlank()) Color(0xFFF0EBE0) else Color(0xFFF7F5F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear transcription",
                                tint = if (editableText.isNotBlank()) SageDark else Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Main Pulsing Microphone Toggle Button
                    PulsingMicButton(
                        isListening = isCurrentlyListening,
                        onClick = {
                            if (isCurrentlyListening) {
                                speechService.stopListening()
                            } else {
                                speechService.startListening(selectedSubject) { finalText ->
                                    if (autoSendOnFinish && finalText.isNotBlank()) {
                                        onSendQuestion(finalText)
                                        onDismiss()
                                    }
                                }
                            }
                        }
                    )

                    // Insert or Send Action Button
                    IconButton(
                        onClick = {
                            if (editableText.isNotBlank()) {
                                onSendQuestion(editableText.trim())
                                speechService.stopListening()
                                onDismiss()
                            }
                        },
                        enabled = editableText.isNotBlank(),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (editableText.isNotBlank()) SagePrimary else Color(0xFFE4EDE8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send dictated question",
                                tint = if (editableText.isNotBlank()) Color.White else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Secondary Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (editableText.isNotBlank()) {
                                onInsertText(editableText.trim())
                                speechService.stopListening()
                                onDismiss()
                            }
                        },
                        enabled = editableText.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("voice_insert_button")
                    ) {
                        Text(
                            text = "Insert into Chat",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SageDark,
                                fontSize = 12.5.sp
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (editableText.isNotBlank()) {
                                onSendQuestion(editableText.trim())
                                speechService.stopListening()
                                onDismiss()
                            }
                        },
                        enabled = editableText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("voice_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ask Nesty",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated sound wave visualizer responding to voice loudness and rhythmic oscillations
 */
@Composable
private fun VoiceWaveformVisualizer(
    isListening: Boolean,
    rmsLevel: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")

    // Harmonics for waveform bars
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w1"
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(580, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w2"
    )

    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(360, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w3"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CreamSurface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barMultipliers = listOf(0.4f, 0.7f, 1.0f, 0.6f, 0.9f, 0.8f, 0.5f)
        val rmsNorm = (rmsLevel / 8f).coerceIn(0f, 1f)

        barMultipliers.forEachIndexed { index, mult ->
            val harmonic = when (index % 3) {
                0 -> wave1
                1 -> wave2
                else -> wave3
            }

            val dynamicHeight = if (isListening) {
                val combined = (harmonic * 0.45f + rmsNorm * 0.55f) * mult
                (10.dp + (34.dp * combined)).coerceIn(8.dp, 44.dp)
            } else {
                6.dp
            }

            val barColor = if (isListening) {
                when (index % 3) {
                    0 -> SagePrimary
                    1 -> SageDark
                    else -> PeachAccent
                }
            } else {
                Color(0xFFC8C2B7)
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(dynamicHeight)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
    }
}

/**
 * Pulsing microphone button with ripple animations when listening
 */
@Composable
private fun PulsingMicButton(
    isListening: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (isListening) 0.5f else 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier.size(68.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer animated ripple ring
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(SagePrimary.copy(alpha = pulseAlpha))
            )
        }

        // Inner FAB
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(if (isListening) SageDark else SagePrimary)
                .clickable { onClick() }
                .testTag("mic_toggle_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isListening) "Stop voice listening" else "Start voice listening",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
