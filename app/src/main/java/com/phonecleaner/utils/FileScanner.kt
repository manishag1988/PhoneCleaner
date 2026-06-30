package com.phonecleaner.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import com.phonecleaner.model.MediaFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileScanner(private val contentResolver: ContentResolver) {

    suspend fun scanAllFiles(): List<MediaFile> = withContext(Dispatchers.IO) {
        val files = mutableListOf<MediaFile>()
        files.addAll(queryImages())
        files.addAll(queryVideos())
        files.sortByDescending { it.dateAdded }
        files
    }

    suspend fun scanGifs(): List<MediaFile> = withContext(Dispatchers.IO) {
        val gifs = mutableListOf<MediaFile>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.RELATIVE_PATH
        )
        val selection = "(${MediaStore.Images.Media.MIME_TYPE} = ? OR ${MediaStore.Images.Media.MIME_TYPE} = ?) AND (${MediaStore.Images.Media.RELATIVE_PATH} LIKE ? OR ${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?)"
        val selectionArgs = arrayOf("image/gif", "image/apng", "Download/%", "Downloads/%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val relativePathCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val relativePath = cursor.getString(relativePathCol)
                if (!isDownloadedPath(relativePath)) continue
                gifs.add(
                    MediaFile(
                        id = id,
                        uri = uri,
                        displayName = cursor.getString(nameCol) ?: "Unknown",
                        size = cursor.getLong(sizeCol),
                        mimeType = cursor.getString(mimeCol) ?: "image/gif",
                        dateAdded = cursor.getLong(dateCol),
                        isGif = true
                    )
                )
            }
        }
        gifs
    }

    private fun queryImages(): List<MediaFile> {
        val images = mutableListOf<MediaFile>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.RELATIVE_PATH
        )
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ? OR ${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("Download/%", "Downloads/%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val relativePathCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val mimeType = cursor.getString(mimeCol) ?: "image/jpeg"
                val relativePath = cursor.getString(relativePathCol)
                if (!isDownloadedPath(relativePath)) continue
                images.add(
                    MediaFile(
                        id = id,
                        uri = uri,
                        displayName = cursor.getString(nameCol) ?: "Unknown",
                        size = cursor.getLong(sizeCol),
                        mimeType = mimeType,
                        dateAdded = cursor.getLong(dateCol),
                        isGif = mimeType.equals("image/gif", ignoreCase = true) || mimeType.equals("image/apng", ignoreCase = true)
                    )
                )
            }
        }
        return images
    }

    private fun queryVideos(): List<MediaFile> {
        val videos = mutableListOf<MediaFile>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.RELATIVE_PATH
        )
        val selection = "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ? OR ${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("Download/%", "Downloads/%")
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val relativePathCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                val relativePath = cursor.getString(relativePathCol)
                if (!isDownloadedPath(relativePath)) continue
                videos.add(
                    MediaFile(
                        id = id,
                        uri = uri,
                        displayName = cursor.getString(nameCol) ?: "Unknown",
                        size = cursor.getLong(sizeCol),
                        mimeType = cursor.getString(mimeCol) ?: "video/mp4",
                        dateAdded = cursor.getLong(dateCol),
                        isGif = false
                    )
                )
            }
        }
        return videos
    }

    private fun isDownloadedPath(relativePath: String?): Boolean {
        if (relativePath.isNullOrBlank()) return false
        val normalized = relativePath.trim().replace('\\', '/')
        return normalized.startsWith("Download/") || normalized.startsWith("Downloads/")
    }
}
