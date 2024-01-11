package uz.kmax.tarixtest.tools.other

import uz.kmax.tarixtest.data.MenuTestData

class DataFilter {

    fun filter(data : ArrayList<MenuTestData>):ArrayList<MenuTestData>{
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