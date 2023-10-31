package uz.kmax.tarixtest.fragment

import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.adapter.TestMenuAdapter
import uz.kmax.tarixtest.ads.GoogleAds
import uz.kmax.tarixtest.data.MenuTestData
import uz.kmax.tarixtest.databinding.FragmentMenuBinding
import uz.kmax.tarixtest.dialog.DialogConnection
import uz.kmax.tarixtest.tools.Connection


class MenuFragment : BaseFragmentWC<FragmentMenuBinding>(FragmentMenuBinding::inflate) {
    val adapter by lazy { TestMenuAdapter() }
    private val db = Firebase.database
    private var connectionDialog = DialogConnection()
    private var googleAds = GoogleAds()
    private lateinit var toggleBar: ActionBarDrawerToggle
    private var adsStatus = false

    override fun onViewCreated() {
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.blue)

        // DrawerLayout Code --
        toggleBar = ActionBarDrawerToggle(requireActivity(),binding.drawerLayout,binding.toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggleBar)
        toggleBar.syncState()
        // DrawerLayout Code --

        googleAds.initialize(requireContext())
        addTestData()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        adapter.setOnTestItemClickListener {testLocation,testCount->
            if (Connection().check(requireContext())) {
                ads(testLocation,testCount)
            } else {
                connectionDialog.show(requireContext())
                connectionDialog.setOnCloseListener {
                    activity?.finish()
                }
                connectionDialog.setOnTryAgainListener {
                    if (Connection().check(requireContext())) {
                        ads(testLocation,testCount)
                    } else {
                        connectionDialog.show(requireContext())
                    }
                }
            }
        }
        adapter.setOnDayHistoryClickListener {
            if (Connection().check(requireContext())) {
                ads()
            } else {
                connectionDialog.show(requireContext())
                connectionDialog.setOnCloseListener {
                    activity?.finish()
                }
                connectionDialog.setOnTryAgainListener {
                    if (Connection().check(requireContext())) {
                        ads()
                    } else {
                        connectionDialog.show(requireContext())
                    }
                }
            }
        }

        // Navigation View Code --
        binding.navigationMenu.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            val id = item.itemId
            when (id) {
                R.id.homePage ->{
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
                                if (result.isCanceled){
                                    Toast.makeText(requireContext(), "Dasturni baholash bekor qilindi !", Toast.LENGTH_SHORT).show()
                                }else if (result.isSuccessful) {
                                    Toast.makeText(requireContext(), "Dastur baholandi !!!", Toast.LENGTH_SHORT).show()
                                }else if (result.isComplete){
                                    
                                    Toast.makeText(requireContext(), "Baholash tugatildi !", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // There was some problem, log or handle the error code.
                            @ReviewErrorCode val reviewErrorCode = (task.exception as ReviewException).errorCode
                        }
                    }
                    closeDrawer()
                    binding.drawerLayout.isSelected = false
                }

                R.id.devConnection -> {
                    startMainFragment(AdminConnectionFragment())
                    closeDrawer()
                }

                R.id.privacyPolicy ->{
                    startMainFragment(PrivacyPolicyFragment())
                    closeDrawer()
                }

                else -> return@OnNavigationItemSelectedListener true
            }
            true
        })
        // Navigation View Code --
    }

    fun closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START,true)
        }
    }

    private fun addTestData() {
        val menuTestDataArray = ArrayList<MenuTestData>()
        menuTestDataArray.add(MenuTestData(0, "", 1, "Kun Tarixi",1))
        db.getReference("TarixTest").child("AllTest")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEachIndexed { _, data ->
                        val h = data.getValue(MenuTestData::class.java)
                        h?.let {
                            if (it.testVisibility == 1){
                                menuTestDataArray.add(
                                    MenuTestData(
                                        it.testCount, it.testLocation, it.testNewOld, it.testType,it.testVisibility
                                    )
                                )
                            }
                        }
                    }
                    adapter.setData(menuTestDataArray)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onResume() {
        super.onResume()
        googleAds.initializeInterstitialAds(requireContext(),getString(R.string.interstitialAdsUnitId))
        googleAds.setOnAdsNullListener {
            adsStatus = it
        }
    }

    private fun ads(testLocation : String,testCount : Int){
        if (adsStatus){
            googleAds.showInterstitialAds(requireActivity())
            googleAds.setOnAdsNotReadyListener {
                replaceFragment(TestFragment(testLocation,testCount))
            }
            googleAds.setOnAdDismissClickListener {
                replaceFragment(TestFragment(testLocation,testCount))
            }
            googleAds.setOnAdsClickListener {
                Toast.makeText(requireContext(), "Thanks ! for clicking ads :D", Toast.LENGTH_SHORT).show()
            }
        }else{
            replaceFragment(TestFragment(testLocation,testCount))
        }
    }

    private fun ads(){
        if (adsStatus){
            googleAds.showInterstitialAds(requireActivity())
            googleAds.setOnAdsNotReadyListener {
                replaceFragment(HistoryOfDayFragment())
            }
            googleAds.setOnAdDismissClickListener {
                replaceFragment(HistoryOfDayFragment())
            }
            googleAds.setOnAdsClickListener {
                Toast.makeText(requireContext(), "Thanks ! for clicking ads :D", Toast.LENGTH_SHORT).show()
            }
        }else{
            replaceFragment(HistoryOfDayFragment())
        }
    }
}