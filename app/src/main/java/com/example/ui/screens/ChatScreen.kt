package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttachmentType
import com.example.data.model.ChatMessage
import com.example.data.model.MessageRole
import com.example.service.FilePickerService
import com.example.ui.components.AttachmentPickerSheet
import com.example.ui.components.BcaSubjectSelector
import com.example.ui.components.CameraCaptureDialog
import com.example.ui.components.HangingNestyRobot
import com.example.ui.components.MarkdownFormattedContent
import com.example.ui.components.MindNestTopBar
import com.example.ui.components.NestyAvatar
import com.example.ui.components.VoiceDictationSheet
import com.example.ui.theme.CreamBase
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PeachAccent
import com.example.ui.theme.PeachLight
import com.example.ui.theme.SageDark
import com.example.ui.theme.SageLight
import com.example.ui.theme.SagePrimary
import com.example.util.FileAttachmentHelper
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodel.BcaQuestionCategory
import com.example.viewmodel.BcaQuickPrompt
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.MindNestViewModel
import com.example.viewmodel.Screen

@Composable
fun ChatScreen(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier
) {
    val chatViewModel: ChatViewModel = viewModel()
    ChatScreen(
        viewModel = chatViewModel,
        onNavigateToExams = { viewModel.navigateTo(Screen.EXAM_PREP) },
        onNavigateToProfile = { viewModel.navigateTo(Screen.PROFILE) },
        modifier = modifier
    )
}

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateToExams: (() -> Unit)? = null,
    onNavigateToProfile: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.displayedMessages.collectAsState()
    val filterHistoryEnabled by viewModel.filterHistoryBySubject.collectAsState()
    val pendingAttachments by viewModel.pendingAttachments.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val userSession by viewModel.userSession.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val activeCategory by viewModel.activeCategory.collectAsState()
    val quickPrompts by viewModel.quickPrompts.collectAsState()
    val isApiKeyConfigured by viewModel.isApiKeyConfigured.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showCameraCapture by remember { mutableStateOf(false) }
    var showVoiceDictationSheet by remember { mutableStateOf(false) }

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            showVoiceDictationSheet = true
        } else {
            Toast.makeText(
                context,
                "Microphone permission is required to dictate questions to Nesty",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Native Android Document & PDF Storage Access Framework Launcher
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            val attachments = FilePickerService.processSelectedUris(context, uris)
            attachments.forEach { viewModel.addAttachment(it) }
            Toast.makeText(context, "Attached ${uris.size} file(s) from phone storage", Toast.LENGTH_SHORT).show()
        }
    }

    // Native Android Photo & Visual Media Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            val attachments = FilePickerService.processSelectedUris(context, uris)
            attachments.forEach { viewModel.addAttachment(it) }
            Toast.makeText(context, "Attached ${uris.size} photo(s)", Toast.LENGTH_SHORT).show()
        }
    }

    val listState = rememberLazyListState()

    // Auto-scroll on new message
    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBase)
    ) {
        // MindNest Academic Top Bar
        MindNestTopBar(
            userSession = userSession,
            onSemesterClick = { onNavigateToExams?.invoke() },
            onProfileClick = { onNavigateToProfile?.invoke() }
        )

        // Gemini Connection Status & Category Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gemini API Status Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isApiKeyConfigured) SageLight else Color(0xFFFFEBEE))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isApiKeyConfigured) Color(0xFF2E7D32) else Color.Red)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isApiKeyConfigured) "Gemini 3.5 Flash Active" else "Offline Fallback Mode",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isApiKeyConfigured) SageDark else Color(0xFFC62828),
                        fontSize = 11.sp
                    )
                )
            }

            // Clear chat action
            if (messages.isNotEmpty()) {
                IconButton(
                    onClick = {
                        viewModel.clearChat()
                        Toast.makeText(context, "Chat cleared", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Clear Chat",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // BCA Question Category Chips (All, Code, Theory, Maths, Exams)
        val categoryScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(categoryScrollState)
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BcaQuestionCategory.values().forEach { cat ->
                val isSelected = cat == activeCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) SagePrimary else Color.White)
                        .border(
                            1.dp,
                            if (isSelected) SagePrimary else Color(0xFFE2DDD2),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { viewModel.setCategory(cat) }
                        .padding(horizontal = 11.dp, vertical = 5.dp)
                        .testTag("cat_tab_${cat.name.lowercase()}")
                ) {
                    Text(
                        text = cat.badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else SageDark,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            // Quick Voice Dictation Pill for Hands-Free Study
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(PeachLight)
                    .border(1.dp, PeachAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable {
                        if (hasMicPermission) {
                            showVoiceDictationSheet = true
                        } else {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                    .padding(horizontal = 11.dp, vertical = 5.dp)
                    .testTag("voice_dictation_pill")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = PeachAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Voice Dictate",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }

        // BCA Subject Selector & AI Filter Component (Programming, OS, Mathematics, DPA, etc.)
        BcaSubjectSelector(
            selectedSubject = selectedSubject,
            onSelectSubject = { viewModel.setSubject(it) },
            filterHistoryEnabled = filterHistoryEnabled,
            onToggleFilterHistory = { viewModel.setFilterHistoryBySubject(it) },
            onQuickPromptClick = { promptText ->
                viewModel.sendMessage(promptText)
            },
            showActiveBanner = true
        )

        // Chat Message List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty()) {
                // Friendly Welcome State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    NestyAvatar(sizeDp = 58)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Hello, ${userSession.name.split(" ").first()}! I'm Nesty.",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your AI companion for BCA programming, math, DPA, OS, and semester exam prep.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF6B7871),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Sample ${activeCategory.badge} Inquiries:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SageDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (item in quickPrompts.take(3)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE6E2D8), RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.sendMessage(item.prompt)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 9.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lightbulb,
                                    contentDescription = null,
                                    tint = PeachAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SageDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = item.subject,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.Gray,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(messages) { message ->
                        ChatMessageItem(
                            message = message,
                            onBookmarkToggle = { viewModel.toggleBookmark(message.id) }
                        )
                    }

                    if (isThinking) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                NestyAvatar(sizeDp = 24)
                                Spacer(modifier = Modifier.width(10.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = SagePrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Nesty is formulating solution...",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SageDark,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips (horizontal scroll)
        if (messages.isNotEmpty()) {
            val quickHorizontalScroll = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(quickHorizontalScroll)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (item in quickPrompts.take(6)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(0.8.dp, Color(0xFFDED8CD), RoundedCornerShape(14.dp))
                            .clickable {
                                viewModel.sendMessage(item.prompt)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = SageDark,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }

        // Attached File Pills Preview
        if (pendingAttachments.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(pendingAttachments) { att ->
                    val (attIcon, attTint) = when (att.fileType) {
                        AttachmentType.PDF -> Icons.Outlined.PictureAsPdf to Color(0xFFD9534F)
                        AttachmentType.IMAGE -> Icons.Outlined.Image to Color(0xFF388E3C)
                        AttachmentType.CODE -> Icons.Outlined.Code to SagePrimary
                        else -> Icons.Outlined.Description to Color(0xFF2B579A)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, attTint.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = attIcon,
                            contentDescription = null,
                            tint = attTint,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = att.fileName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SageDark
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = att.sizeText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.5.sp,
                                    color = Color.Gray
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { viewModel.removeAttachment(att.id) }
                        )
                    }
                }
            }
        }

        // ==========================================
        // MAIN CHATBOX WITH HANGING FUTURISTIC NESTY ROBOT
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // The Input Surface Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp) // Leave headroom for Nesty robot hands hanging on the edge
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .border(1.2.dp, Color(0xFFDED8CD), RoundedCornerShape(22.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // "+" Attachment Button
                    IconButton(
                        onClick = { showAttachmentSheet = true },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("attach_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(SageLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Attach file",
                                tint = SageDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Input Text Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = if (!selectedSubject.isNullOrBlank()) "Ask Nesty about $selectedSubject..." else "Ask Nesty anything about BCA...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF909C95)
                                )
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = SagePrimary
                        ),
                        maxLines = 4,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field")
                    )

                    // Voice Dictation Button (Hands-free BCA study)
                    IconButton(
                        onClick = {
                            if (hasMicPermission) {
                                showVoiceDictationSheet = true
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("mic_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SageLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Dictate question with voice",
                                tint = SageDark,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    // Send Button
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() || pendingAttachments.isNotEmpty()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        enabled = (inputText.isNotBlank() || pendingAttachments.isNotEmpty()) && !isThinking,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("send_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (inputText.isNotBlank() || pendingAttachments.isNotEmpty()) SagePrimary else Color(0xFFE4EDE8)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank() || pendingAttachments.isNotEmpty()) Color.White else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // ==========================================
            // THE CUTE FUTURISTIC NESTY ROBOT
            // Hanging over the top-right border of the chatbox!
            // ==========================================
            HangingNestyRobot(
                isThinking = isThinking,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-24).dp, y = (-2).dp)
            )
        }
    }

    // Modal Bottom Sheet for File Uploads
    if (showAttachmentSheet) {
        AttachmentPickerSheet(
            onDismiss = { showAttachmentSheet = false },
            onPickFromStorage = {
                FilePickerService.launchDocumentPicker(documentPickerLauncher)
            },
            onPickImages = {
                FilePickerService.launchImagePicker(photoPickerLauncher)
            },
            onCaptureCamera = {
                showCameraCapture = true
            },
            onAttachQuickMaterial = { att ->
                viewModel.addAttachment(att)
            }
        )
    }

    if (showCameraCapture) {
        CameraCaptureDialog(
            onDismiss = { showCameraCapture = false },
            onImageCaptured = { attachment ->
                viewModel.addAttachment(attachment)
            }
        )
    }

    // Modal Bottom Sheet for Hands-Free Voice Dictation
    if (showVoiceDictationSheet) {
        VoiceDictationSheet(
            onDismiss = { showVoiceDictationSheet = false },
            onSendQuestion = { voiceQuestion ->
                viewModel.sendMessage(voiceQuestion)
            },
            onInsertText = { voiceText ->
                inputText = if (inputText.isBlank()) voiceText else "$inputText $voiceText"
            },
            selectedSubject = selectedSubject,
            hasMicPermission = hasMicPermission,
            onRequestPermission = {
                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        )
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            NestyAvatar(sizeDp = 32)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.weight(1f, fill = false),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        )
                    )
                    .background(
                        if (isUser) SagePrimary else Color.White
                    )
                    .border(
                        1.dp,
                        if (isUser) Color.Transparent else Color(0xFFE5DFC),
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    if (!message.subjectTag.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isUser) SageDark else LavenderContainer,
                            modifier = Modifier.padding(bottom = 5.dp)
                        ) {
                            Text(
                                text = "🎯 ${message.subjectTag}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) Color.White else LavenderPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (message.attachments.isNotEmpty()) {
                        for (att in message.attachments) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(bottom = 6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isUser) SageDark else SageLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = if (isUser) Color.White else SageDark,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = att.fileName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUser) Color.White else SageDark
                                    )
                                )
                            }
                        }
                    }

                    if (isUser) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        )
                    } else {
                        MarkdownFormattedContent(content = message.text)
                    }
                }
            }

            // Message action footer (bookmark, timestamp)
            if (!isUser) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                ) {
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (message.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (message.isBookmarked) PeachAccent else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = if (message.isBookmarked) "Saved to bookmarks" else "Save explanation",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            color = Color.Gray
                        ),
                        modifier = Modifier.clickable { onBookmarkToggle() }
                    )
                }
            }
        }
    }
}
