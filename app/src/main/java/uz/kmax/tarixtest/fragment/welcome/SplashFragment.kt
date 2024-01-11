package uz.kmax.tarixtest.fragment.welcome

import android.graphics.Color
import android.os.CountDownTimer
import com.google.android.material.snackbar.Snackbar
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
import uz.kmax.tarixtest.fragment.main.MenuFragment
import uz.kmax.tarixtest.tools.other.SharedPref
import uz.kmax.tarixtest.tools.manager.UpdateManager
import uz.kmax.tarixtest.tools.other.getAppVersion


class SplashFragment : BaseFragmentWC<FragmentSplashBinding>(FragmentSplashBinding::inflate) {
    var connectionDialog = DialogConnection()
    private val db = Firebase.database
    var updateLevel: Int = 0
    lateinit var update: UpdateManager
    lateinit var shared: SharedPref

    override fun onViewCreated() {
        update = UpdateManager(requireContext(),requireActivity())
        update.init(requireContext())
        shared = SharedPref(requireContext())
        checkUpdate()
        update.setNotUpdateListener {
            startApp()
        }
        update.setUpdateDismissListener {
            activity?.finish()
        }

        update.setOnFlexibleUpdateListener {
            Snackbar.make(
                requireActivity().findViewById(R.id.splashScreen),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction("RESTART") { update.updateNow() }
                setActionTextColor(Color.WHITE)
                show()
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
                            update.update( 1)
                        } else {
                            update.update(2)
                            shared.setUpdateStatus(true)
                        }
                    } else {
                        startApp()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    update.update(1)
                }
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

            override fun onTick(value: Long) {

            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        update.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        update.onDestroy()
    }
}