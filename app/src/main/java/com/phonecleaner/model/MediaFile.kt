package com.phonecleaner.model

import android.net.Uri

data class MediaFile(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val size: Long,
    val mimeType: String,
    val dateAdded: Long,
    val isGif: Boolean = false
) {
    fun getSizeString(): String {
        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$size B"
        }
    }
}
