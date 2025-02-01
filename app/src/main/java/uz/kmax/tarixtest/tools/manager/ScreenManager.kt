package uz.kmax.tarixtest.tools.manager

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController

object ScreenManager {

    fun enableFullscreen(window: Window, statusBarColor: Int = 0, navigationBarColor: Int = 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+ uchun zamonaviy WindowInsetsController
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            @Suppress("DEPRECATION")
            window.statusBarColor = statusBarColor
            @Suppress("DEPRECATION")
            window.navigationBarColor = navigationBarColor
        } else {
            // API 24-29 uchun eski usul
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
            @Suppress("DEPRECATION")
            window.statusBarColor = statusBarColor
            @Suppress("DEPRECATION")
            window.navigationBarColor = navigationBarColor
        }
    }

    fun disableFullscreen(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+ uchun zamonaviy WindowInsetsController
            val controller = window.insetsController
            controller?.show(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
        } else {
            // API 24-29 uchun eski usul
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
}
