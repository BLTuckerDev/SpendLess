package dev.bltucker.spendless.common

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import java.io.File
import java.io.IOException
import java.io.OutputStream

/**
 * Utility class for handling file operations in a way that's compatible
 * with various Android API levels.
 */
object FileUtils {

    /**
     * Saves a file to the Downloads directory in a way that's compatible across
     * API levels.
     *
     * @param context Application context
     * @param fileName Name of the file to save
     * @param mimeType MIME type of the file (e.g., "application/pdf", "text/csv")
     * @param fileWriter Function that writes to the provided OutputStream
     * @return The file that was saved (either direct file or temp file depending on API level)
     */
    @SuppressLint("MissingPermission")
    fun saveFileToDownloads(
        context: Context,
        fileName: String,
        mimeType: String,
        fileWriter: (OutputStream) -> Unit
    ): File {
        // For Android 10+ (API 29+), use MediaStore
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return saveFileUsingMediaStore(context, fileName, mimeType, fileWriter)
        }
        // For older versions, use direct file access (requires WRITE_EXTERNAL_STORAGE permission)
        else {
            return saveFileDirectly(fileName, fileWriter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileUsingMediaStore(
        context: Context,
        fileName: String,
        mimeType: String,
        fileWriter: (OutputStream) -> Unit
    ): File {
        val tempFile = File(context.cacheDir, fileName)

        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val contentResolver = context.contentResolver
        val contentUri = contentResolver.insert(
            android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: throw IOException("Failed to create new MediaStore record")

        contentResolver.openOutputStream(contentUri)?.use { outputStream ->
            fileWriter(outputStream)
        } ?: throw IOException("Failed to open output stream")

        return tempFile
    }

    @RequiresPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun saveFileDirectly(
        fileName: String,
        fileWriter: (OutputStream) -> Unit
    ): File {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val file = File(downloadsDir, fileName)

        file.outputStream().use { outputStream ->
            fileWriter(outputStream)
        }

        return file
    }
}