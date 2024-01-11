package uz.kmax.tarixtest

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import uz.kmax.base.fragmentcontroller.FragmentController
import uz.kmax.tarixtest.databinding.ActivityMainBinding
import uz.kmax.tarixtest.fragment.welcome.SplashFragment
import uz.kmax.tarixtest.fragment.welcome.WelcomeFragment
import uz.kmax.tarixtest.tools.other.SharedPref


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var shared: SharedPref
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
        shared = SharedPref(this)
        FragmentController.init(R.id.container, supportFragmentManager)
        if (shared.getWelcomeStatus()){
            FragmentController.controller?.startMainFragment(WelcomeFragment())
        }else {
            FragmentController.controller?.startMainFragment(SplashFragment())
        }
    }
}