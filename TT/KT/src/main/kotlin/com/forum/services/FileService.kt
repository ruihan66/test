package com.forum.services

import java.io.File
import java.util.UUID
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

class FileService {
    private val uploadDir = "uploads"
    private val maxFileSize = 10L * 1024 * 1024 // 10MB

    init {
        Path(uploadDir).createDirectories()
        Path("$uploadDir/avatars").createDirectories()
        Path("$uploadDir/posts").createDirectories()
    }

    fun saveFile(bytes: ByteArray, filename: String, type: String): String {
        require(bytes.size <= maxFileSize) { "File size exceeds maximum limit" }
        
        val extension = filename.substringAfterLast('.', "")
        val newFilename = "${UUID.randomUUID()}.$extension"
        val path = "$uploadDir/$type/$newFilename"
        
        File(path).writeBytes(bytes)
        return "/files/$type/$newFilename"
    }

    fun deleteFile(path: String) {
        val file = File(path.removePrefix("/files/"))
        if (file.exists() && !file.isDirectory) {
            file.delete()
        }
    }
}

