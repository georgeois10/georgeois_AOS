package com.example.georgeois.service

import com.example.georgeois.dataclass.JoinUser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface UserApiService {

    @GET("checkUserIdDuplication.php")
    fun checkIdDuplication(@Query("id") id: String) : Call<Map<String, Boolean>>

    @GET("checkUserNickNmDuplication.php")
    fun checkNickNmDuplication(@Query("nickNm") nickNm: String) : Call<Map<String, Boolean>>

    @FormUrlEncoded
    @POST("login.php")
    fun login(@Field("id") id: String, @Field("pw") pw: String) : Call<Map<String, Map<String, Any>>>

    @POST("joinUser.php")
    fun join(@Body joinUser: JoinUser) : Call<ResponseBody>

//    @FormUrlEncoded
//    @POST("joinUser.php")
//    fun join(@FieldMap joinUser: Map<String, String>) : Call<ResponseBody>

}