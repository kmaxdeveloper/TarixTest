package uz.kmax.tarixtest.domain.models.main

data class MenuContentData(
    var contentAnyWay: Int = 0,
    var contentCount: Int = 0,
    var contentLocation: String = "",
    var contentName: String = "",
    var contentNewOld: Int = 0,
    var contentType: Int = 0,
    var contentVisibility: Int = 0
)