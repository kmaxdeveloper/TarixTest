package uz.kmax.tarixtest.tools.tools

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

class FindFileFromDevice {
    fun findPdfFile(fileName: String): String {
        val directories = listOf(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "TarixTest/Kitoblar")
        )

        for (dir in directories) {
            val file = File(dir, fileName)
            if (file.exists()) {
                return file.absolutePath
            }
        }
        return ""
    }

    fun Context.findFileInSecureDir(folderName: String, fileName: String): File? {
        val secureDir = File(filesDir, folderName) // 📂 /data/data/{package_name}/files/TarixTest/

        if (!secureDir.exists()) return null // 📌 Papka mavjudligini tekshiramiz

        val file = File(secureDir, fileName)
        return if (file.exists()) file else null // 🔍 Agar fayl mavjud bo‘lsa, uni qaytaramiz
    }

    fun findPdfFile(context: Context, fileName: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 📌 API 29+ (Android 10 va undan yuqori) uchun MediaStore orqali topish
            getPdfFilePathFromMediaStore(context, fileName)
        } else {
            // 📌 API 24-28 (Android 7-9) uchun Environment orqali topish
            getPdfFilePathLegacy(fileName)
        }
    }

    // 📌 API 24-28 uchun `Downloads/TarixTest/Kitoblar` da qidirish
    private fun getPdfFilePathLegacy(fileName: String): String {
        val directories = listOf(
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "TarixTest/Kitoblar")
        )

        for (dir in directories) {
            val file = File(dir, fileName)
            if (file.exists()) {
                return file.absolutePath
            }
        }
        return ""
    }

    // 📌 API 29+ (Android 10+) uchun MediaStore orqali PDF faylni qidirish
    @SuppressLint("NewApi")
    private fun getPdfFilePathFromMediaStore(context: Context, fileName: String): String {
        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME)
        val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        context.contentResolver.query(collection, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
                val id = cursor.getLong(idColumn)
                return MediaStore.Downloads.EXTERNAL_CONTENT_URI.buildUpon().appendPath(id.toString()).toString()
            }
        }
        return ""
    }

}