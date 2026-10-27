package com.example.util

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.Inflater
import java.util.zip.ZipInputStream

object DocumentReader {

    data class ParsedDocument(
        val fileName: String,
        val mimeType: String,
        val textSummary: String,
        val fullContent: String
    )

    fun queryFileName(context: Context, uri: Uri): String {
        var name: String? = null
        try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) name = it.getString(nameIndex)
                }
            }
        } catch (_: Exception) {}
        return name ?: uri.lastPathSegment ?: "document.dat"
    }

    fun readDocument(context: Context, uri: Uri): ParsedDocument {
        val fileName = queryFileName(context, uri)
        val mimeType = context.contentResolver.getType(uri) ?: getMimeTypeFromExtension(fileName)
        val ext = fileName.substringAfterLast('.', "").lowercase()

        val rawBytes = try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
        } catch (e: Exception) {
            return ParsedDocument(
                fileName = fileName,
                mimeType = mimeType,
                textSummary = "Impossible d'accéder au fichier : ${e.localizedMessage}",
                fullContent = ""
            )
        }

        if (rawBytes.isEmpty()) {
            return ParsedDocument(
                fileName = fileName,
                mimeType = mimeType,
                textSummary = "Fichier vide (0 octet).",
                fullContent = ""
            )
        }

        return when {
            ext == "pdf" || mimeType.contains("pdf") -> parsePdf(context, uri, fileName, rawBytes)
            ext == "docx" || mimeType.contains("wordprocessingml") -> parseDocx(fileName, rawBytes)
            ext in listOf("jpg", "jpeg", "png", "webp", "gif") || mimeType.startsWith("image/") -> parseImage(fileName, mimeType, rawBytes)
            else -> parsePlainText(fileName, mimeType, rawBytes)
        }
    }

    private fun parsePdf(context: Context, uri: Uri, fileName: String, bytes: ByteArray): ParsedDocument {
        var pageCount = 0
        try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                val renderer = PdfRenderer(pfd)
                pageCount = renderer.pageCount
                renderer.close()
            }
        } catch (_: Exception) {}

        // Extract real text tokens from PDF streams
        val extractedText = extractTextFromPdfBytes(bytes)
        val summary = if (extractedText.isNotBlank()) {
            "PDF analysé : $fileName (${if (pageCount > 0) "$pageCount pages, " else ""}${extractedText.length} caractères extraits)"
        } else {
            "PDF chargé : $fileName (${if (pageCount > 0) "$pageCount pages" else "${bytes.size / 1024} Ko"}). Document principalement visuel ou numérisé."
        }

        val content = buildString {
            append("=== DOCUMENT PDF : $fileName ===\n")
            if (pageCount > 0) append("Nombre de pages : $pageCount\n")
            append("Taille : ${bytes.size / 1024} Ko\n")
            if (extractedText.isNotBlank()) {
                append("\n--- CONTENU TEXTUEL EXTRAIT ---\n")
                append(extractedText.take(20000))
                append("\n--- FIN DE L'EXTRACTION ---\n")
            } else {
                append("\nNote : Ce document PDF contient des éléments graphiques ou des polices vectorisées. Analyse le contexte selon les instructions de l'utilisateur.\n")
            }
        }

        return ParsedDocument(
            fileName = fileName,
            mimeType = "application/pdf",
            textSummary = summary,
            fullContent = content
        )
    }

    /**
     * Extracts text tokens from PDF streams (supports uncompressed & FlateDecode zlib streams).
     */
    private fun extractTextFromPdfBytes(bytes: ByteArray): String {
        val stringBuffer = StringBuilder()
        val textPattern = Regex("""\(([^\\)]*(?:\\.[^\\)]*)*)\)\s*Tj|\[(.*?)\]\s*TJ""", RegexOption.DOT_MATCHES_ALL)

        // 1. Direct search on uncompressed portions
        try {
            val rawString = String(bytes, Charsets.ISO_8859_1)
            textPattern.findAll(rawString).forEach { match ->
                val singleStr = match.groups[1]?.value
                val arrStr = match.groups[2]?.value
                if (singleStr != null) {
                    val clean = unescapePdfString(singleStr)
                    if (clean.isNotBlank()) stringBuffer.append(clean).append(" ")
                } else if (arrStr != null) {
                    val subPattern = Regex("""\(([^\\)]*(?:\\.[^\\)]*)*)\)""")
                    subPattern.findAll(arrStr).forEach { subMatch ->
                        val clean = unescapePdfString(subMatch.groups[1]?.value ?: "")
                        if (clean.isNotBlank()) stringBuffer.append(clean)
                    }
                    stringBuffer.append(" ")
                }
            }
        } catch (_: Exception) {}

        // 2. Also search inside compressed stream ... endstream blocks
        val streamStartTag = "stream".toByteArray(Charsets.US_ASCII)
        val streamEndTag = "endstream".toByteArray(Charsets.US_ASCII)

        var idx = 0
        while (idx < bytes.size - streamStartTag.size) {
            val startIdx = indexOf(bytes, streamStartTag, idx)
            if (startIdx == -1) break

            var dataStart = startIdx + streamStartTag.size
            if (dataStart < bytes.size && bytes[dataStart] == '\r'.code.toByte()) dataStart++
            if (dataStart < bytes.size && bytes[dataStart] == '\n'.code.toByte()) dataStart++

            val endIdx = indexOf(bytes, streamEndTag, dataStart)
            if (endIdx == -1) break

            val streamLen = endIdx - dataStart
            if (streamLen in 10..500000) {
                try {
                    val decompressed = decompressZlib(bytes, dataStart, streamLen)
                    if (decompressed != null) {
                        val decompStr = String(decompressed, Charsets.ISO_8859_1)
                        textPattern.findAll(decompStr).forEach { match ->
                            val singleStr = match.groups[1]?.value
                            val arrStr = match.groups[2]?.value
                            if (singleStr != null) {
                                val clean = unescapePdfString(singleStr)
                                if (clean.isNotBlank()) stringBuffer.append(clean).append(" ")
                            } else if (arrStr != null) {
                                val subPattern = Regex("""\(([^\\)]*(?:\\.[^\\)]*)*)\)""")
                                subPattern.findAll(arrStr).forEach { subMatch ->
                                    val clean = unescapePdfString(subMatch.groups[1]?.value ?: "")
                                    if (clean.isNotBlank()) stringBuffer.append(clean)
                                }
                                stringBuffer.append(" ")
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
            idx = endIdx + streamEndTag.size
        }

        // Clean up whitespace
        return stringBuffer.toString().replace(Regex("""\s+"""), " ").trim()
    }

    private fun decompressZlib(source: ByteArray, offset: Int, length: Int): ByteArray? {
        return try {
            val inflater = Inflater()
            inflater.setInput(source, offset, length)
            val buffer = ByteArray(4096)
            val out = java.io.ByteArrayOutputStream()
            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                if (count == 0) {
                    if (inflater.needsInput() || inflater.needsDictionary()) break
                }
                out.write(buffer, 0, count)
            }
            inflater.end()
            out.toByteArray()
        } catch (_: Exception) {
            null
        }
    }

    private fun unescapePdfString(str: String): String {
        return str
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\(", "(")
            .replace("\\)", ")")
            .replace("\\\\", "\\")
    }

    private fun parseDocx(fileName: String, bytes: ByteArray): ParsedDocument {
        val stringBuilder = StringBuilder()
        try {
            val zip = ZipInputStream(ByteArrayInputStream(bytes))
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name == "word/document.xml") {
                    val reader = BufferedReader(InputStreamReader(zip, Charsets.UTF_8))
                    val xmlContent = reader.readText()
                    // Extract all <w:t> tags
                    val textRegex = Regex("""<w:t[^>]*>(.*?)</w:t>""")
                    textRegex.findAll(xmlContent).forEach { match ->
                        stringBuilder.append(match.groups[1]?.value ?: "").append(" ")
                    }
                    break
                }
                entry = zip.nextEntry
            }
        } catch (e: Exception) {
            stringBuilder.append("(Erreur d'extraction DOCX : ${e.localizedMessage})")
        }

        val text = stringBuilder.toString().trim()
        val summary = "Document Word DOCX analysé : $fileName (${text.length} caractères)"
        val content = buildString {
            append("=== DOCUMENT WORD : $fileName ===\n")
            append("--- TEXTE EXTRAIT DU DOCUMENT ---\n")
            append(text.take(20000))
            append("\n--- FIN DU DOCUMENT ---\n")
        }

        return ParsedDocument(
            fileName = fileName,
            mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            textSummary = summary,
            fullContent = content
        )
    }

    private fun parseImage(fileName: String, mimeType: String, bytes: ByteArray): ParsedDocument {
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        val summary = "Image importée : $fileName (${bytes.size / 1024} Ko)"
        val content = buildString {
            append("=== IMAGE IMPORTÉE : $fileName ===\n")
            append("Format : $mimeType\n")
            append("Taille : ${bytes.size / 1024} Ko\n")
            append("Data URL : data:$mimeType;base64,${base64.take(1000)}... (image de ${bytes.size} octets)\n")
        }

        return ParsedDocument(
            fileName = fileName,
            mimeType = mimeType,
            textSummary = summary,
            fullContent = content
        )
    }

    private fun parsePlainText(fileName: String, mimeType: String, bytes: ByteArray): ParsedDocument {
        val text = try {
            String(bytes, Charsets.UTF_8)
        } catch (_: Exception) {
            String(bytes, Charsets.ISO_8859_1)
        }

        val summary = "Fichier texte analysé : $fileName (${text.length} caractères)"
        val content = buildString {
            append("=== FICHIER : $fileName ===\n")
            append(text.take(25000))
            append("\n=== FIN DU FICHIER ===\n")
        }

        return ParsedDocument(
            fileName = fileName,
            mimeType = mimeType,
            textSummary = summary,
            fullContent = content
        )
    }

    private fun indexOf(source: ByteArray, target: ByteArray, fromIndex: Int): Int {
        if (target.isEmpty()) return fromIndex
        val max = source.size - target.size
        for (i in fromIndex..max) {
            var j = 0
            while (j < target.size && source[i + j] == target[j]) {
                j++
            }
            if (j == target.size) return i
        }
        return -1
    }

    private fun getMimeTypeFromExtension(fileName: String): String {
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "pdf" -> "application/pdf"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "doc" -> "application/msword"
            "txt" -> "text/plain"
            "json" -> "application/json"
            "csv" -> "text/csv"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }
    }
}
