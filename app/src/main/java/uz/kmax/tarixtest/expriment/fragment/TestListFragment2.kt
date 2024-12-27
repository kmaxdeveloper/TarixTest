package uz.kmax.tarixtest.expriment.fragment

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.adapter.MenuAdapter
import uz.kmax.tarixtest.data.CheckUpdateData
import uz.kmax.tarixtest.tools.manager.AdsManager
import uz.kmax.tarixtest.databinding.FragmentTestListBinding
import uz.kmax.tarixtest.dialog.DialogConnection
import uz.kmax.tarixtest.expriment.viewModels.MenuViewModel
import uz.kmax.tarixtest.fragment.main.TestFragment
import uz.kmax.tarixtest.fragment.main.content.DayHistoryFragment
import uz.kmax.tarixtest.fragment.other.MessageFragment
import uz.kmax.tarixtest.fragment.other.UpdateFragment
import uz.kmax.tarixtest.tools.manager.ConnectionManager
import uz.kmax.tarixtest.tools.manager.UpdateManager
import uz.kmax.tarixtest.tools.filter.DataFilter
import uz.kmax.tarixtest.tools.filter.TypeFilter
import uz.kmax.tarixtest.tools.other.SharedPref
import uz.kmax.tarixtest.tools.other.getAppVersion
import java.util.Date

class TestListFragment2 : BaseFragmentWC<FragmentTestListBinding>(FragmentTestListBinding::inflate) {
    val adapter by lazy { MenuAdapter() }
    private val db = Firebase.database
    private var connectionDialog = DialogConnection()
    private var adsManager = AdsManager()
    private lateinit var shared: SharedPref
    lateinit var update: UpdateManager
    private var adsStatus = false
    private var updateLevel: Int = 0
    private var date: String = ""
    private lateinit var viewmodel: MenuViewModel

    override fun onViewCreated() {
        viewmodel = ViewModelProvider(requireActivity())[MenuViewModel::class.java]
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.appTheme)
        shared = SharedPref(requireContext())
        update = UpdateManager(requireContext())
        update.init(requireContext())

        adsManager.initialize(requireContext())

        //
        val currentDayDate: String = SimpleDateFormat("dd").format(Date())
        val currentMonthDate: String = SimpleDateFormat("MM").format(Date())
        val currentYearDate: String = SimpleDateFormat("yyyy").format(Date())

        date = "${currentDayDate}.${currentMonthDate}.${currentYearDate}"
        //
        addTestData()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        adapter.setOnTestItemClickListener { testLocation, testCount ->
            if (ConnectionManager().check(requireContext())) {
                ads(testLocation, testCount)
            } else {
                connectionDialog.show(requireContext())
                connectionDialog.setOnCloseListener {
                    activity?.finish()
                }
                connectionDialog.setOnTryAgainListener {
                    if (ConnectionManager().check(requireContext())) {
                        ads(testLocation, testCount)
                    } else {
                        connectionDialog.show(requireContext())
                    }
                }
            }
        }
        adapter.setOnTypeClickListener { it, location ->
            if (ConnectionManager().check(requireContext())) {
                ads(it, location)
            } else {
                connectionDialog.show(requireContext())
                connectionDialog.setOnCloseListener {
                    activity?.finish()
                }
                connectionDialog.setOnTryAgainListener {
                    if (ConnectionManager().check(requireContext())) {
                        ads(it, location)
                    } else {
                        connectionDialog.show(requireContext())
                    }
                }
            }
        }
    }

    private fun addTestData() {
        viewmodel.getFirebaseData("TarixTest/AllTest/${getString(R.string.language)}",0)
//        viewmodel.getFirebaseData("TarixTest/AllTest/uz/",0)
//        viewmodel.list.observe(viewLifecycleOwner){ data->
//            if (data != null){
//                adapter.setData(data)
//            }
//        }

//        viewmodel.list.observe(viewLifecycleOwner, Observer { data ->
//
//            adapter.setData(data)
//        })

        viewmodel.list.observeForever {
            adapter.setData(it)
        }
    }

    override fun onResume() {
        super.onResume()
        adsManager.initializeInterstitialAds(
            requireContext(),
            getString(R.string.interstitialAdsUnitId)
        )
        adsManager.setOnAdsNullListener {
            adsStatus = it
        }
    }

    private fun ads(testLocation: String, testCount: Int) {
        if (adsStatus) {
            adsManager.showInterstitialAds(requireActivity())
            adsManager.setOnAdsNotReadyListener {
                replaceFragment(TestFragment(testLocation, testCount))
            }
            adsManager.setOnAdDismissClickListener {
                replaceFragment(
                    TestFragment(
                        testLocation,
                        testCount
                    )
                )
            }
            adsManager.setOnAdsClickListener {
                toast("Thanks ! for clicking ads :D")
            }
        } else {
            replaceFragment(TestFragment(testLocation, testCount))
        }
    }

    private fun ads(type: Int, location: String) {
        if (adsStatus) {
            adsManager.showInterstitialAds(requireActivity())
            adsManager.setOnAdsNotReadyListener {
                replace(type, location)
            }
            adsManager.setOnAdDismissClickListener {
                replace(type, location)
            }
            adsManager.setOnAdsClickListener {
                toast("Thanks ! for clicking ads :D")
            }
        } else {
            replace(type, location)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun replace(type: Int, location: String) {
        when (type) {
            1 -> {
                replaceFragment(DayHistoryFragment())
            }

            2 -> {
                replaceFragment(MessageFragment(location))
            }

            3 -> {
                replaceFragment(UpdateFragment(location))
            }
        }
    }

    fun checking() {
        update.setNotUpdateListener {
            toast("Not Update !")
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
                    Toast.makeText(requireContext(), "Data is loaded !", Toast.LENGTH_SHORT).show()
                    val status = allUpdatesList[allUpdatesList.size - 1]
                    val currentAppVersion: Long = getAppVersion(requireContext())!!.versionNumber
                    if (currentAppVersion < status.versionCode) {
                        updateLevel = status.updateLevel
                        if (status.updateLevel >= 4) {
                            Toast.makeText(requireContext(), "Update 1", Toast.LENGTH_SHORT).show()
                            update.update(1)
                        } else {
                            Toast.makeText(requireContext(), "Update 2", Toast.LENGTH_SHORT).show()
                            update.update(2)
                            shared.setUpdateStatus(true)
                        }
                    } else {
                        toast("NOT UPDATE YET !")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    toast("Not UPD")
                    update.update(1)
                }
            })
    }
}