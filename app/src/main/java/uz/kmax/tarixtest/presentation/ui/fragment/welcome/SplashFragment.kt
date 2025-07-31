package uz.kmax.tarixtest.presentation.ui.fragment.welcome

import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.widget.Toast
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.data.tools.manager.ConnectionManager
import uz.kmax.tarixtest.data.tools.manager.NetworkMonitor
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.data.tools.tools.getAppVersion
import uz.kmax.tarixtest.domain.models.tool.CheckUpdateData
import uz.kmax.tarixtest.databinding.FragmentSplashBinding
import uz.kmax.tarixtest.presentation.ui.dialog.DialogConnection
import uz.kmax.tarixtest.presentation.ui.dialog.DialogUpdate
import uz.kmax.tarixtest.presentation.ui.fragment.main.MenuFragment

class SplashFragment : BaseFragmentWC<FragmentSplashBinding>(FragmentSplashBinding::inflate) {
    private var connectionDialog = DialogConnection()
    private lateinit var shared: SharedPref
    private var updateDialog = DialogUpdate()
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var firebaseManager: FirebaseManager
    private var updateInfo : Boolean = true

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        shared = SharedPref(requireContext())
        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.color_app)

        updateDialog.setOnUpdateNowListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=uz.kmax.tarixtest")
                setPackage("com.android.vending")
            }
            startActivity(intent)
            activity?.finish()
        }

        updateDialog.setOnExitListener { activity?.finish() }
        progress()
    }

    private fun progress(){
        if (ConnectionManager().check(requireContext())) {
            checkUpdate()
        } else {
            Toast.makeText(requireContext(), "No connection !", Toast.LENGTH_SHORT).show()
            connectionDialog.show(requireContext())
            connectionDialog.setOnCloseListener {
                activity?.finish()
            }
            connectionDialog.setOnTryAgainListener {
                if (ConnectionManager().check(requireContext())) {
                    checkUpdate()
                } else {
                    connectionDialog.show(requireContext())
                }
            }
        }
    }

    private fun checkUpdate(){
        firebaseManager.observeList("Update/AppUpdate", CheckUpdateData::class.java) { list ->
            if (list != null) {
                val currentAppVersion: Long = getAppVersion(requireContext())!!.versionNumber
                for (i in 0 until list.size){
                    if (currentAppVersion < list[i].versionCode) {
                        if (list[i].updateLevel >= 4) {
                            updateInfo = false
                            updateDialog.show(requireContext())
                        } else {
                            startApp()
                            shared.setUpdateStatus(true)
                        }
                    }
                }
                if (updateInfo){
                    startApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        progress()
    }

    private fun startApp() {
        object : CountDownTimer(3000, 100) {
            override fun onFinish() {
                if (ConnectionManager().check(requireContext())) {
                    startMainFragment(MenuFragment())
                } else {
                    connectionDialog.show(requireContext())
                    connectionDialog.setOnCloseListener {
                        activity?.finish()
                    }
                    connectionDialog.setOnTryAgainListener {
                        if (ConnectionManager().check(requireContext())) {
                            startMainFragment(MenuFragment())
                        } else {
                            connectionDialog.show(requireContext())
                        }
                    }
                }
            }
            override fun onTick(value: Long) {}
        }.start()
    }

    override fun onStart() {
        super.onStart()
        networkMonitor = NetworkMonitor(requireActivity().application)
        networkMonitor.observe(requireActivity()){
            if (!it){
                connectionDialog.show(requireContext())
                connectionDialog.setOnCloseListener {
                    activity?.finish()
                }
                connectionDialog.setOnTryAgainListener {
                    if (ConnectionManager().check(requireContext())) {
                        startMainFragment(MenuFragment())
                    } else {
                        connectionDialog.show(requireContext())
                    }
                }
            }
        }
    }
}