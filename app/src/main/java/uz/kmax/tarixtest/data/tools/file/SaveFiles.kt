package uz.kmax.tarixtest.data.tools.file

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import uz.kmax.tarixtest.data.tools.tools.GetAbsPath
import java.io.File
import java.io.FileOutputStream

object SaveFiles {

    // 📌 API 29+ (Android 10+) uchun faylni `Downloads` ga saqlash
    @SuppressLint("NewApi")
    fun saveFileToDownloads(context: Context, fileName: String, sourceFile: File): String {
        val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/TarixTest/Kitoblar"

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, relativePath)
        }

        val resolver = context.contentResolver
        val fileUri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        fileUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                sourceFile.inputStream().copyTo(outputStream)
            }
            Log.d("MediaStore", "Fayl saqlandi: $uri")
            return uz.kmax.tarixtest.data.tools.tools.GetAbsPath.getAbsolutePathFromUri(context,uri)
        }
        return ""
    }

    // 📌 API 28- (Android 9 va eski) uchun faylni `Downloads` ga saqlash
    fun saveFileToDownloadsLegacy(fileName: String, sourceFile: File): String {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val customFolder = File(downloadsDir, "TarixTest/Kitoblar")

        if (!customFolder.exists()) {
            customFolder.mkdirs() // 📌 Papkani yaratish
        }
        val destinationFile = File(customFolder, fileName)
        sourceFile.inputStream().use { input ->
            FileOutputStream(destinationFile).use { output ->
                input.copyTo(output)
            }
        }
        Log.d("LegacySave", "Fayl saqlandi: ${destinationFile.absolutePath}")
        return destinationFile.absolutePath
    }
}