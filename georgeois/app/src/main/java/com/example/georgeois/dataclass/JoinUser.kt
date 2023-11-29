package com.example.georgeois.dataclass

import com.google.gson.annotations.SerializedName


data class JoinUser(
//    @SerializedName("auth")
    val auth: String,
//    @SerializedName("profile")
    val profile: String,
//    @SerializedName("id")
    val id: String,
//    @SerializedName("pw")
    val pw: String,
//    @SerializedName("nm")
    val nm: String,
//    @SerializedName("nicknm")
    val nicknm: String,
//    @SerializedName("pnumber")
    val pnumber: String,
//    @SerializedName("gender")
    val gender: String,
//    @SerializedName("birth")
    val birth: Int,
//    @SerializedName("email")
    val email: String
)
