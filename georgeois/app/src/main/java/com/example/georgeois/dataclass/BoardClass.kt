package com.example.georgeois.dataclass


data class BoardClass(
    val b_idx : Int?,
    val u_idx : Int,
    val b_title : String,
    val u_nicknm : String,
    val b_content : String,
    val b_imgpath : String,
    // 조회수
    val b_hits : Int,
    // 댓글 수
    val b_comm_cnt : Int,
    // 추천 수
    val b_reco_cnt : Int,
    // 신고 수
    val b_noti_cnt : Int,
    val b_date : String,
    val cre_user : String,
)