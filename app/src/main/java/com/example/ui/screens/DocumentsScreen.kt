package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentEntity
import com.example.service.FilePickerService
import com.example.ui.components.AttachmentPickerSheet
import com.example.ui.components.MindNestTopBar
import com.example.ui.theme.CreamBase
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PeachAccent
import com.example.ui.theme.SageContainer
import com.example.ui.theme.SageDark
import com.example.ui.theme.SageLight
import com.example.ui.theme.SagePrimary
import com.example.util.FileAttachmentHelper
import com.example.viewmodel.MindNestViewModel

@Composable
fun DocumentsScreen(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val documents by viewModel.documents.collectAsState()
    val userSession by viewModel.userSession.collectAsState()
    var showAttachmentSheet by remember { mutableStateOf(false) }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            val attachments = FilePickerService.processSelectedUris(context, uris)
            attachments.forEach { att ->
                viewModel.addDocument(
                    title = att.fileName,
                    subject = "BCA Semester ${userSession.semester}",
                    fileType = att.fileType.name,
                    sizeText = att.sizeText,
                    summary = att.contentSnippet?.take(150) ?: "Uploaded file for analysis.",
                    content = att.contentSnippet ?: ""
                )
            }
            Toast.makeText(context, "Added ${uris.size} document(s) to My Documents", Toast.LENGTH_SHORT).show()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            val attachments = FilePickerService.processSelectedUris(context, uris)
            attachments.forEach { att ->
                viewModel.addDocument(
                    title = att.fileName,
                    subject = "BCA Semester ${userSession.semester}",
                    fileType = att.fileType.name,
                    sizeText = att.sizeText,
                    summary = att.contentSnippet?.take(150) ?: "Uploaded photo.",
                    content = att.contentSnippet ?: ""
                )
            }
            Toast.makeText(context, "Added ${uris.size} photo(s) to My Documents", Toast.LENGTH_SHORT).show()
        }
    }

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

        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Documents",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDark,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "${documents.size} lecture notes, manuals & PYQ papers",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF7A8680))
                )
            }

            Button(
                onClick = { showAttachmentSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("upload_document_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Upload", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
            }
        }

        if (documents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = SagePrimary.copy(alpha = 0.5f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No study documents yet",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Upload PDF notes, code files, question papers or handwritten sheets for Nesty to solve.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(documents) { doc ->
                    DocumentCard(
                        doc = doc,
                        onAnalyze = { viewModel.analyzeDocumentWithNesty(doc) },
                        onDelete = { viewModel.deleteDocument(doc.id) }
                    )
                }
            }
        }
    }

    if (showAttachmentSheet) {
        AttachmentPickerSheet(
            onDismiss = { showAttachmentSheet = false },
            onPickFromStorage = {
                FilePickerService.launchDocumentPicker(documentPickerLauncher)
            },
            onPickImages = {
                FilePickerService.launchImagePicker(photoPickerLauncher)
            },
            onAttachQuickMaterial = { att ->
                viewModel.addDocument(
                    title = att.fileName,
                    subject = "BCA Semester ${userSession.semester}",
                    fileType = att.fileType.name,
                    sizeText = att.sizeText,
                    summary = att.contentSnippet?.take(150) ?: "Uploaded file for analysis.",
                    content = att.contentSnippet ?: ""
                )
            }
        )
    }
}

@Composable
fun DocumentCard(
    doc: DocumentEntity,
    onAnalyze: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, tint) = getDocIconAndColor(doc.fileType)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(18.dp))
            .border(1.dp, Color(0xFFE5E0D6), RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(tint.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = doc.fileType,
                            tint = tint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = doc.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 13.5.sp
                            ),
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = doc.subject,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = LavenderPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = " • ${doc.sizeText}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF7A8680),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (!doc.summary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = doc.summary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF56625C),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onAnalyze,
                    colors = ButtonDefaults.buttonColors(containerColor = SageLight),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SageDark,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Analyze with Nesty",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SageDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

private fun getDocIconAndColor(fileType: String): Pair<ImageVector, Color> {
    return when (fileType.uppercase()) {
        "PDF" -> Icons.Outlined.PictureAsPdf to Color(0xFFD9534F)
        "CODE" -> Icons.Outlined.Code to SagePrimary
        "IMAGE" -> Icons.Outlined.Image to Color(0xFF388E3C)
        "QUESTION_PAPER", "PYQ" -> Icons.Outlined.Quiz to LavenderPrimary
        "NOTES" -> Icons.Outlined.EditNote to PeachAccent
        "XLS", "CSV" -> Icons.Outlined.TableChart to Color(0xFF217346)
        else -> Icons.Outlined.Description to Color(0xFF2B579A)
    }
}
