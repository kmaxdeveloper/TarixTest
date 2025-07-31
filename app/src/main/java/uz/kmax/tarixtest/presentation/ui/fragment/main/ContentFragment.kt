package uz.kmax.tarixtest.presentation.ui.fragment.main

import android.graphics.Color
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.adapter.ContentAdapter
import uz.kmax.tarixtest.data.tools.filter.Filter
import uz.kmax.tarixtest.data.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.data.ads.AdmobManager
import uz.kmax.tarixtest.data.ads.AdsManager
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.data.tools.tools.onFragmentBackPressed
import uz.kmax.tarixtest.domain.models.main.MenuContentData
import uz.kmax.tarixtest.databinding.FragmentContentBinding
import uz.kmax.tarixtest.presentation.ui.fragment.main.content.BookListFragment
import uz.kmax.tarixtest.presentation.ui.fragment.main.content.DayHistoryFragment
import uz.kmax.tarixtest.presentation.ui.fragment.main.content.HistoricalPersonListFragment
import javax.inject.Inject

@AndroidEntryPoint
class ContentFragment : BaseFragmentWC<FragmentContentBinding>(FragmentContentBinding::inflate) {
    private val adapter by lazy { ContentAdapter() }
    private lateinit var firebaseManager: FirebaseManager
    private var filter = Filter()
    private lateinit var shared : SharedPref
    private var language = "uz"

    @Inject
    lateinit var adsManager: AdsManager

    override fun onViewCreated() {
        /** Ads init */
        adsManager.init()
        /** Ads init */

        firebaseManager = FirebaseManager()

        shared = SharedPref(requireContext())
        language = shared.getLanguage().toString()

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
        if (type == 1) {
            adsManager.showAds(requireActivity()) {
                replace(type, contentLocation)
            }
        }else{
            replace(type,contentLocation)
        }

        adsManager.setOnAdClickListener {
            Toast.makeText(requireContext(), "Thank You !", Toast.LENGTH_SHORT).show()
        }

        adsManager.setOnAdDismissListener {
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
            3->{
                replaceFragment(HistoricalPersonListFragment())
            }
            else->{
                Snackbar.make(binding.contentRecycleView, getString(R.string.contentWarningInfo), Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.CYAN)
                    .setTextColor(Color.BLACK)
                    .show()
            }
        }
    }
}