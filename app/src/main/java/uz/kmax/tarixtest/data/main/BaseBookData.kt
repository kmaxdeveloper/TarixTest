package uz.kmax.tarixtest.data.main

data class BaseBookData(
    var bookLocation: String = "",
    var bookName: String = "",
    var bookNewOld: Int = 1,
    var bookRelease: String = "",
    var bookSize: String = "",
    var bookTitle: String = "",
    var bookVisibility: Int = 0,
)