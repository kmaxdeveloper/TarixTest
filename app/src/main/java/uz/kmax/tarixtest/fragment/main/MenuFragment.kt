package uz.kmax.tarixtest.fragment.main

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.adapter.MenuAdapter
import uz.kmax.tarixtest.data.CheckUpdateData
import uz.kmax.tarixtest.tools.manager.AdsManager
import uz.kmax.tarixtest.data.MenuTestData
import uz.kmax.tarixtest.databinding.FragmentMenuBinding
import uz.kmax.tarixtest.dialog.DialogConnection
import uz.kmax.tarixtest.fragment.other.PrivacyFragment
import uz.kmax.tarixtest.fragment.other.AdminFragment
import uz.kmax.tarixtest.fragment.other.MessageFragment
import uz.kmax.tarixtest.fragment.other.UpdateFragment
import uz.kmax.tarixtest.tools.manager.ConnectionManager
import uz.kmax.tarixtest.tools.manager.UpdateManager
import uz.kmax.tarixtest.tools.other.DataFilter
import uz.kmax.tarixtest.tools.other.SharedPref
import uz.kmax.tarixtest.tools.other.getAppVersion
import java.util.Date

class MenuFragment : BaseFragmentWC<FragmentMenuBinding>(FragmentMenuBinding::inflate) {
    val adapter by lazy { MenuAdapter() }
    private val db = Firebase.database
    private var connectionDialog = DialogConnection()
    private var adsManager = AdsManager()
    private lateinit var shared: SharedPref
    private lateinit var toggleBar: ActionBarDrawerToggle
    lateinit var update: UpdateManager
    private var dataFilter = DataFilter()
    private var adsStatus = false
    private var updateLevel: Int = 0
    private var date: String = ""

    override fun onViewCreated() {
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.appTheme)
        shared = SharedPref(requireContext())
        update = UpdateManager(requireContext())
        update.init(requireContext())


        // DrawerLayout Code --
        toggleBar = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggleBar)
        toggleBar.syncState()
        // DrawerLayout Code --

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
        adapter.setOnTypeClickListener {it,location->
            if (ConnectionManager().check(requireContext())) {
                ads(it,location)
            } else {
                connectionDialog.show(requireContext())
                connectionDialog.setOnCloseListener {
                    activity?.finish()
                }
                connectionDialog.setOnTryAgainListener {
                    if (ConnectionManager().check(requireContext())) {
                        ads(it,location)
                    } else {
                        connectionDialog.show(requireContext())
                    }
                }
            }
        }

        // Navigation View Code --
        binding.navigationMenu.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homePage -> {
                    closeDrawer()
                    binding.drawerLayout.isSelected = false
                }

                R.id.ratingApp -> {
                    val manager = ReviewManagerFactory.create(requireContext())
                    val request = manager.requestReviewFlow()
                    request.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val reviewInfo = task.result
                            val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                            flow.addOnCompleteListener { result ->
                                if (result.isCanceled) {
                                    toast("Dasturni baholash bekor qilindi !")
                                } else if (result.isSuccessful) {
                                    toast("Dastur baholandi !!!")
                                } else if (result.isComplete) {
                                    toast("Baholash tugatildi !")
                                }
                            }
                        } else {
                            @ReviewErrorCode val reviewErrorCode =
                                (task.exception as ReviewException).errorCode
                        }
                    }
                    closeDrawer()
                    binding.drawerLayout.isSelected = false
                }

                R.id.devConnection -> {
                    startMainFragment(AdminFragment())
                    closeDrawer()
                }

                R.id.privacyPolicy -> {
                    startMainFragment(PrivacyFragment())
                    closeDrawer()
                }

                else -> return@OnNavigationItemSelectedListener true
            }
            true
        })
        // Navigation View Code --
    }

    private fun closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START, true)
        }
    }

    private fun addTestData() {
        val updateStatus: Boolean = shared.getUpdateAvailable()
        val menuTestDataArray = ArrayList<MenuTestData>()
        db.getReference("TarixTest").child("AllTest").child("uz")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEachIndexed { _, data ->
                        val h = data.getValue(MenuTestData::class.java)
                        h?.let {
                            if (it.testVisibility == 1) {
                                if (it.testType == 3) {
                                    if (updateStatus) {
                                        menuTestDataArray.add(
                                            MenuTestData(
                                                it.testAnyWay,
                                                it.testCount,
                                                it.testLocation,
                                                it.testName,
                                                it.testNewOld,
                                                it.testType,
                                                it.testVisibility
                                            )
                                        )
                                    }
                                } else {
                                    menuTestDataArray.add(
                                        MenuTestData(
                                            it.testAnyWay,
                                            it.testCount,
                                            it.testLocation,
                                            it.testName,
                                            it.testNewOld,
                                            it.testType,
                                            it.testVisibility
                                        )
                                    )
                                }
                            }
                        }
                    }
                    adapter.setData(dataFilter.filter(menuTestDataArray))
                }

                override fun onCancelled(error: DatabaseError) {}
            })
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

    private fun ads(type: Int,location: String) {
        if (adsStatus) {
            adsManager.showInterstitialAds(requireActivity())
            adsManager.setOnAdsNotReadyListener {
                replace(type,location)
            }
            adsManager.setOnAdDismissClickListener {
                replace(type,location)
            }
            adsManager.setOnAdsClickListener {
                toast("Thanks ! for clicking ads :D")
            }
        } else {
            replace(type,location)
        }
    }

    private fun toast(message : String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun replace(type: Int,location : String) {
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