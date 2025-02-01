package uz.kmax.tarixtest.tools.filter

import uz.kmax.tarixtest.data.main.MenuContentData
import uz.kmax.tarixtest.data.main.MenuTestData

class TypeFilter {
    fun filter(unFilteredList : ArrayList<MenuTestData>, filterType : Int):ArrayList<MenuTestData>{
        var data = ArrayList<MenuTestData>()
        var mData = ArrayList<MenuTestData>()
        mData.addAll(unFilteredList)
        for (i in 0 until mData.size){
            if (mData[i].testType == filterType && mData[i].testVisibility == 1){
                data.add(mData[i])
            }
        }
        return data
    }

    fun filter(unFilteredList2 : ArrayList<MenuContentData>, filterType : Int, type : Int):ArrayList<MenuContentData>{
        val contentData = ArrayList<MenuContentData>()
        val mContentData = ArrayList<MenuContentData>()
        mContentData.addAll(unFilteredList2)
        for (i in 0 until mContentData.size){
            if (mContentData[i].contentType == filterType && mContentData[i].contentVisibility == 1){
                contentData.add(mContentData[i])
            }
        }
        return contentData
    }

    fun filterPosition(data : ArrayList<MenuTestData>):ArrayList<MenuTestData>{
        val nonFilteredData = ArrayList<MenuTestData>()
        nonFilteredData.addAll(data)
        val filteredData = ArrayList<MenuTestData>()

        for (i in 0 until 1) {
            val removedData: ArrayList<MenuTestData> = ArrayList()
            for (j in 0 until data.size) {
                if (nonFilteredData[j].testType == 3) {
                    filteredData.add(data[j])
                } else {
                    removedData.add(data[j])
                }
            }

            val removedData2: ArrayList<MenuTestData> = ArrayList()
            for (b in 0 until removedData.size) {
                if (removedData[b].testType == 2) {
                    filteredData.add(removedData[b])
                } else {
                    removedData2.add(removedData[b])
                }
            }

            val removedData3: ArrayList<MenuTestData> = ArrayList()
            for (k in 0 until removedData2.size) {
                if (removedData2[k].testType == 1) {
                    filteredData.add(removedData2[k])
                } else {
                    removedData3.add(removedData2[k])
                }
            }

            for (n in 0 until removedData3.size) {
                filteredData.add(removedData3[n])
            }
        }
        return filteredData
    }
}