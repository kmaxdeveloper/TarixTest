package uz.kmax.tarixtest.fragment

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onViewCreated() {
        googleAds.initialize(requireContext())
        googleAds.initializeInterstitialAds(requireContext(),getString(R.string.interstitialAdsUnitId))
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
    }

    private fun addTestData() {
        val menuTestDataArray = ArrayList<MenuTestData>()
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

    private fun ads(testLocation : String,testCount : Int){
        googleAds.showInterstitialAds(requireActivity())
        googleAds.setOnAdsNotReadyListener {
            startMainFragment(TestFragment(testLocation,testCount))
        }

        googleAds.setOnAdDismissClickListener {
            startMainFragment(TestFragment(testLocation,testCount))
        }
        googleAds.setOnAdsClickListener {
            Toast.makeText(requireContext(), "Thanks ! for clicking ads :D", Toast.LENGTH_SHORT).show()
        }
    }
}