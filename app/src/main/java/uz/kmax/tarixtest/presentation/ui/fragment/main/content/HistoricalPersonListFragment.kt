package uz.kmax.tarixtest.presentation.ui.fragment.main.content

import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.adapter.HistoricalPersonListAdapter
import uz.kmax.tarixtest.data.ads.AdsManager
import uz.kmax.tarixtest.data.tools.filter.Filter
import uz.kmax.tarixtest.data.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.data.tools.tools.onFragmentBackPressed
import uz.kmax.tarixtest.databinding.FragmentHistoricalPersonListBinding
import uz.kmax.tarixtest.domain.models.content.HistoricalPersonListData
import uz.kmax.tarixtest.presentation.ui.fragment.main.MenuFragment
import javax.inject.Inject

@AndroidEntryPoint
class HistoricalPersonListFragment : BaseFragmentWC<FragmentHistoricalPersonListBinding>(FragmentHistoricalPersonListBinding::inflate) {
    private var adapter = HistoricalPersonListAdapter()
    private lateinit var firebaseManager : FirebaseManager
    private var dataFilter = Filter()
    private var language = ""
    private var personPath : String = ""

    @Inject
    lateinit var sharedPref: SharedPref

    @Inject
    lateinit var adsManager: AdsManager

    override fun onViewCreated() {
        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.appTheme)

        adsManager.init()

        adsManager.setOnAdDismissListener {
            startMainFragment(HistoricalPersonFragment(personPath))
        }

        firebaseManager = FirebaseManager()
        language = sharedPref.getLanguage().toString()

        onFragmentBackPressed {
            startMainFragment(MenuFragment())
        }

        binding.back.setOnClickListener {
            startMainFragment(MenuFragment())
        }

        getHistoricalPersonListData()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        adapter.setOnItemSendListener {path->
            personPath = path.personLocation
            adsManager.showAds(requireActivity()){
                startMainFragment(HistoricalPersonFragment(path.personLocation))
            }
        }

    }

    private fun getHistoricalPersonListData() {
        firebaseManager.observeList("Content/$language/HistoricalPersonList", HistoricalPersonListData::class.java){
            if (it != null){
                adapter.setItems(dataFilter.filterHistoricalPerson(it))
            }
        }
    }
}