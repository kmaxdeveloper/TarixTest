package uz.kmax.tarixtest.presentation.ui.fragment.main.content

import android.icu.text.SimpleDateFormat
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.data.adapter.DayHistoryAdapter
import uz.kmax.tarixtest.data.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.domain.models.main.DayHistoryData
import uz.kmax.tarixtest.databinding.FragmentDayHistoryBinding
import uz.kmax.tarixtest.presentation.ui.dialog.DialogDatePicker
import uz.kmax.tarixtest.presentation.ui.fragment.main.MenuFragment
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class DayHistoryFragment : BaseFragmentWC<FragmentDayHistoryBinding>(FragmentDayHistoryBinding::inflate){
    private var dialog = DialogDatePicker()
    private var adapter = DayHistoryAdapter()
    private var dayHistorySize: Int = 0
    private var month: String = ""
    private var day: String = ""
    private var language = "uz"
    private lateinit var firebaseManager: FirebaseManager

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        month = SimpleDateFormat("MM").format(Date())
        day = SimpleDateFormat("dd").format(Date())
        language = sharedPref.getLanguage().toString()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        getData()

        binding.back.setOnClickListener {
            startMainFragment(MenuFragment())
        }

        dialog.setOnDataPickerDateClickListener { monthM, dayM ->
            month = monthM
            day = dayM
            Toast.makeText(requireContext(), "$dayM/$monthM/2024", Toast.LENGTH_SHORT).show()
            getData()
        }

        binding.setDateBtn.setOnClickListener {
            dialog.show(requireActivity())
        }

    }

    private fun getData(){
        firebaseManager.observeList("Content/$language/KunTarixi/$month/$day/kunTarixi", DayHistoryData::class.java){
            if (it != null) {
                dayHistorySize = it.size
                adapter.setItems(it)
            }
        }
    }
}