package uz.kmax.tarixtest.data.tools.tools

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log

object GetAbsPath {
    fun getAbsolutePathFromUri(context: Context, fileUri: Uri): String {
        var filePath: String = ""
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)

        context.contentResolver.query(fileUri, projection, null, null, null)?.use { cursor: Cursor? ->
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                filePath = cursor.getString(columnIndex)
            }
        }

        if (filePath.isEmpty()) {
            Log.e("FilePath", "Absolute path topilmadi")
        } else {
            Log.d("FilePath", "Absolute Path: $filePath")
        }
        return filePath
    }
}