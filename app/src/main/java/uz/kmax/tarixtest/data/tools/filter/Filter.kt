package uz.kmax.tarixtest.data.tools.filter

import uz.kmax.tarixtest.domain.models.content.HistoricalPersonListData
import uz.kmax.tarixtest.domain.models.main.BaseBookData
import uz.kmax.tarixtest.domain.models.main.MenuContentData
import uz.kmax.tarixtest.domain.models.main.MenuTestData

class Filter {
    fun filterTest(data : ArrayList<MenuTestData>):ArrayList<MenuTestData>{
        val list = ArrayList<MenuTestData>()
        var newOld = 1
        for (i in 0 until 2){
            for (j in 0 until data.size){
                if (data[j].testNewOld == newOld && data[j].testVisibility == 1){
                    list.add(data[j])
                }
            }
            newOld = 0
        }
        return list
    }

    fun filterContent(data : ArrayList<MenuContentData>):ArrayList<MenuContentData>{
        val list = ArrayList<MenuContentData>()
        var newOld = 1
        for (i in 0 until 2){
            for (j in 0 until data.size){
                if (data[j].contentNewOld == newOld && data[j].contentVisibility == 1){
                    list.add(data[j])
                }
            }
            newOld = 0
        }
        return list
    }

    fun filterBook(data : ArrayList<BaseBookData>):ArrayList<BaseBookData>{
        val list = ArrayList<BaseBookData>()
        var newOld = 1
        for (i in 0 until 2){
            for (j in 0 until data.size){
                if (data[j].bookNewOld == newOld && data[j].bookVisibility == 1){
                    list.add(data[j])
                }
            }
            newOld = 0
        }
        return list
    }

    fun filterHistoricalPerson(data : ArrayList<HistoricalPersonListData>) : ArrayList<HistoricalPersonListData>{
        val list = ArrayList<HistoricalPersonListData>()
        var newOld = 1
        for (i in 0 until 2){
            for (j in 0 until data.size){
                if (data[j].personNewOld == newOld && data[j].personVisibility == 1){
                    list.add(data[j])
                }
            }
            newOld = 0
        }
        return list
    }
}