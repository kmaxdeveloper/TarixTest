package uz.kmax.tarixtest.fragment.welcome

import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.CheckUpdateData
import uz.kmax.tarixtest.tools.manager.ConnectionManager
import uz.kmax.tarixtest.databinding.FragmentSplashBinding
import uz.kmax.tarixtest.dialog.DialogConnection
import uz.kmax.tarixtest.dialog.DialogUpdate
import uz.kmax.tarixtest.fragment.main.MenuFragment
import uz.kmax.tarixtest.fragment.main.TestListFragment
import uz.kmax.tarixtest.tools.manager.NetworkMonitor
import uz.kmax.tarixtest.tools.other.SharedPref
import uz.kmax.tarixtest.tools.other.getAppVersion

class SplashFragment : BaseFragmentWC<FragmentSplashBinding>(FragmentSplashBinding::inflate) {
    var connectionDialog = DialogConnection()
    private val db = Firebase.database
    var updateLevel: Int = 0
    lateinit var shared: SharedPref
    var updateDialog = DialogUpdate()
    lateinit var networkMonitor: NetworkMonitor

    override fun onViewCreated() {

        shared = SharedPref(requireContext())
        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.appTheme)

        updateDialog.setOnUpdateNowListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=uz.kmax.tarixtest")
                setPackage("com.android.vending")
            }
            startActivity(intent)
            activity?.finish()
        }

        updateDialog.setOnExitListener {
            activity?.finish()
        }

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
                    startMainFragment(MenuFragment())
                } else {
                    connectionDialog.show(requireContext())
                }
            }
        }
    }

    private fun checkUpdate() {
        val allUpdatesList = ArrayList<CheckUpdateData>()
        db.getReference("App").child("TarixTest").child("Update")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEachIndexed { _, data ->
                        val h = data.getValue(CheckUpdateData::class.java)
                        h?.let {
                            allUpdatesList.add(
                                CheckUpdateData(
                                    it.updateLevel,
                                    it.versionCode,
                                    it.versionName
                                )
                            )
                        }
                    }
                    // yuklangandan so'ng
                    val status = allUpdatesList[allUpdatesList.size - 1]
                    val currentAppVersion: Long = getAppVersion(requireContext())!!.versionNumber
                    if (currentAppVersion < status.versionCode) {
                        updateLevel = status.updateLevel
                        if (status.updateLevel >= 4) {
                            updateDialog.show(requireContext())
                        } else {
                            startApp()
                            shared.setUpdateStatus(true)
                        }
                    } else {
                        startApp()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun startApp() {
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