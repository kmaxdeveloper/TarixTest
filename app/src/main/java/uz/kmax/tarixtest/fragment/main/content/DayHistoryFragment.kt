package uz.kmax.tarixtest.fragment.main.content

import android.icu.text.SimpleDateFormat
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.adapter.DayHistoryAdapter
import uz.kmax.tarixtest.data.main.DayHistoryData
import uz.kmax.tarixtest.databinding.FragmentDayHistoryBinding
import uz.kmax.tarixtest.dialog.DialogDatePicker
import uz.kmax.tarixtest.fragment.main.MenuFragment
import uz.kmax.tarixtest.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.tools.tools.SharedPref
import java.util.Date

class DayHistoryFragment : BaseFragmentWC<FragmentDayHistoryBinding>(FragmentDayHistoryBinding::inflate){
    private var dialog = DialogDatePicker()
    private var adapter = DayHistoryAdapter()
    private var dayHistorySize: Int = 0
    private var month: String = ""
    private var day: String = ""
    private var language = "uz"
    lateinit var sharedPref: SharedPref
    lateinit var firebaseManager: FirebaseManager

    override fun onViewCreated() {
        firebaseManager = FirebaseManager("TarixTest/Content")
        month = SimpleDateFormat("MM").format(Date())
        day = SimpleDateFormat("dd").format(Date())
        sharedPref = SharedPref(requireContext())
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
        firebaseManager.observeList("$language/KunTarixi/$month/$day/kunTarixi", DayHistoryData::class.java){
            if (it != null) {
                dayHistorySize = it.size
                adapter.setItems(it)
            }
        }
    }
}