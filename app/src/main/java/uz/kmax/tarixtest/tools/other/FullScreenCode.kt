package uz.kmax.tarixtest.tools.other

import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class FullScreenCode {

    fun init(window : Window){
            val windowInsetsController =
                WindowCompat.getInsetsController(window, window.decorView)

            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

//            ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
//                if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
//                    || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())) {
//                    binding.toggleFullscreenButton.setOnClickListener {
//                        // Hide both the status bar and the navigation bar.
//                        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
//                    }
//                } else {
//                    binding.toggleFullscreenButton.setOnClickListener {
//                        // Show both the status bar and the navigation bar.
//                        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
//                    }
//                }
//                ViewCompat.onApplyWindowInsets(view, windowInsets)
//            }
        }

}