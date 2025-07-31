package uz.kmax.tarixtest.data.tools.tools

import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability

class UpdateManager(private val context: Context,private var activity: FragmentActivity) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)

    companion object {
        const val REQUEST_CODE_UPDATE = 1234
    }

    /**
     * Yangilanish mavjudligini tekshiradi va ko'rsatilgan turga muvofiq yangilashni boshlaydi
     * @param updateType AppUpdateType.FLEXIBLE yoki AppUpdateType.IMMEDIATE
     */
    fun checkForUpdate(updateType: Int, updateInfo: (Boolean) -> Unit) {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (isUpdateAvailable(appUpdateInfo, updateType)) {
                updateInfo(true)
                startUpdateFlow(appUpdateInfo, updateType)
            } else {
                updateInfo(false)
                Log.d("UpdateHelper", "No update available or not allowed for the specified type.")
            }
        }.addOnFailureListener {
            updateInfo(false)
            Log.e("UpdateHelper", "Failed to check for updates: ${it.message}")
        }
    }

    /**
     * Yangilanish mavjudligini tekshiradi va turini tasdiqlaydi
     */
    private fun isUpdateAvailable(appUpdateInfo: AppUpdateInfo, updateType: Int): Boolean {
        return appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(updateType)
    }

    /**
     * Yangilanish jarayonini boshlaydi
     */
    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo, updateType: Int) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateType,
                activity,
                REQUEST_CODE_UPDATE
            )
        } catch (e: IntentSender.SendIntentException) {
            Log.e("UpdateHelper", "Failed to start update flow: ${e.message}")
        } catch (e: Exception) {
            Log.e("UpdateHelper", "An unexpected error occurred: ${e.message}")
        }
    }
}



