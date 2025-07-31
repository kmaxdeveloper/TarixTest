package uz.kmax.tarixtest.presentation.ui.fragment.main

import android.graphics.Color
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.adapter.TestListAdapter
import uz.kmax.tarixtest.data.tools.filter.Filter
import uz.kmax.tarixtest.data.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.data.ads.AdmobManager
import uz.kmax.tarixtest.data.ads.AdsManager
import uz.kmax.tarixtest.data.tools.manager.ConnectionManager
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.domain.models.main.MenuTestData
import uz.kmax.tarixtest.databinding.FragmentTestListBinding
import uz.kmax.tarixtest.presentation.ui.dialog.DialogConnection
import uz.kmax.tarixtest.presentation.ui.dialog.DialogTestTypeSelection
import uz.kmax.tarixtest.presentation.ui.fragment.main.test.HeartTestFragment
import uz.kmax.tarixtest.presentation.ui.fragment.main.test.TimerTestFragment
import javax.inject.Inject

@AndroidEntryPoint
class TestListFragment : BaseFragmentWC<FragmentTestListBinding>(FragmentTestListBinding::inflate) {
    private val adapter by lazy { TestListAdapter() }
    private lateinit var firebaseManager: FirebaseManager
    private var connectionDialog = DialogConnection()
    private var dataFilter = Filter()
    private var language = "uz"
    private var testTypeFilter = 0

    @Inject
    lateinit var adsManager: AdsManager

    @Inject
    lateinit var shared: SharedPref

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        language = shared.getLanguage().toString()
        testTypeFilter = shared.getTestType()

        /** Init Ads */
        adsManager.init()
        /** Init Ads */

        getTestListData()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        adapter.setOnTaskListener { testCount, testLocation ->
            adapter.setOnItemSendListener { data->
                if (ConnectionManager().check(requireContext())) {
                    checkLocation(testLocation, testCount,data.testType)
                } else {
                    connectionDialog.show(requireContext())
                    connectionDialog.setOnCloseListener {
                        activity?.finish()
                    }
                    connectionDialog.setOnTryAgainListener {
                        if (ConnectionManager().check(requireContext())) {
                            checkLocation(testLocation, testCount, data.testType)
                        } else {
                            connectionDialog.show(requireContext())
                        }
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

    private fun ads(testLocation: String, testCount: Int, testType: Int) {
        adsManager.showAds(requireActivity()){
            startTestFragment(testType,testLocation,testCount)
        }

        adsManager.setOnAdClickListener {
            Toast.makeText(requireContext(), "Thank You Bro !", Toast.LENGTH_SHORT).show()
        }

        adsManager.setOnAdDismissListener {
            startTestFragment(testType,testLocation,testCount)
        }
    }

    private fun startTestFragment(testType: Int, testLocation: String, testCount: Int){
        when(testTypeFilter){
            1->{
                startTest(testType,testLocation,testCount)
            }
            3->{
                replaceFragment(HeartTestFragment(testLocation,testCount))
            }

            else -> {
                Toast.makeText(requireContext(), "Dasturda Texnik nosozlik bo'ldi !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocation(testLocation: String, testCount: Int, testType : Int){
        firebaseManager.observeListVisibly("Test/$language/$testLocation"){ status->
            if (status){
                when(testType){
                    1->{
                        ads(testLocation,testCount, testType)
                    }

                    3->{
                        ads(testLocation,testCount, testType)
                    }
                    else -> {
                        Snackbar.make(binding.recyclerView, "Bu test dastur versiyasiga mos emas ! \n Dasturni yangilang", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.WHITE)
                            .setTextColor(Color.BLACK)
                            .show()
                    }
                }
            }else{
                Snackbar.make(binding.recyclerView, "Texnik ishlar olib borilmoqda !", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.WHITE)
                    .setTextColor(Color.BLACK)
                    .show()
            }
        }
    }

    private fun startTest(type: Int,testLocation: String,testCount: Int){
        when(type){
            1->{
                startMainFragment(TimerTestFragment(testLocation,testCount))
            }

            3->{
                startMainFragment(HeartTestFragment(testLocation,testCount))
            }
        }
    }
}