package uz.kmax.tarixtest.fragment.main

import android.icu.text.SimpleDateFormat
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.adapter.DayHistoryAdapter
import uz.kmax.tarixtest.data.DayHistoryData
import uz.kmax.tarixtest.databinding.FragmentDayHistoryBinding
import uz.kmax.tarixtest.dialog.DialogDatePicker
import java.util.Date

class DayHistoryFragment : BaseFragmentWC<FragmentDayHistoryBinding>(FragmentDayHistoryBinding::inflate){
    private var dialog = DialogDatePicker()
    private var adapter = DayHistoryAdapter()
    private val db = Firebase.database
    private var dayHistorySize: Int = 0
    private var month: String = ""
    private var day: String = ""
    private var language = "uz"

    override fun onViewCreated() {

        month = SimpleDateFormat("MM").format(Date())
        day = SimpleDateFormat("dd").format(Date())

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
        val dayList = ArrayList<DayHistoryData>()
        db.getReference("TarixTest/KunTarixi").child(language).child(month).child(day)
            .child("kunTarixi").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEachIndexed { _, data ->
                        val h = data.getValue(DayHistoryData::class.java)
                        h?.let {
                            dayList.add(DayHistoryData(story = it.story))
                        }
                    }
                    // ad
                    dayHistorySize = dayList.size
                    adapter.setData(dayList)
                }

                override fun onCancelled(error: DatabaseError) {
                    dayHistorySize = 0
                    startMainFragment(MenuFragment())
                }
            })

    }
}