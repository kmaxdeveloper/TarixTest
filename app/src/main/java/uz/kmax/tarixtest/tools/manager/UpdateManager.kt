package uz.kmax.tarixtest.tools.manager

import android.content.Context
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed

class UpdateManager(context: Context, fragmentActivity: FragmentActivity) {

    private var onNotUpdateListener: (() -> Unit)? = null
    fun setNotUpdateListener(listener: () -> Unit) { onNotUpdateListener = listener }
    private var onUpdateDismissListener: (() -> Unit)? = null
    fun setUpdateDismissListener(f: () -> Unit) { onUpdateDismissListener = f }
    private var onFlexibleUpdateListener :(() -> Unit)? = null
    fun setOnFlexibleUpdateListener(f :() -> Unit){ onFlexibleUpdateListener = f }
    private lateinit var appUpdateManager: AppUpdateManager
    private var updateTypeImmediate = AppUpdateType.IMMEDIATE
    private var updateTypeFlexible = AppUpdateType.FLEXIBLE
    private var type = 1

    fun init(context: Context) {
        appUpdateManager = AppUpdateManagerFactory.create(context)
    }

    fun update(upType: Int) {
        when (upType) {
            1 -> {
                type = 1
                checkImmediateUpdate()
            }

            2 -> {
                type = 2
                checkFlexibleUpdate()
                if (updateTypeImmediate == AppUpdateType.FLEXIBLE) {
                    appUpdateManager.registerListener(installUpdateListener)
                }
            }
        }
    }

    private fun checkImmediateUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateTypeImmediate) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }

            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info, activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            } else {
                onNotUpdateListener?.invoke()
            }
        }.addOnCanceledListener {
            onNotUpdateListener?.invoke()
        }
    }

    private fun checkFlexibleUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateTypeFlexible) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }

            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info, activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                )
            } else {
                onNotUpdateListener?.invoke()
            }
        }.addOnCanceledListener {
            onNotUpdateListener?.invoke()
        }
    }

    private val installUpdateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            onFlexibleUpdateListener?.invoke()
        } else {
            Toast.makeText(context, "NO Flexible Update!", Toast.LENGTH_SHORT).show()
            onNotUpdateListener?.invoke()
        }
    }

    fun onResume() {
        if (updateTypeImmediate == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        info, activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
            }
        }
    }

    private var activityResultLauncher = fragmentActivity.registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult ->
        if (result.resultCode != FragmentActivity.RESULT_OK) {
            transAction()
        } else if (result.resultCode == FragmentActivity.RESULT_CANCELED) {
            transAction()
        } else if (result.resultCode == FragmentActivity.RESULT_OK) {
            onNotUpdateListener?.invoke()
        } else {
            transAction()
        }
    }

    fun updateNow(){
        appUpdateManager.completeUpdate()
    }

    private fun transAction() {
        if (type == 1) {
            onUpdateDismissListener?.invoke()
        } else {
            onNotUpdateListener?.invoke()
        }
    }

    fun onDestroy() {
        if (updateTypeFlexible == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installUpdateListener)
        }
    }
}