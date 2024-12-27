package uz.kmax.tarixtest.tools.filter

import uz.kmax.tarixtest.data.MenuTestData

class TypeFilter {
    fun filter(unFilteredList : ArrayList<MenuTestData>,filterType : Int):ArrayList<MenuTestData>{
        var data = ArrayList<MenuTestData>()
        var mData = ArrayList<MenuTestData>()
        mData.addAll(unFilteredList)
        for (i in 0 until mData.size){
            if (mData[i].testType == filterType){
                data.add(mData[i])
            }
        }
        return data
    }
}