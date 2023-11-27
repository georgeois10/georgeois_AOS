package com.example.georgeois.service

import com.example.georgeois.dataclass.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface UserApiService {

    @GET("checkUserIdDuplication.php")
    fun checkIdDuplication(@Query("id") id: String) : Call<Map<String, Boolean>>

    @GET("checkUserNickNmDuplication.php")
    fun checkNickNmDuplication(@Query("nickNm") nickNm: String) : Call<Map<String, Boolean>>

    @GET("checkUserIdDuplication.php")
    fun test() : Call<Map<Any, Any>>

    @POST("join.php")
    fun joinUser(@Body user: User) : Response<Int>
}