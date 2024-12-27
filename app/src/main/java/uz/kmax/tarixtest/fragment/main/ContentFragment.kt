package uz.kmax.tarixtest.fragment.main

import android.icu.text.SimpleDateFormat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.adapter.MenuAdapter
import uz.kmax.tarixtest.data.DayHistoryData
import uz.kmax.tarixtest.data.MenuTestData
import uz.kmax.tarixtest.databinding.FragmentContentBinding
import uz.kmax.tarixtest.fragment.main.content.DayHistoryFragment
import uz.kmax.tarixtest.tools.filter.TypeFilter
import uz.kmax.tarixtest.tools.other.SharedPref
import java.util.Date

class ContentFragment : BaseFragmentWC<FragmentContentBinding>(FragmentContentBinding::inflate) {
    val adapter by lazy { MenuAdapter() }
    private val db = Firebase.database
    private var date: String = ""
    var filter = TypeFilter()
    lateinit var shared : SharedPref
    private var language = "uz"

    override fun onViewCreated() {
        val currentDayDate: String = SimpleDateFormat("dd").format(Date())
        val currentMonthDate: String = SimpleDateFormat("MM").format(Date())
        val currentYearDate: String = SimpleDateFormat("yyyy").format(Date())
        date = "${currentDayDate}.${currentMonthDate}.${currentYearDate}"
        shared = SharedPref(requireContext())
        language = shared.getLanguage().toString()

        addTestData()
        binding.contentRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.contentRecycleView.adapter = adapter

        adapter.setOnTypeClickListener { type, location ->
            when(type){
                1->{
                    replaceFragment(DayHistoryFragment())
                }
            }
        }
    }

    private fun addTestData() {
        val menuTestDataArray = ArrayList<MenuTestData>()
        db.getReference("TarixTest").child("AllTest").child(language)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEachIndexed { _, data ->
                        val h = data.getValue(MenuTestData::class.java)
                        h?.let {
                            if (it.testVisibility == 1) {
                                menuTestDataArray.add(
                                    MenuTestData(
                                        it.testAnyWay,
                                        it.testCount,
                                        it.testLocation,
                                        it.testName,
                                        it.testNewOld,
                                        it.testType,
                                        it.testVisibility
                                    )
                                )
                            }
                        }
                    }
                    adapter.setData(filter.filter(menuTestDataArray, 1))
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}