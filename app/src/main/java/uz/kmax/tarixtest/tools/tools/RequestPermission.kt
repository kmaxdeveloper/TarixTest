package uz.kmax.tarixtest.tools.tools

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat

object RequestPermission {
    fun requestStoragePermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ uchun MANAGE_EXTERNAL_STORAGE ruxsatini so‘rash
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), 100)
        } else {
            // Android 10 va undan past versiyalar uchun
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }

    fun requestStoragePermissionExtend(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+) uchun ruxsat so‘rash
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivity(intent)
            }
        } else {
            // Android 10 va undan past versiyalar uchun
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
        }
    }

}