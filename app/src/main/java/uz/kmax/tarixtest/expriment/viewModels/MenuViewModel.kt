package uz.kmax.tarixtest.expriment.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.kmax.tarixtest.data.MenuTestData
import uz.kmax.tarixtest.expriment.GetDataFromFirebase
import uz.kmax.tarixtest.tools.filter.TypeFilter

class MenuViewModel : ViewModel() {

    // Ma'lumotlarni saqlash uchun LiveData
    private val _data = MutableLiveData<ArrayList<MenuTestData>>()
    val list: LiveData<ArrayList<MenuTestData>> get() = _data
    var dataTypeFilter = TypeFilter()

    fun getFirebaseData(path: String, type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Firebase reference yaratish
                val reference = FirebaseDatabase.getInstance().getReference(path)
                // Firebase'dan ma'lumotni olish
                val snapshot = GetDataFromFirebase().fetchDataFromFirebase(reference)
                var list = ArrayList<MenuTestData>()
                // Ma'lumotni o'qish va qayta ishlash
                val result = snapshot.getValue(MenuTestData::class.java)
                result.let {
                    if (it?.testVisibility == 1) {
                        Log.d("HELLO", it.testName)
                        list.add(
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
                // UI-ni yangilash uchun ma'lumotni asosiy ipga jo'natish
                _data.postValue(dataTypeFilter.filter(list, type))
            } catch (e: Exception) {
                // Xatolik bo'lsa, LiveData-ga xabarni qaytarish
//                _data.postValue("Error: ${e.message}")
            }
        }
    }
}