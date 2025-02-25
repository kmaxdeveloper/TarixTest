package uz.kmax.tarixtest.fragment.main

import android.graphics.Color
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.adapter.TestListAdapter
import uz.kmax.tarixtest.data.main.MenuTestData
import uz.kmax.tarixtest.databinding.FragmentTestListBinding
import uz.kmax.tarixtest.dialog.DialogConnection
import uz.kmax.tarixtest.tools.manager.ConnectionManager
import uz.kmax.tarixtest.tools.filter.Filter
import uz.kmax.tarixtest.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.tools.manager.AdmobManager
import uz.kmax.tarixtest.tools.manager.AdsManager
import uz.kmax.tarixtest.tools.tools.SharedPref

class TestListFragment : BaseFragmentWC<FragmentTestListBinding>(FragmentTestListBinding::inflate) {
    private val adapter by lazy { TestListAdapter() }
    private lateinit var firebaseManager: FirebaseManager
    private var connectionDialog = DialogConnection()
    private lateinit var admobManager: AdmobManager
    private lateinit var shared: SharedPref
    private var dataFilter = Filter()
    private var adsStatus = false
    private var language = "uz"

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        shared = SharedPref(requireContext())
        admobManager = AdmobManager(requireContext())
        language = shared.getLanguage().toString()
        admobManager.initialize(getString(R.string.interstitialAdsUnitId))

        admobManager.setOnAdLoadListener {
            adsStatus = it
        }

        getTestListData()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        adapter.setOnTaskListener { testCount, testLocation ->
            if (ConnectionManager().check(requireContext())) {
                checkLocation(testLocation, testCount)
            } else {
                connectionDialog.show(requireContext())
                connectionDialog.setOnCloseListener {
                    activity?.finish()
                }
                connectionDialog.setOnTryAgainListener {
                    if (ConnectionManager().check(requireContext())) {
                        checkLocation(testLocation, testCount)
                    } else {
                        connectionDialog.show(requireContext())
                    }
                }
            }
        }
    }

    private fun getTestListData() {
        firebaseManager.observeList("AllTest/$language", MenuTestData::class.java){
            if (it != null) {
                adapter.setItems(dataFilter.filterTest(it))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        admobManager.setOnAdLoadListener {
            adsStatus = it
        }
    }

    private fun ads(testLocation: String, testCount: Int) {
        if (adsStatus || admobManager.isAdReady()) {
            admobManager.showInterstitialAd(requireActivity()){
                replaceFragment(TestFragment(testLocation, testCount))
            }
            admobManager.setOnAdDismissListener {
                replaceFragment(TestFragment(testLocation, testCount))
            }
            admobManager.setOnAdClickListener {
                Toast.makeText(requireContext(), "Thanks ! for clicking ads :D", Toast.LENGTH_SHORT).show()
            }
        } else {
            replaceFragment(TestFragment(testLocation, testCount))
        }
    }

    override fun onStart() {
        super.onStart()
        admobManager.setOnAdLoadListener {
            adsStatus = it
        }
    }

    private fun checkLocation(testLocation: String, testCount: Int){
        firebaseManager.observeListVisibly("Test/$language/$testLocation"){ status->
            if (status){
                ads(testLocation,testCount)
            }else{
                Snackbar.make(binding.recyclerView, "Texnik ishlar olib borilmoqda !", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.WHITE)
                    .setTextColor(Color.BLACK)
                    .show()
            }
        }
    }
}