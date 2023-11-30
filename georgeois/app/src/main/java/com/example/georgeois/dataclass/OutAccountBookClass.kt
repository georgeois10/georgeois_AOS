package com.example.georgeois.dataclass

data class OutAccountBookClass(
    val uIdx: Int,
    val uId: String,
    val uNicknm: String,
    val oAmount: Int,
    val oContent: String,
    val oCategory: String,
    val oDate : String,
    val oImgpath: String,
    val oBudregiYn: Boolean,
    val oProperty: Char
)
