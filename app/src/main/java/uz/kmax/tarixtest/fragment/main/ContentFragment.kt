package uz.kmax.tarixtest.fragment.main

import android.graphics.Color
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.adapter.ContentAdapter
import uz.kmax.tarixtest.data.main.MenuContentData
import uz.kmax.tarixtest.databinding.FragmentContentBinding
import uz.kmax.tarixtest.fragment.main.content.BookListFragment
import uz.kmax.tarixtest.fragment.main.content.DayHistoryFragment
import uz.kmax.tarixtest.tools.filter.Filter
import uz.kmax.tarixtest.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.tools.manager.AdmobManager
import uz.kmax.tarixtest.tools.manager.AdsManager
import uz.kmax.tarixtest.tools.tools.SharedPref

class ContentFragment : BaseFragmentWC<FragmentContentBinding>(FragmentContentBinding::inflate) {
    private val adapter by lazy { ContentAdapter() }
    private lateinit var firebaseManager: FirebaseManager
    private var filter = Filter()
    private lateinit var shared : SharedPref
    private var language = "uz"
    private lateinit var admobManager: AdmobManager
    private var adsStatus = false

    override fun onViewCreated() {
        admobManager = AdmobManager(requireContext())
        admobManager.initialize(getString(R.string.interstitialAdsUnitId))
        firebaseManager = FirebaseManager()

        shared = SharedPref(requireContext())
        language = shared.getLanguage().toString()
        admobManager.setOnAdLoadListener {
            adsStatus = it
        }

        getContentData()
        binding.contentRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.contentRecycleView.adapter = adapter

        adapter.setOnTaskListener { contentType, contentLocation ->
            ads(contentType,contentLocation)
        }
    }

    private fun getContentData() {
        firebaseManager.observeList("AllContent/$language", MenuContentData::class.java){
            if (it != null){
                adapter.setItems(filter.filterContent(it))
            }
        }
    }

    private fun ads(type: Int, contentLocation: String) {
        if (adsStatus || admobManager.isAdReady()) {
            admobManager.showInterstitialAd(requireActivity()){
                replace(type, contentLocation)
            }
            admobManager.setOnAdDismissListener {
                replace(type, contentLocation)
            }
            admobManager.setOnAdClickListener {
                Toast.makeText(requireContext(), "Thanks ! for clicking ads :D", Toast.LENGTH_SHORT).show()
            }
        } else {
            replace(type, contentLocation)
        }
    }

    private fun replace(type: Int, location : String) {
        when (type) {
            1 -> {
                replaceFragment(DayHistoryFragment())
            }
            2->{
                replaceFragment(BookListFragment())
            }
            else->{
                Snackbar.make(binding.contentRecycleView, getString(R.string.contentWarningInfo), Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.CYAN)
                    .setTextColor(Color.BLACK)
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        admobManager.setOnAdLoadListener {
            adsStatus = it
        }
    }
}