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
import uz.kmax.tarixtest.databinding.FragmentDayOfHistoryBinding
import uz.kmax.tarixtest.dialog.DialogDatePicker
import java.util.Date

class DayHistoryFragment : BaseFragmentWC<FragmentDayOfHistoryBinding>(FragmentDayOfHistoryBinding::inflate){

    private var dialog = DialogDatePicker()
    private var adapter = DayHistoryAdapter()
    private val db = Firebase.database
    private var dayHistorySize: Int = 0
    private var month: String = ""
    private var day: String = ""

    override fun onViewCreated() {

        var currentDayDate = SimpleDateFormat("dd")
        var currentMonthDate = SimpleDateFormat("MM")

        month = currentMonthDate.format(Date())
        day = currentDayDate.format(Date())

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        getData()

        binding.back.setOnClickListener {
            startMainFragment(uz.kmax.tarixtest.fragment.main.MenuFragment())
        }

        dialog.setOnDateListener { monthX, dayX, yearX ->

            if (yearX == 1970){
                month = currentMonthDate.format(Date())
                day = currentDayDate.format(Date())
            }else {
                val dayString = dayX.toString()
                val monthString = monthX.toString()
                val yearString = yearX.toString()

                month = if (monthString.length <= 1) {
                    "0$monthString"
                } else {
                    monthString
                }

                day = if (dayString.length <= 1) {
                    "0$dayString"
                } else {
                    dayString
                }
            }

            getData()
        }

        binding.setDateBtn.setOnClickListener {
            dialog.show(requireContext())
            Toast.makeText(requireContext(), "${day}.${month}.2023", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getData(){
        val dayList = ArrayList<DayHistoryData>()
        db.getReference("TarixTest").child("KunTarixi").child(month).child(day)
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
                    startMainFragment(uz.kmax.tarixtest.fragment.main.MenuFragment())
                }
            })

    }
}