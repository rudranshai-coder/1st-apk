package com.example.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import com.example.data.model.AttachmentInfo
import com.example.data.model.AttachmentType
import java.io.InputStream
import java.util.UUID

object FileAttachmentHelper {

    fun processUri(context: Context, uri: Uri): AttachmentInfo {
        val contentResolver = context.contentResolver
        var fileName = "document_${System.currentTimeMillis()}"
        var fileSizeLong: Long = 0

        // Query file metadata from ContentResolver
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex) ?: fileName
                    }
                    if (sizeIndex != -1) {
                        fileSizeLong = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val sizeText = when {
            fileSizeLong <= 0 -> "File"
            fileSizeLong < 1024 -> "$fileSizeLong B"
            fileSizeLong < 1024 * 1024 -> "${fileSizeLong / 1024} KB"
            else -> String.format(java.util.Locale.US, "%.1f MB", fileSizeLong / (1024f * 1024f))
        }

        val mimeType = contentResolver.getType(uri) ?: getMimeTypeFromExtension(fileName)
        val fileType = detectAttachmentType(fileName, mimeType)

        var contentSnippet: String? = null
        var base64Data: String? = null

        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                if (fileType == AttachmentType.IMAGE) {
                    val bytes = inputStream.readBytes()
                    base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    contentSnippet = "[Image attached: $fileName ($sizeText)]"
                } else if (fileType == AttachmentType.PDF) {
                    val bytes = inputStream.readBytes()
                    // If file is under 4MB, encode Base64 for Gemini multimodal document API
                    if (bytes.size <= 4 * 1024 * 1024) {
                        base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    }
                    // Try to read plain text parts if present, or create clear document metadata
                    val textPreview = try {
                        val text = String(bytes.take(2000).toByteArray())
                        if (text.any { it.isLetterOrDigit() }) {
                            "PDF Header: " + text.filter { it.isLetterOrDigit() || it.isWhitespace() }.take(250)
                        } else null
                    } catch (_: Exception) { null }
                    contentSnippet = textPreview ?: "[PDF Document: $fileName ($sizeText)]"
                } else {
                    // Text / Code / CSV / Markdown
                    val text = inputStream.bufferedReader().use { it.readText() }
                    contentSnippet = if (text.length > 8000) {
                        text.take(8000) + "\n...[truncated for AI processing]"
                    } else {
                        text
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            contentSnippet = "[File: $fileName ($sizeText)]"
        }

        return AttachmentInfo(
            id = UUID.randomUUID().toString(),
            fileName = fileName,
            fileType = fileType,
            sizeText = sizeText,
            uriString = uri.toString(),
            contentSnippet = contentSnippet,
            base64Data = base64Data,
            mimeType = mimeType
        )
    }

    private fun detectAttachmentType(fileName: String, mimeType: String): AttachmentType {
        val lower = fileName.lowercase()
        return when {
            lower.endsWith(".pdf") || mimeType.contains("pdf") -> AttachmentType.PDF
            lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".webp") || mimeType.startsWith("image/") -> AttachmentType.IMAGE
            lower.endsWith(".java") || lower.endsWith(".c") || lower.endsWith(".cpp") || lower.endsWith(".py") || lower.endsWith(".js") || lower.endsWith(".ts") || lower.endsWith(".html") || lower.endsWith(".css") || lower.endsWith(".sql") -> AttachmentType.CODE
            lower.endsWith(".doc") || lower.endsWith(".docx") || mimeType.contains("msword") || mimeType.contains("wordprocessingml") -> AttachmentType.DOC
            lower.endsWith(".ppt") || lower.endsWith(".pptx") || mimeType.contains("presentationml") -> AttachmentType.PPT
            lower.endsWith(".xls") || lower.endsWith(".xlsx") || mimeType.contains("spreadsheetml") -> AttachmentType.XLS
            lower.endsWith(".csv") -> AttachmentType.CSV
            lower.contains("question") || lower.contains("pyq") || lower.contains("paper") -> AttachmentType.QUESTION_PAPER
            else -> AttachmentType.NOTES
        }
    }

    fun processFile(context: Context, file: java.io.File, customName: String? = null): AttachmentInfo {
        val fileName = customName ?: file.name
        val fileSizeLong = file.length()
        val sizeText = when {
            fileSizeLong <= 0 -> "File"
            fileSizeLong < 1024 -> "$fileSizeLong B"
            fileSizeLong < 1024 * 1024 -> "${fileSizeLong / 1024} KB"
            else -> String.format(java.util.Locale.US, "%.1f MB", fileSizeLong / (1024f * 1024f))
        }
        val bytes = file.readBytes()
        val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return AttachmentInfo(
            id = UUID.randomUUID().toString(),
            fileName = fileName,
            fileType = AttachmentType.IMAGE,
            sizeText = sizeText,
            mimeType = "image/jpeg",
            contentSnippet = "[Captured photo of notes / diagram: $fileName ($sizeText)]",
            base64Data = base64Data
        )
    }

    private fun getMimeTypeFromExtension(fileName: String): String {
        val lower = fileName.lowercase()
        return when {
            lower.endsWith(".pdf") -> "application/pdf"
            lower.endsWith(".png") -> "image/png"
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
            lower.endsWith(".webp") -> "image/webp"
            lower.endsWith(".txt") -> "text/plain"
            lower.endsWith(".csv") -> "text/csv"
            lower.endsWith(".java") -> "text/x-java-source"
            lower.endsWith(".c") || lower.endsWith(".cpp") -> "text/x-c"
            lower.endsWith(".py") -> "text/x-python"
            lower.endsWith(".sql") -> "application/sql"
            lower.endsWith(".docx") -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            lower.endsWith(".pptx") -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            lower.endsWith(".xlsx") -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            else -> "application/octet-stream"
        }
    }
}
