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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttachmentInfo
import com.example.data.model.AttachmentType
import com.example.ui.theme.CreamBase
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PeachAccent
import com.example.ui.theme.PeachLight
import com.example.ui.theme.SageContainer
import com.example.ui.theme.SageDark
import com.example.ui.theme.SageLight
import com.example.ui.theme.SagePrimary

private data class QuickBcaMaterial(
    val title: String,
    val fileType: AttachmentType,
    val icon: ImageVector,
    val color: Color,
    val snippet: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentPickerSheet(
    onDismiss: () -> Unit,
    onPickFromStorage: () -> Unit,
    onPickImages: () -> Unit,
    onCaptureCamera: () -> Unit = {},
    onAttachQuickMaterial: (AttachmentInfo) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val quickMaterials = listOf(
        QuickBcaMaterial(
            title = "DPA_Notes_Unit2.pdf",
            fileType = AttachmentType.PDF,
            icon = Icons.Outlined.PictureAsPdf,
            color = Color(0xFFD9534F),
            snippet = "Unit 2: Relational Model, Functional Dependencies, Armstrong's Axioms, 1NF to BCNF algorithms with lossy vs lossless join decomposition proofs."
        ),
        QuickBcaMaterial(
            title = "OS_EndTerm_2024_Paper.pdf",
            fileType = AttachmentType.QUESTION_PAPER,
            icon = Icons.Outlined.Quiz,
            color = LavenderPrimary,
            snippet = "Section A: 10 Short questions (2 marks each). Section B: Bankers algorithm problem with allocation matrix, Page replacement FIFO vs LRU."
        ),
        QuickBcaMaterial(
            title = "BinarySearchTree.java",
            fileType = AttachmentType.CODE,
            icon = Icons.Outlined.Code,
            color = SagePrimary,
            snippet = "class BSTNode { int val; BSTNode left, right; }\npublic class BST { public void inorder(BSTNode r) { if(r!=null){ inorder(r.left); System.out.println(r.val); inorder(r.right); } } }"
        ),
        QuickBcaMaterial(
            title = "Discrete_Math_Graph_Theory.notes",
            fileType = AttachmentType.NOTES,
            icon = Icons.Outlined.Description,
            color = PeachAccent,
            snippet = "Eulerian graph has even degree for all vertices. Hamiltonian cycle visits each vertex exactly once. Handshaking lemma: sum of degrees = 2 * |E|."
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CreamBase,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Add Attachments",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Attach documents, PDFs, or photos from your device storage",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF7A8680),
                            fontSize = 12.sp
                        )
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==============================================
            // 1. PRIMARY DEVICE STORAGE PICKER (ChatGPT / Gemini style)
            // ==============================================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.2.dp, SagePrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .clickable {
                        onDismiss()
                        onPickFromStorage()
                    }
                    .padding(16.dp)
                    .testTag("choose_from_phone_storage")
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(SageContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.UploadFile,
                        contentDescription = "Upload from Storage",
                        tint = SagePrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Browse Phone Storage...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 14.5.sp
                        )
                    )
                    Text(
                        text = "PDF, DOCX, PPT, XLS, CSV, Code (.java, .c, .py, .sql) & Text",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6A7770),
                            fontSize = 11.5.sp
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = SagePrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==============================================
            // 2. PHOTOS & DIAGRAMS PICKER (Gallery / Phone Storage)
            // ==============================================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5DFC), RoundedCornerShape(16.dp))
                    .clickable {
                        onDismiss()
                        onPickImages()
                    }
                    .padding(16.dp)
                    .testTag("choose_photos_from_gallery")
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(LavenderContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Photos & Screenshots",
                        tint = LavenderPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Photos & Diagrams",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 14.5.sp
                        )
                    )
                    Text(
                        text = "Screenshots, textbook diagrams, whiteboard notes",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6A7770),
                            fontSize = 11.5.sp
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==============================================
            // 3. CAPTURE NOTES & DIAGRAMS (CameraX)
            // ==============================================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, PeachAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .clickable {
                        onDismiss()
                        onCaptureCamera()
                    }
                    .padding(16.dp)
                    .testTag("capture_camera_notes")
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PeachLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = "Capture Notes",
                        tint = PeachAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CameraX Scanner",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 14.5.sp
                        )
                    )
                    Text(
                        text = "Snap handwritten notes, lab sketches & textbook diagrams",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6A7770),
                            fontSize = 11.5.sp
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = PeachAccent
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = Color(0xFFE5E0D6), thickness = 0.8.dp)

            Spacer(modifier = Modifier.height(14.dp))

            // ==============================================
            // 3. QUICK BCA ACADEMIC MATERIALS
            // ==============================================
            Text(
                text = "Quick BCA Study Materials",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDark,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickMaterials) { item ->
                    Column(
                        modifier = Modifier
                            .width(150.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5DFC), RoundedCornerShape(14.dp))
                            .clickable {
                                onAttachQuickMaterial(
                                    AttachmentInfo(
                                        fileName = item.title,
                                        fileType = item.fileType,
                                        sizeText = "1.6 MB",
                                        contentSnippet = item.snippet
                                    )
                                )
                                onDismiss()
                            }
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(item.color.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = item.color,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 11.5.sp
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = "Tap to attach",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
