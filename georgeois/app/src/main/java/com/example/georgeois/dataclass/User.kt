package com.example.georgeois.dataclass

import java.util.Date


data class User(
    val u_idx: Int,
    val u_auth: Char,
    var u_id: String,
    val u_pw: String,
    val u_nm: String,
    val u_nickNm: String,
    val u_pNumber: String,
    val u_gender: Char,
    val u_birth: Int,
    val u_email: String,
    val u_profilePath: String,
    val u_in_ctgy: String,
    val u_out_ctgy: String,
    val u_budget: Int,
    val u_alarm_yn: Boolean,
    val del_yn: Boolean,
    // Timestamp or Date
    val cre_date: Date,
    val cre_user: String,
    val mod_date: Date,
    val mod_user: String,
)
