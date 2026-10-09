package com.example.service

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.data.model.AttachmentInfo
import com.example.util.FileAttachmentHelper

/**
 * FilePickerService provides unified file picking capabilities using modern
 * Android ActivityResultContracts to allow students to select PDFs, DOCs, spreadsheets,
 * code files, and images directly from their device storage without requiring broad runtime storage permissions.
 */
object FilePickerService {

    val DOCUMENT_MIME_TYPES = arrayOf(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain",
        "text/csv",
        "text/x-java-source",
        "text/x-c",
        "text/x-python",
        "text/*",
        "*/*"
    )

    val IMAGE_MIME_TYPES = arrayOf(
        "image/png",
        "image/jpeg",
        "image/webp",
        "image/*"
    )

    /**
     * Launch the system document picker using ActivityResultContracts.OpenMultipleDocuments()
     */
    fun launchDocumentPicker(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(DOCUMENT_MIME_TYPES)
    }

    /**
     * Launch the system photo / visual media picker using ActivityResultContracts.PickMultipleVisualMedia()
     */
    fun launchImagePicker(launcher: ActivityResultLauncher<PickVisualMediaRequest>) {
        launcher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    /**
     * Processes chosen Uris from Android Storage Access Framework, extracting metadata,
     * text preview snippets, and base64 for AI Multimodal ingestion.
     */
    fun processSelectedUris(context: Context, uris: List<Uri>): List<AttachmentInfo> {
        return uris.map { uri ->
            FileAttachmentHelper.processUri(context, uri)
        }
    }

    /**
     * Processes a single chosen Uri from storage
     */
    fun processSingleUri(context: Context, uri: Uri): AttachmentInfo {
        return FileAttachmentHelper.processUri(context, uri)
    }
}
