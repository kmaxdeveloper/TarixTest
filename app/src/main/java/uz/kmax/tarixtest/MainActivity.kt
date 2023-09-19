package uz.kmax.tarixtest

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type.statusBars
import uz.kmax.base.fragmentcontroller.FragmentController
import uz.kmax.tarixtest.databinding.ActivityMainBinding
import uz.kmax.tarixtest.fragment.SplashFragment


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val window = this.window
            WindowCompat.setDecorFitsSystemWindows(window, false);
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        setContentView(binding.root)
        FragmentController.init(R.id.container, supportFragmentManager)
        FragmentController.controller?.startMainFragment(SplashFragment())
    }
}