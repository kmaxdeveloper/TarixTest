package uz.kmax.tarixtest.domain.models.content

data class HistoricalPersonListData(
    var personName: String = "", var personYears: String = "",
    var personNewOld: Int = 0, var personLocation: String = "",
    var personVisibility : Int = 0
)