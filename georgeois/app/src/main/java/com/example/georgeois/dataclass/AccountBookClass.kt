package com.example.georgeois.dataclass

data class InAccountBookClass(
    val i_idx : Int?,
    val u_idx: Int,
    val cre_user: String,
    val u_nicknm: String,
    val i_amount: Int,
    val i_content: String,
    val i_category: String,
    val i_date: String,
    val i_imgpath: String,
    val i_budregi_yn: Int
)
data class OutAccountBookClass(
    val o_idx : Int?,
    val u_idx: Int,
    val cre_user: String,
    val u_nicknm: String,
    val o_amount: Int,
    val o_content: String,
    val o_category: String,
    val o_date : String,
    val o_imgpath: String,
    val o_budregi_yn: Int,
    val o_property: Char
)
data class AccountBookClass(
    var isInorOut : Char,
    var idx : Int,
    var uIdx : Int,
    var creUser: String,
    var uNicknm: String,
    var amount: Int,
    val content: String,
    val category: String,
    val date: String,
    val imgpath: String,
    val budregi_yn: Int,
    val property : Char?
)
data class MonthAccountBookClass(
    var inAmount : Int,
    var outAmount:Int,
    var totalAmount:Int,
    var date: String
)
data class DayAccountBookClass(
    var inAmount : Int,
    var outAmount:Int,
    var day : String
)


data class CategoryAccountBookClass(
    val category: String,
    val amount : Int,
    val count:Int,
    val date: String,
    val inOrOut : Char
)

